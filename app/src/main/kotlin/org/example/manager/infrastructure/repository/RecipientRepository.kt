package org.example.manager.infrastructure.repository

import org.apache.ibatis.annotations.Mapper
import org.example.manager.domain.model.*
import org.example.manager.domain.repository.RecipientCommand
import org.example.manager.domain.repository.RecipientQuery
import org.springframework.stereotype.Repository

@Repository
class RecipientCommandImpl(private val recipientCommandDAO: RecipientCommandDAO) : RecipientCommand {
    override fun save(recipient: Recipient) {
        recipientCommandDAO.save(RecipientDTO.fromRecipient(recipient))
    }

    override fun update(recipient: Recipient) {
        recipientCommandDAO.update(RecipientDTO.fromRecipient(recipient))
    }

    override fun deleteByRecipientId(recipientId: RecipientId) {
        recipientCommandDAO.deleteByRecipientId(recipientId.value)
    }
}

@Repository
class RecipientQueryImpl(private val recipientQueryDAO: RecipientQueryDAO) : RecipientQuery {
    override fun findByEmail(email: RecipientEmailAddress): Recipient? {
        return recipientQueryDAO.findByEmail(email.value)?.toRecipient()
    }

    override fun findByRecipientId(recipientId: RecipientId): Recipient? {
        return recipientQueryDAO.findByRecipientId(recipientId.value)?.toRecipient()
    }

    override fun findAll(): List<Recipient> {
        return recipientQueryDAO.findAll().map { it.toRecipient() }
    }

    override fun findRecipientsByRecipientIds(recipientIds: Set<RecipientId>): List<Recipient> {
        val dtos = recipientQueryDAO.findRecipientsByRecipientIds(recipientIds.map { it.value })
        return dtos.map { it.toRecipient() }
    }

    override fun fuzzyFindRecipientsByRecipientName(recipientName: RecipientName): List<Recipient> {
        return recipientQueryDAO.fuzzyFindRecipientsByRecipientName(recipientName.value).map { it.toRecipient() }
    }

    override fun findRecipientsByApplicationId(applicationId: ApplicationId): List<Recipient> {
        return recipientQueryDAO.findRecipientsByApplicationId(applicationId.value).map { it.toRecipient() }
    }
}

data class RecipientDTO(val id: String, val name: String, val email: String, val locked: Boolean) {
    companion object {
        fun fromRecipient(recipient: Recipient): RecipientDTO {
            return RecipientDTO(recipient.id.value, recipient.name.value, recipient.email.value, recipient.locked)
        }
    }

    fun toRecipient(): Recipient {
        return Recipient(
            RecipientId.fromString(id),
            RecipientName(name),
            RecipientEmailAddress(email),
            locked
        )
    }
}

@Mapper
interface RecipientQueryDAO {
    fun findAll(): List<RecipientDTO>
    fun findByRecipientId(recipientId: String): RecipientDTO?
    fun findByEmail(email: String): RecipientDTO?
    fun findRecipientsByRecipientIds(recipientIds: List<String>): List<RecipientDTO>
    fun fuzzyFindRecipientsByRecipientName(recipientName: String): List<RecipientDTO>
    fun findRecipientsByApplicationId(applicationId: String): List<RecipientDTO>
}

@Mapper
interface RecipientCommandDAO {
    fun save(recipient: RecipientDTO)
    fun update(recipient: RecipientDTO)
    fun deleteByRecipientId(recipientId: String)
}