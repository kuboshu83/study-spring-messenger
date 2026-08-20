package org.example.notification.domain.service

import org.example.notification.domain.model.Message
import org.springframework.stereotype.Component

interface MessageNotificator {
    fun notify(message: Message)
}

@Component
class ConsoleMessageNotificator : MessageNotificator {
    override fun notify(message: Message) {
        println(message)
    }
}