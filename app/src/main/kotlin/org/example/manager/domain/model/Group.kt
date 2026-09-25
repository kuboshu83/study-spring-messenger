package org.example.manager.domain.model

import org.example.common.domain.Id
import org.example.common.utils.trimSpace

data class GroupId private constructor(private val id: Id) {
    companion object {
        fun createRandom(): GroupId {
            return GroupId(Id.createRandom())
        }

        fun fromString(text: String): GroupId {
            return GroupId(Id.fromString(text))
        }
    }

    val value: String
        get() = id.value
}

data class GroupName(val value: String) {
    companion object {
        private val VALID_PATTERN = Regex("""^[a-z]+ ?[a-z]+$""")
        private const val MAX_SIZE = 20
    }

    init {
        require(value.length <= MAX_SIZE) {
            "group name is too long: limitSize=$MAX_SIZE, actual=${value.length}"
        }
        require(VALID_PATTERN.matches(value)) {
            "group name format is invalid: name='$value'"
        }
    }
}

class GroupUniqueMembers(val members: Set<RecipientId>) {
    companion object {
        fun empty(): GroupUniqueMembers {
            return GroupUniqueMembers(emptySet())
        }
    }

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    // リストを受け取る場合の方が多そうなのでリスト型のコンストラクタを用意した
    constructor(recipients: List<RecipientId>) : this(recipients.toSet())

    fun contains(recipientId: RecipientId): Boolean {
        return members.contains(recipientId)
    }

    fun add(recipientId: RecipientId): GroupUniqueMembers {
        return GroupUniqueMembers(members + recipientId)
    }

    fun delete(recipientId: RecipientId): GroupUniqueMembers {
        return GroupUniqueMembers(members - recipientId)
    }

    fun subtract(other: GroupUniqueMembers): GroupUniqueMembers {
        return GroupUniqueMembers(members - other.members)
    }
}

data class GroupDescription private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 200

        fun of(text: String): GroupDescription {
            return GroupDescription(trimSpace(text))
        }
    }

    init {
        require(value.length <= MAX_LENGTH) {
            "group description is too long: maxSize=$MAX_LENGTH, length=${value.length}"
        }
    }
}

class Group(
    val id: GroupId,
    val name: GroupName,
    val locked: Boolean,
    val members: GroupUniqueMembers,
    val description: GroupDescription
) {
    companion object {
        fun create(name: GroupName, description: GroupDescription): Group {
            return Group(GroupId.createRandom(), name, false, GroupUniqueMembers.empty(), description)
        }
    }

    constructor(id: GroupId, name: GroupName, locked: Boolean, description: GroupDescription)
            : this(id, name, locked, GroupUniqueMembers.empty(), description)

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    fun addMember(recipientId: RecipientId): Group {
        val updatedMembers = members.add(recipientId)
        return Group(id, name, locked, updatedMembers, description)
    }
}