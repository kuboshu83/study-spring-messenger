package org.example.notification.publisher.infrastructure

import org.example.notification.common.domain.model.NotificationMessage
import org.example.notification.publisher.domain.repository.MessageRepository
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository
import tools.jackson.module.kotlin.jacksonObjectMapper


@Repository
class RabbitMqMessageRepository(private val template: RabbitTemplate, private val queue: Queue) :
    MessageRepository {
    override fun publish(message: NotificationMessage) {
        val dto = NotificationMessageDTO.fromDomain(message)
        val message = jacksonObjectMapper().writeValueAsString(dto)
        template.convertAndSend(queue.name, message)
    }
}

data class NotificationMessageDTO(val title: String, val body: String, val recipientAddresses: List<String>) {
    companion object {
        fun fromDomain(message: NotificationMessage): NotificationMessageDTO {
            return NotificationMessageDTO(
                message.title.value,
                message.body.value,
                message.recipients.toList().map { it.email.value })
        }
    }
}

@Configuration
class RabbitMqConfig {
    @Bean
    fun queue(): Queue {
        return QueueBuilder.durable("q.notification").quorum().build()
    }
}