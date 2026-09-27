package org.example.manager.domain.repository

import org.example.manager.domain.model.*

interface RecipientCommand {
    /*
    重複するデータが入力されたらDuplicateRecipientExceptionをThrowする。
     */
    fun save(recipient: Recipient)

    /*
    存在しないデータが入力されたらDataNotFoundExceptionをThrowする。
     */
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