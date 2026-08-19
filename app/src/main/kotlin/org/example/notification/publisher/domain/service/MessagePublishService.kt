package org.example.notification.publisher.domain.service

import org.example.domain.model.ApplicationId
import org.example.domain.service.RecipientSearchService
import org.example.notification.common.domain.model.MessageBody
import org.example.notification.common.domain.model.MessageDestinations
import org.example.notification.common.domain.model.MessageTitle
import org.example.notification.common.domain.model.PublishMessage
import org.example.notification.publisher.domain.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessagePublishService(
    private val messageRepository: MessageRepository,
    private val recipientSearchService: RecipientSearchService
) {
    fun publishMessage(applicationId: ApplicationId, title: MessageTitle, body: MessageBody) {
        val recipients = recipientSearchService.findRecipientsByApplicationId(applicationId)
        val destinations = MessageDestinations.fromList(recipients.map { it.email })
        val message = PublishMessage(title, body, destinations)
        messageRepository.publish(message)
    }
}