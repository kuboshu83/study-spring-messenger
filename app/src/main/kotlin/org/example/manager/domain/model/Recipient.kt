package org.example.manager.domain.model

import org.example.common.domain.EmailAddress
import org.example.common.domain.Id
import org.example.manager.domain.errors.NotAllowedEmailDomainException

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

// ユーザ名は他システムの仕様に合わせてアルファベットの小文字とハイフンのみで構成する
// 例) tanaka-ichiro, john
data class RecipientName(val value: String) {
    companion object {
        private val VALID_PATTERN = Regex("""^[a-z]+(-[a-z]+)?$""")
        private const val MAX_LENGTH = 20
    }

    init {
        require(value.length <= MAX_LENGTH) {
            "recipient name is too long: maxLength=$MAX_LENGTH, length=${value.length}"
        }
        require(VALID_PATTERN.matches(value)) {
            "recipient name is invalid format: name='$value'"
        }
    }
}

data class RecipientEmailAddress private constructor(val address: EmailAddress) {
    val value: String
        get() = address.value

    companion object {
        // 送信許可ドメインの追加・削除はここを修正してください。
        // ドメインの変更はレビュー対象であるため、ハードコードしています。
        private val allowedDomain = listOf("example.com", "example.org")

        fun of(text: String): RecipientEmailAddress {
            val address = EmailAddress(text)
            return RecipientEmailAddress(address)
        }
    }

    init {
        if (!allowedDomain.contains(address.domain)) {
            throw NotAllowedEmailDomainException("not allowed domain: allowed domain is [${allowedDomain.joinToString(", ")}]")
        }
    }
}

class Recipient(
    val id: RecipientId,
    val name: RecipientName,
    val email: RecipientEmailAddress,
    val locked: Boolean
) {
    companion object {
        fun create(name: RecipientName, email: RecipientEmailAddress): Recipient {
            return Recipient(RecipientId.createRandom(), name, email, false)
        }
    }
}