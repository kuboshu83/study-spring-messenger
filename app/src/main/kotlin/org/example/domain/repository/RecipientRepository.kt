package org.example.domain.repository

import org.example.domain.model.Recipient
import org.example.domain.model.RecipientEmailAddress
import org.example.domain.model.RecipientId

interface RecipientCommand {
    fun save(recipient: Recipient)
    fun update(recipient: Recipient)
    fun deleteByRecipientId(recipientId: RecipientId)
}

interface RecipientQuery {
    fun findByEmail(email: RecipientEmailAddress): Recipient?
    fun findByRecipientId(recipientId: RecipientId): Recipient?
    fun findAll(): List<Recipient>
}