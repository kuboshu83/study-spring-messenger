package org.example.manager.domain.model

import org.example.common.domain.Id
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
        private val VALID_PATTERN = Regex("""^[a-z]+ ?[a-z]+$""")
        private const val MAX_SIZE = 20
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

class ApplicationUniqueMembers(private val groups: Set<GroupId>) {
    companion object {
        fun empty(): ApplicationUniqueMembers {
            return ApplicationUniqueMembers(emptySet())
        }
    }

    fun isEmpty(): Boolean {
        return groups.isEmpty()
    }

    // フィールドを直接公開するより標準ライブラリの挙動に近く、直感的にわかりやすい
    fun toList(): List<GroupId> {
        return groups.toList()
    }

    // リストを受け取る場合の方が多そうなのでリスト型のコンストラクタを用意した
    constructor(groupIds: List<GroupId>) : this(groupIds.toSet())

    fun contains(groupId: GroupId): Boolean {
        return groups.contains(groupId)
    }

    fun add(groupId: GroupId): ApplicationUniqueMembers {
        return ApplicationUniqueMembers(groups + groupId)
    }

    fun delete(groupId: GroupId): ApplicationUniqueMembers {
        return ApplicationUniqueMembers(groups - groupId)
    }

    fun subtract(other: ApplicationUniqueMembers): ApplicationUniqueMembers {
        return ApplicationUniqueMembers(groups - other.groups)
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
    val groups: ApplicationUniqueMembers,
    val description: ApplicationDescription
) {
    companion object {
        fun create(name: ApplicationName, description: ApplicationDescription): Application {
            return Application(ApplicationId.createRandom(), name, false, ApplicationUniqueMembers.empty(), description)
        }
    }

    constructor(id: ApplicationId, name: ApplicationName, locked: Boolean, description: ApplicationDescription)
            : this(id, name, locked, ApplicationUniqueMembers.empty(), description)

    fun addGroup(groupId: GroupId): Application {
        val updatedGroups = groups.add(groupId)
        return Application(id, name, locked, updatedGroups, description)
    }

    fun isEmpty(): Boolean {
        return groups.isEmpty()
    }
}