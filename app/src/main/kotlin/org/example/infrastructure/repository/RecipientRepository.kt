package org.example.infrastructure.repository

import org.apache.ibatis.annotations.Mapper
import org.example.domain.model.Recipient
import org.example.domain.model.RecipientEmailAddress
import org.example.domain.model.RecipientId
import org.example.domain.model.RecipientName
import org.example.domain.repository.RecipientCommand
import org.example.domain.repository.RecipientQuery
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
}

@Mapper
interface RecipientCommandDAO {
    fun save(recipient: RecipientDTO)
    fun update(recipient: RecipientDTO)
    fun deleteByRecipientId(recipientId: String)
}