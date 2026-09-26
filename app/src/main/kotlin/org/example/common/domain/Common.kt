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

class UniqueElements<T>(val elements: Set<T>) {
    companion object {
        fun <U> empty(): UniqueElements<U> {
            return UniqueElements(emptySet())
        }
    }

    // リストを受け取る場合が多いので、リストを受け取るコンストラクタも用意した
    constructor(elements: List<T>) : this(elements.toSet())

    val size: Int
        get() = elements.size

    fun add(element: T): UniqueElements<T> {
        return UniqueElements(elements + element)
    }

    fun delete(element: T): UniqueElements<T> {
        return UniqueElements(elements - element)
    }

    fun subtract(other: UniqueElements<T>): UniqueElements<T> {
        return UniqueElements(elements - other.elements)
    }

    fun contains(element: T): Boolean {
        return elements.contains(element)
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }
}