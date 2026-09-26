package org.example.manager.domain.model

import org.example.common.domain.Id
import org.example.common.domain.UniqueElements
import org.example.common.utils.ellipsize
import org.example.common.utils.trimSpace

data class ApplicationId private constructor(val id: Id) {
    companion object {
        fun createRandom(): ApplicationId {
            return ApplicationId(Id.createRandom())
        }

        fun fromString(text: String): ApplicationId {
            return ApplicationId(Id.fromString(text))
        }
    }

    val value: String
        get() = id.value
}

data class ApplicationName(val value: String) {
    companion object {
        private val VALID_PATTERN = Regex("""^[a-zA-Z0-9-_.]+( [a-zA-Z0-9-_.]+)*$""")
        private const val MAX_SIZE = 50
    }

    init {
        require(value.length <= MAX_SIZE) {
            "application name is too long: limitSize=$MAX_SIZE, actual=${value.length}"
        }
        require(VALID_PATTERN.matches(value)) {
            "application name is invalid format: name='${ellipsize(value)}'"
        }
    }
}

class UniqueApplicationGroups(private val elements: UniqueElements<GroupId>) {
    companion object {
        fun empty(): UniqueApplicationGroups {
            return UniqueApplicationGroups(UniqueElements.empty())
        }
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    // フィールドを直接公開するより標準ライブラリの挙動に近く、直感的にわかりやすい
    fun toList(): List<GroupId> {
        return elements.toList()
    }

    constructor(groups: Set<GroupId>) : this(UniqueElements(groups))

    // リストを受け取る場合の方が多そうなのでリスト型のコンストラクタを用意した
    constructor(groups: List<GroupId>) : this(groups.toSet())

    fun contains(id: GroupId): Boolean {
        return elements.contains(id)
    }

    fun add(id: GroupId): UniqueApplicationGroups {
        return UniqueApplicationGroups(elements.add(id))
    }

    fun delete(id: GroupId): UniqueApplicationGroups {
        return UniqueApplicationGroups(elements.delete(id))
    }

    fun subtract(other: UniqueApplicationGroups): UniqueApplicationGroups {
        return UniqueApplicationGroups(elements.subtract(other.elements))
    }
}

data class ApplicationDescription private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 200

        fun of(text: String): ApplicationDescription {
            return ApplicationDescription(trimSpace(text))
        }
    }

    init {
        require(value.length <= MAX_LENGTH) {
            "application description is too long: maxSize=$MAX_LENGTH, length=${value.length}"
        }
    }
}

class Application(
    val id: ApplicationId,
    val name: ApplicationName,
    val locked: Boolean,
    val groups: UniqueApplicationGroups,
    val description: ApplicationDescription
) {
    companion object {
        fun create(name: ApplicationName, description: ApplicationDescription): Application {
            return Application(ApplicationId.createRandom(), name, false, UniqueApplicationGroups.empty(), description)
        }
    }

    constructor(id: ApplicationId, name: ApplicationName, locked: Boolean, description: ApplicationDescription)
            : this(id, name, locked, UniqueApplicationGroups.empty(), description)

    fun addGroup(groupId: GroupId): Application {
        val updatedGroups = groups.add(groupId)
        return Application(id, name, locked, updatedGroups, description)
    }

    fun isEmpty(): Boolean {
        return groups.isEmpty()
    }
}