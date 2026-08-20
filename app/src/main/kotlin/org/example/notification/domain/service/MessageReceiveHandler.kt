package org.example.notification.domain.service

import org.example.notification.domain.model.Message
import org.springframework.stereotype.Component

@Component
class MessageReceiveHandler(private val messageNotificator: MessageNotificator) {
    fun handle(message: Message) {
        messageNotificator.notify(message)
    }
}