package org.example.manager.domain.model

import org.example.manager.domain.errors.InvalidFormatException
import org.example.manager.domain.errors.NotAllowedDomainException
import org.example.manager.domain.errors.TooLongException

@ConsistentCopyVisibility
data class RecipientId private constructor(private val id: Id) {
    companion object {
        fun fromString(text: String): RecipientId {
            return RecipientId(Id.fromString(text))
        }

        fun createRandom(): RecipientId {
            return RecipientId(Id.createRandom())
        }
    }

    val value: String
        get() = id.value
}

data class RecipientName(val value: String) {
    companion object {
        private val VALID_PATTERN = Regex("""^[a-z]+-?[a-z]+$""")
        private const val MAX_SIZE = 20

        fun validate(name: String) {
            if (name.length > MAX_SIZE) {
                throw TooLongException("recipient name is too long: limitSize=$MAX_SIZE, actual=${name.length}")
            }
            if (!VALID_PATTERN.matches(name)) {
                throw InvalidFormatException("recipient name format is invalid: name='$name'")
            }
        }
    }

    init {
        validate(value)
    }
}

enum class AllowedRecipientDomain(val value: String) {
    EXAMPLE_ORG("example.org"),
    EXAMPLE_COM("example.com");
}

data class RecipientEmailAddress(val value: String) {
    companion object {
        fun validate(address: String) {
            for (entry in AllowedRecipientDomain.entries) {
                if (address.endsWith(entry.value)) {
                    return
                }
            }
            throw NotAllowedDomainException("specified mail domain is not allowed: $address")
        }
    }

    init {
        validate(value)
    }
}

class Recipient(val id: RecipientId, val name: RecipientName, val email: RecipientEmailAddress, val locked: Boolean) {
    companion object {
        fun create(name: RecipientName, email: RecipientEmailAddress): Recipient {
            return Recipient(RecipientId.createRandom(), name, email, false)
        }
    }
}