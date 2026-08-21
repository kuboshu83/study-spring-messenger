package org.example.notification.infrastructure

import org.example.manager.domain.model.RecipientEmailAddress
import org.example.notification.domain.model.Message
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.model.UniqueMessageDestinationCollection
import org.example.notification.domain.service.MessagePublisher
import org.example.notification.domain.service.MessageReceiveHandler
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

data class MessagePayloadDTO(val title: String, val body: String, val destinations: List<String>) {
    companion object {
        fun fromDomain(message: Message): MessagePayloadDTO {
            return MessagePayloadDTO(
                message.title.value,
                message.body.value,
                message.destinations.toSet().map { it.value }
            )
        }
    }

    fun toDomain(): Message {
        val addresses = destinations.map { RecipientEmailAddress(it) }
        return Message(MessageTitle(title), MessageBody(body), UniqueMessageDestinationCollection.fromList(addresses))
    }
}

@Component
class RabbitMqMessagePublisher(
    private val template: RabbitTemplate,
    private val objectMapper: ObjectMapper,
    @param:Qualifier("notificationExchange") private val direct: DirectExchange
) :
    MessagePublisher {
    override fun publish(message: Message) {
        val dto = MessagePayloadDTO.fromDomain(message)
        val message = objectMapper.writeValueAsString(dto)

        // TODO: RabbitMQへ送信中にエラーが発生したらリトライする(リトライは呼び出し側で行うようにする)
        // TODO: リトライ回数はパラメータで設定できるようにする。
        template.convertAndSend(direct.name, "", message)
    }
}

@Component
@RabbitListener(queues = ["q.notification"])
class RabbitMqMessageReceiver(
    private val messageReceiveHandler: MessageReceiveHandler,
    private val objectMapper: ObjectMapper
) {
    @RabbitHandler
    fun receive(message: String) {
        // TODO: メッセージの復元エラー(IllegalArgumentException)は即DLQ行き
        val messageDTO = objectMapper.readValue<MessagePayloadDTO>(message)
        val message = messageDTO.toDomain()

        // TODO: ハンドラー(主に通知)エラーはリトライしてからDLQ行き
        // TODO: リトライ回数はパラメータで渡せるようにする
        messageReceiveHandler.handle(message)
    }
}

@Configuration
class RabbitMqConfig {
    @Bean
    fun notificationExchange(): DirectExchange {
        return DirectExchange("ex.notification")
    }

    @Bean
    fun notificationQueue(): Queue {
        return QueueBuilder.durable("q.notification").quorum().build()
    }

    @Bean
    fun binding(
        @Qualifier("notificationExchange") direct: DirectExchange,
        @Qualifier("notificationQueue") queue: Queue
    ): Binding {
        return BindingBuilder.bind(queue).to(direct).with("")
    }

    // TODO: Dead-Letter-Queueの設定
}