package org.example.notification.domain.repository

import org.example.notification.domain.model.Message

interface MessagePublisher {
    fun publish(message: Message)
}