package org.example.notification.domain.service

import org.example.notification.config.MailConfigurations
import org.example.notification.domain.model.MailProperties
import org.example.notification.domain.model.Message
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

interface MessageNotificator {
    fun notify(message: Message)
}

@Component
class MailMessageNotificator(mailConfigurations: MailConfigurations, private val mailSender: JavaMailSender) :
    MessageNotificator {

    private val properties: MailProperties = mailConfigurations.toProperties()

    override fun notify(message: Message) {
        val destinations = message.destinations.toSet().map { it.value }.toTypedArray()

        val mail = SimpleMailMessage().also { m ->
            m.subject = message.title.value
            m.setTo(*destinations)
            m.from = properties.mailFrom.value
            m.text = message.body.value
        }

        mailSender.send(mail)
    }
}