package org.example.notification.infrastructure

import org.example.manager.domain.model.RecipientEmailAddress
import org.example.notification.domain.model.Message
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.model.UniqueMessageDestinationCollection
import org.example.notification.domain.service.MessagePublisher
import org.example.notification.domain.service.MessageReceiveHandler
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
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

@Repository
class RabbitMqMessagePublisher(private val template: RabbitTemplate, private val queue: Queue) :
    MessagePublisher {
    override fun publish(message: Message) {
        val dto = MessagePayloadDTO.fromDomain(message)
        val message = jacksonObjectMapper().writeValueAsString(dto)

        // TODO: RabbitMQへ送信中にエラーが発生したらリトライする(リトライは呼び出し側で行うようにする)
        // TODO: リトライ回数はパラメータで設定できるようにする。
        template.convertAndSend(queue.name, message)
    }
}

@Component
@RabbitListener(queues = ["q.notification"])
class RabbitMqMessageReceiver(
    private val messageReceiveHandler: MessageReceiveHandler,
    private val objectMapper: ObjectMapper
) {
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
    fun queue(): Queue {
        return QueueBuilder.durable("q.notification").quorum().build()
    }

    // TODO: Dead-Letter-Queueの設定
}