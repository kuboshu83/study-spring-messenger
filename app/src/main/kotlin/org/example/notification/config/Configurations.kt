package org.example.notification.config

import org.example.notification.domain.model.MailProperties
import org.example.notification.domain.model.SenderEmailAddress
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "messenger.mail")
class MailConfigurations(private val from: String) {
    fun toProperties(): MailProperties {
        return MailProperties(
            SenderEmailAddress(from)
        )
    }
}
