package org.example.manager.domain.model

import org.example.utils.trimSpace
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


/**
 * 両端の空白文字は削除され、文字数は削除後の文字列でカウントされます。
 * また、このクラスで取り扱えるのはUTF-16コード単位で1000文字です。
 */
data class Description private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 1000

        // 規定サイズ以上の文字列が入力されたら例外をスローする。
        fun of(text: String): Description {
            return Description(trimSpace(text))
        }
    }

    init {
        require(value.length <= MAX_LENGTH) {
            "description is too long: limit=${MAX_LENGTH} length=${value.length}"
        }
    }
}