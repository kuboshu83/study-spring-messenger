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
        private val VALID_PATTERN = Regex("""^[a-zA-Z0-9-_.]+( [a-zA-Z0-9-_.]+)*$""")
        private const val MAX_SIZE = 20
    }

    init {
        require(value.length <= MAX_SIZE) {
            "group name is too long: maxLength=$MAX_SIZE, length=${value.length}"
        }
        require(VALID_PATTERN.matches(value)) {
            "group name format is invalid: name='$value'"
        }
    }
}

class UniqueGroupMembers(val members: Set<RecipientId>) {
    companion object {
        fun empty(): UniqueGroupMembers {
            return UniqueGroupMembers(emptySet())
        }
    }

    val size: Int
        get() = members.size

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    // リストを受け取る場合の方が多そうなのでリスト型のコンストラクタを用意した
    constructor(recipients: List<RecipientId>) : this(recipients.toSet())

    fun contains(recipientId: RecipientId): Boolean {
        return members.contains(recipientId)
    }

    fun add(recipientId: RecipientId): UniqueGroupMembers {
        return UniqueGroupMembers(members + recipientId)
    }

    fun delete(recipientId: RecipientId): UniqueGroupMembers {
        return UniqueGroupMembers(members - recipientId)
    }

    fun subtract(other: UniqueGroupMembers): UniqueGroupMembers {
        return UniqueGroupMembers(members - other.members)
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
            "group description is too long: maxLength=$MAX_LENGTH, length=${value.length}"
        }
    }
}

class Group(
    val id: GroupId,
    val name: GroupName,
    val locked: Boolean,
    val members: UniqueGroupMembers,
    val description: GroupDescription
) {
    companion object {
        fun create(name: GroupName, description: GroupDescription): Group {
            return Group(GroupId.createRandom(), name, false, UniqueGroupMembers.empty(), description)
        }
    }

    constructor(id: GroupId, name: GroupName, locked: Boolean, description: GroupDescription)
            : this(id, name, locked, UniqueGroupMembers.empty(), description)

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    fun addMember(recipientId: RecipientId): Group {
        val updatedMembers = members.add(recipientId)
        return Group(id, name, locked, updatedMembers, description)
    }
}