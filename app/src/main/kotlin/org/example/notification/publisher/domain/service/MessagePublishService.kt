package org.example.notification.publisher.domain.service

import org.example.domain.model.ApplicationId
import org.example.domain.service.RecipientSearchService
import org.example.notification.common.domain.model.NotificationBody
import org.example.notification.common.domain.model.NotificationMessage
import org.example.notification.common.domain.model.NotificationRecipients
import org.example.notification.common.domain.model.NotificationTitle
import org.example.notification.publisher.domain.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessagePublishService(
    private val messageRepository: MessageRepository,
    private val recipientSearchService: RecipientSearchService
) {
    fun publishMessage(applicationId: ApplicationId, title: NotificationTitle, body: NotificationBody) {
        val recipients = recipientSearchService.findRecipientsByApplicationId(applicationId)
        val notificationRecipients = NotificationRecipients.fromList(recipients)
        val notification = NotificationMessage(title, body, notificationRecipients)
        messageRepository.publish(notification)
    }
}