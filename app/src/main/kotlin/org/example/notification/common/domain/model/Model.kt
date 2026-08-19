package org.example.notification.common.domain.model

import org.example.domain.errors.EmptyException
import org.example.domain.errors.InvalidSizeException
import org.example.domain.model.Recipient
import org.example.domain.model.RecipientId

data class NotificationTitle(val value: String) {
    companion object {
        private const val MAX_SIZE = 20
        private const val MIN_SIZE = 1
    }

    init {
        val size = value.length
        if (size !in MIN_SIZE..MAX_SIZE) {
            throw InvalidSizeException(" notification title size is out of range: minLimit=$MIN_SIZE, maxLimit=$MAX_SIZE, actual=$size")
        }
    }
}

data class NotificationBody(val value: String) {
    companion object {
        private const val MAX_SIZE = 100
        private const val MIN_SIZE = 1
    }

    init {
        val size = value.length
        if (size !in MIN_SIZE..MAX_SIZE) {
            throw InvalidSizeException("notification body size is out of range: minLimit=$MIN_SIZE, maxLimit=$MAX_SIZE, actual=$size")
        }
    }
}

class NotificationRecipients private constructor(val recipients: List<Recipient>) {
    companion object {
        fun fromList(recipients: List<Recipient>): NotificationRecipients {
            val recipientsMap = mutableMapOf<RecipientId, Recipient>()
            for (recipient in recipients) {
                if (!recipientsMap.containsKey(recipient.id)) {
                    recipientsMap[recipient.id] = recipient
                }
            }
            return NotificationRecipients(recipientsMap.values.toList())
        }
    }

    fun toList(): List<Recipient> {
        return recipients
    }

    fun isEmpty(): Boolean = recipients.isEmpty()
}

data class NotificationMessage(
    val title: NotificationTitle,
    val body: NotificationBody,
    val recipients: NotificationRecipients
) {
    init {
        if (recipients.isEmpty()) {
            throw EmptyException("notification destination is empty")
        }
    }
}

