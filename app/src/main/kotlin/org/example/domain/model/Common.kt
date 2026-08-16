package org.example.domain.model

import org.example.domain.errors.TooLongException
import java.util.*

@ConsistentCopyVisibility
data class Id private constructor(private val uuid: UUID) {
    companion object {
        fun fromString(text: String): Id {
            return Id(UUID.fromString(text))
        }

        fun createRandom(): Id {
            return Id(UUID.randomUUID())
        }
    }

    val value: String
        get() = uuid.toString()
}

data class Description(val value: String) {
    companion object {
        private const val MAX_SIZE = 100
    }

    init {
        if (value.length > MAX_SIZE) {
            throw TooLongException("description is too long: limitSize=$MAX_SIZE, actual=${value.length}")
        }
    }
}