package org.example.notification.domain.model

import org.example.domain.errors.EmptyException
import org.example.domain.errors.InvalidSizeException
import org.example.domain.model.RecipientEmailAddress

data class MessageTitle(val value: String) {
    companion object {
        private const val MAX_SIZE = 20
        private const val MIN_SIZE = 1
    }

    init {
        val size = value.length
        if (size !in MIN_SIZE..MAX_SIZE) {
            throw InvalidSizeException(" message title size is out of range: minLimit=$MIN_SIZE, maxLimit=$MAX_SIZE, actual=$size")
        }
    }
}

data class MessageBody(val value: String) {
    companion object {
        private const val MAX_SIZE = 100
        private const val MIN_SIZE = 1
    }

    init {
        val size = value.length
        if (size !in MIN_SIZE..MAX_SIZE) {
            throw InvalidSizeException("message body size is out of range: minLimit=$MIN_SIZE, maxLimit=$MAX_SIZE, actual=$size")
        }
    }
}

class MessageDestinations(private val addresses: Set<RecipientEmailAddress>) {
    companion object {
        fun fromList(addresses: List<RecipientEmailAddress>): MessageDestinations {
            return MessageDestinations(addresses.toSet())
        }
    }

    fun toSet(): Set<RecipientEmailAddress> = addresses

    fun isEmpty(): Boolean = addresses.isEmpty()
}

data class PublishMessage(
    val title: MessageTitle,
    val body: MessageBody,
    val destinations: MessageDestinations
) {
    init {
        if (destinations.isEmpty()) {
            throw EmptyException("message destination is empty")
        }
    }
}

