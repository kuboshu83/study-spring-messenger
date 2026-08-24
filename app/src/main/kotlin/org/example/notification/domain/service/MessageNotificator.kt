package org.example.notification.domain.service

import org.example.notification.domain.model.Message
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

interface MessageNotificator {
    fun notify(message: Message)
}

class MailMessageNotificator(private val from: String, private val mailSender: JavaMailSender) : MessageNotificator {
    override fun notify(message: Message) {
        val destinations = message.destinations.toSet().map { it.value }.toTypedArray()

        val mail = SimpleMailMessage().also { m ->
            m.subject = message.title.value
            m.setTo(*destinations)
            m.from = from
            m.text = message.body.value
        }

        mailSender.send(mail)
    }
}

@Configuration
class NotificationConfig {
    @Bean
    fun mailMessageNotificator(mailSender: JavaMailSender): MessageNotificator {
        val from = "from@example.org"
        return MailMessageNotificator(from, mailSender)
    }
}
