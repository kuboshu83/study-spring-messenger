package org.example.domain.model

import org.example.domain.errors.InvalidFormatException
import org.example.domain.errors.TooLongException

@ConsistentCopyVisibility
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

        fun validate(name: String) {
            if (name.length > MAX_SIZE) {
                throw TooLongException("group name is too long: limitSize=$MAX_SIZE, actual=${name.length}")
            }
            if (!VALID_PATTERN.matches(name)) {
                throw InvalidFormatException("group name format is invalid: name='$name'")
            }
        }
    }

    init {
        validate(value)
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

class Group(
    val id: GroupId,
    val name: GroupName,
    val locked: Boolean,
    val members: GroupUniqueMembers,
    val description: Description
) {
    companion object {
        fun create(name: GroupName, description: Description): Group {
            return Group(GroupId.createRandom(), name, false, GroupUniqueMembers.empty(), description)
        }
    }

    constructor(id: GroupId, name: GroupName, locked: Boolean, description: Description)
            : this(id, name, locked, GroupUniqueMembers.empty(), description)

    fun isEmpty(): Boolean {
        return members.isEmpty()
    }

    fun addMember(recipientId: RecipientId): Group {
        val updatedMembers = members.add(recipientId)
        return Group(id, name, locked, updatedMembers, description)
    }
}