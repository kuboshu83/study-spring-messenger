package org.example.notification.config

import org.example.notification.domain.model.MailProperties
import org.example.notification.domain.model.SenderEmailAddress
import org.example.notification.domain.service.MailMessageNotificator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@ConfigurationProperties(prefix = "messenger.mail")
class MailConfigurations(private val from: String) {
    fun toProperties(): MailProperties {
        return MailProperties(
            SenderEmailAddress(from)
        )
    }
}

@Configuration
class MailConfig {
    @Bean
    fun mailMessageNotificator(
        mailConfigurations: MailConfigurations,
        javaMailSender: JavaMailSender
    ): MailMessageNotificator {
        val properties = mailConfigurations.toProperties()
        return MailMessageNotificator(properties, javaMailSender)
    }
}