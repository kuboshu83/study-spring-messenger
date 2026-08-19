package org.example.notification.publisher.domain.repository

import org.example.notification.common.domain.model.PublishMessage

interface MessageRepository {
    fun publish(message: PublishMessage)
}