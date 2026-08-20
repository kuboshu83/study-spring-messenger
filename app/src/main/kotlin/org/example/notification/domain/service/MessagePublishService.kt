package org.example.notification.domain.service

import org.example.manager.domain.model.ApplicationId
import org.example.manager.domain.service.RecipientSearchService
import org.example.notification.domain.model.Message
import org.example.notification.domain.model.MessageBody
import org.example.notification.domain.model.MessageDestinations
import org.example.notification.domain.model.MessageTitle
import org.example.notification.domain.repository.MessagePublisher
import org.springframework.stereotype.Service

@Service
class MessagePublishService(
    private val messagePublisher: MessagePublisher,
    private val recipientsQueryService: RecipientSearchService
) {
    fun publishMessage(applicationId: ApplicationId, title: MessageTitle, body: MessageBody) {
        val recipients = recipientsQueryService.findRecipientsByApplicationId(applicationId)
        val destinations = MessageDestinations.fromList(recipients.map { it.email })
        val message = Message(title, body, destinations)
        messagePublisher.publish(message)
    }
}