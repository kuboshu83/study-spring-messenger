package org.example.notification.infrastructure

import org.example.notification.domain.model.Message
import org.example.notification.domain.repository.MessagePublishRepository
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository
import tools.jackson.module.kotlin.jacksonObjectMapper

data class PublishMessageDTO(val title: String, val body: String, val destinations: List<String>) {
    companion object {
        fun fromDomain(message: Message): PublishMessageDTO {
            return PublishMessageDTO(
                message.title.value,
                message.body.value,
                message.destinations.toSet().map { it.value }
            )
        }
    }
}

@Repository
class RabbitMqMessageRepository(private val template: RabbitTemplate, private val queue: Queue) :
    MessagePublishRepository {
    override fun publish(message: Message) {
        val dto = PublishMessageDTO.fromDomain(message)
        val message = jacksonObjectMapper().writeValueAsString(dto)
        template.convertAndSend(queue.name, message)
    }
}

@Configuration
class RabbitMqConfig {
    @Bean
    fun queue(): Queue {
        return QueueBuilder.durable("q.notification").quorum().build()
    }
}