package org.example.domain.repository

import org.example.domain.model.*

interface RecipientCommand {
    fun save(recipient: Recipient)
    fun update(recipient: Recipient)
    fun deleteByRecipientId(recipientId: RecipientId)
}

interface RecipientQuery {
    fun findByEmail(email: RecipientEmailAddress): Recipient?
    fun findByRecipientId(recipientId: RecipientId): Recipient?
    fun findAll(): List<Recipient>
    fun findRecipientsByRecipientIds(recipientIds: Set<RecipientId>): List<Recipient>
    fun fuzzyFindRecipientsByRecipientName(recipientName: RecipientName): List<Recipient>
    fun findRecipientsByApplicationId(applicationId: ApplicationId): List<Recipient>
}