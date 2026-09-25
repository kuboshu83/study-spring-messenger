package org.example.common.domain

import java.util.*

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

// 内部ツールなので、そこまで異常な値が入力されることもないので、
// 簡易的な検証のみを行う
data class EmailAddress(val value: String) {
    companion object {
        private val VALID_PATTERN = Regex(
            """^[a-zA-Z0-9]+(?:[-_.]?[a-zA-Z0-9]+)+@(?<domain>[a-zA-Z0-9]+(?:\.[a-zA-Z0-9]+)+)$"""
        )
    }

    val domain: String
        get() {
            return value.substringAfter("@")
        }

    init {
        require(VALID_PATTERN.matches(value)) {
            "email address is invalid format"
        }
    }
}