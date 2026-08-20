package org.example.notification.domain.repository

import org.example.notification.domain.model.Message

interface MessagePublishRepository {
    fun publish(message: Message)
}