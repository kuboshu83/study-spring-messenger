package org.example.notification.publisher.domain.repository

import org.example.notification.common.domain.model.NotificationMessage

interface MessageRepository {
    fun publish(message: NotificationMessage)
}