package org.example.notification.publisher.infrastructure

import org.example.notification.common.domain.model.PublishMessage
import org.example.notification.publisher.domain.repository.MessageRepository
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository
import tools.jackson.module.kotlin.jacksonObjectMapper


@Repository
class RabbitMqMessageRepository(private val template: RabbitTemplate, private val queue: Queue) : MessageRepository {
    override fun publish(message: PublishMessage) {
        val dto = PublishMessageDTO.fromDomain(message)
        val message = jacksonObjectMapper().writeValueAsString(dto)
        template.convertAndSend(queue.name, message)
    }
}

data class PublishMessageDTO(val title: String, val body: String, val destinations: List<String>) {
    companion object {
        fun fromDomain(message: PublishMessage): PublishMessageDTO {
            return PublishMessageDTO(
                message.title.value,
                message.body.value,
                message.destinations.toSet().map { it.value }
            )
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