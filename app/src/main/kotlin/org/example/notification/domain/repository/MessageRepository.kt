package org.example.notification.domain.repository

import org.example.notification.domain.model.PublishMessage

interface MessageRepository {
    fun publish(message: PublishMessage)
}