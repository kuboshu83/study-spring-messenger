package org.example.notification.domain.service

import org.example.domain.model.ApplicationId
import org.example.domain.service.RecipientSearchService
import org.example.notification.domain.model.Message
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageDestinations
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.repository.MessagePublishRepository
import org.springframework.stereotype.Service

@Service
class MessagePublishService(
    private val messageRepository: MessagePublishRepository,
    private val recipientSearchService: RecipientSearchService
) {
    fun publishMessage(applicationId: ApplicationId, title: MessageTitle, body: MessageBody) {
        val recipients = recipientSearchService.findRecipientsByApplicationId(applicationId)
        val destinations = MessageDestinations.fromList(recipients.map { it.email })
        val message = Message(title, body, destinations)
        messageRepository.publish(message)
    }
}