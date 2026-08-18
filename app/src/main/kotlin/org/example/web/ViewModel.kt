package org.example.web

import org.example.domain.model.Group
import org.example.domain.model.Recipient

@ConsistentCopyVisibility
data class GroupViewModel private constructor(
    val id: String,
    val name: String,
    val locked: Boolean,
    val members: List<String>,
    val description: String
) {
    companion object {
        fun fromDomain(group: Group): GroupViewModel {
            return GroupViewModel(
                group.id.value,
                group.name.value,
                group.locked,
                group.members.members.map { it.value }.toList(),
                group.description.value
            )
        }
    }
}

@ConsistentCopyVisibility
data class RecipientViewModel private constructor(
    val id: String,
    val name: String,
    val email: String,
    val locked: Boolean
) {
    companion object {
        fun fromDomain(recipient: Recipient): RecipientViewModel {
            return RecipientViewModel(recipient.id.value, recipient.name.value, recipient.email.value, recipient.locked)
        }
    }
}