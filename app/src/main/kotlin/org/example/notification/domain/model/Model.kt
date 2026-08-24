package org.example.notification.domain.model

import org.example.manager.domain.model.RecipientEmailAddress

private fun String.ellipsize(length: Int = 50): String {
    return if (this.length > length) {
        "${this.substring(0, length)}..."
    } else {
        this
    }
}

data class MessageTitle(val value: String) {
    companion object {
        private const val MAX_LENGTH = 20
        private const val MIN_LENGTH = 1

        private fun validate(title: String) {
            if (title.isBlank()) {
                throw IllegalArgumentException("title cannot be blank")
            }

            val length = title.length
            if (length !in MIN_LENGTH..MAX_LENGTH) {
                throw IllegalArgumentException("title length must be between $MIN_LENGTH and $MAX_LENGTH: length=$length, title=${title.ellipsize()}")
            }
        }
    }

    init {
        validate(value)
    }
}

data class MessageBody(val value: String) {
    companion object {
        private const val MAX_SIZE = 100
        private const val MIN_SIZE = 1

        private fun validate(body: String) {
            if (body.isBlank()) {
                throw IllegalArgumentException("body cannot be blank")
            }

            val length = body.length
            if (length !in MIN_SIZE..MAX_SIZE) {
                throw IllegalArgumentException("body length must be between $MIN_SIZE and $MAX_SIZE: length=$length, body=${body.ellipsize()}")
            }
        }
    }

    init {
        validate(value)
    }
}

class UniqueMessageDestinationCollection(private val addresses: Set<RecipientEmailAddress>) {
    companion object {
        fun fromList(addresses: List<RecipientEmailAddress>): UniqueMessageDestinationCollection {
            return UniqueMessageDestinationCollection(addresses.toSet())
        }
    }

    fun toSet(): Set<RecipientEmailAddress> = addresses
    fun isEmpty(): Boolean = addresses.isEmpty()
}

data class Message(
    val title: MessageTitle,
    val body: MessageBody,
    val destinations: UniqueMessageDestinationCollection
) {
    companion object {
        private fun validate(destinations: UniqueMessageDestinationCollection) {
            if (destinations.isEmpty()) {
                throw IllegalArgumentException("destinations cannot be empty")
            }
        }
    }

    init {
        validate(destinations)
    }
}

enum class AllowedSenderDomain(val value: String) {
    EXAMPLE_ORG("example.org"),
    EXAMPLE_COM("example.com");
}

data class SenderEmailAddress(val value: String) {
    companion object {
        private fun validate(address: String) {
            for (entry in AllowedSenderDomain.entries) {
                if (address.endsWith("@${entry.value}")) {
                    return
                }
            }
            throw IllegalArgumentException("not allowed address domain: address=$address")
        }
    }

    init {
        validate(value)
    }
}

data class MailProperties(val mailFrom: SenderEmailAddress)