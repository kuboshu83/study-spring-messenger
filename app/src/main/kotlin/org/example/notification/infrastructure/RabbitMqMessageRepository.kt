package org.example.notification.infrastructure

import org.example.manager.domain.model.RecipientEmailAddress
import org.example.notification.domain.model.Message
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.model.UniqueMessageDestinationCollection
import org.example.notification.domain.repository.MessagePublisher
import org.example.notification.domain.service.MessageReceiveHandler
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

data class MessageDTO(val title: String, val body: String, val destinations: List<String>) {
    companion object {
        fun fromDomain(message: Message): MessageDTO {
            return MessageDTO(
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
class RabbitMqMessageRepository(private val template: RabbitTemplate, private val queue: Queue) :
    MessagePublisher {
    override fun publish(message: Message) {
        val dto = MessageDTO.fromDomain(message)
        val message = jacksonObjectMapper().writeValueAsString(dto)
        template.convertAndSend(queue.name, message)
    }
}

@Component
@RabbitListener(queues = ["q.notification"])
class RabbitMqMessageReceiver(private val messageReceiveHandler: MessageReceiveHandler) {
    @RabbitHandler
    fun receive(message: String) {
        val messageDTO = jacksonObjectMapper().readValue<MessageDTO>(message)
        val message = messageDTO.toDomain()
        messageReceiveHandler.handle(message)
    }
}

@Configuration
class RabbitMqConfig {
    @Bean
    fun queue(): Queue {
        return QueueBuilder.durable("q.notification").quorum().build()
    }
}