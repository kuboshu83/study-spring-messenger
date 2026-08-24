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
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.subject = message.title.value
        val destinations = message.destinations.toSet().map { it.value }
        simpleMailMessage.setTo(*destinations.toTypedArray())
        simpleMailMessage.from = from
        simpleMailMessage.text = message.body.value
        mailSender.send(simpleMailMessage)
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

//@Component
//class ConsoleMessageNotificator : MessageNotificator {
//    override fun notify(message: Message) {
//        println(message)
//    }
//}