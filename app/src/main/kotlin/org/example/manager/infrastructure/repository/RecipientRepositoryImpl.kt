package org.example.manager.infrastructure.repository

import org.apache.ibatis.annotations.Mapper
import org.example.manager.domain.errors.DataCorruptionException
import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.errors.DuplicateDataException
import org.example.manager.domain.model.*
import org.example.manager.domain.repository.RecipientCommand
import org.example.manager.domain.repository.RecipientQuery
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component

@Component
class RecipientCommandImpl(private val recipientCommandDAO: RecipientCommandDAO) : RecipientCommand {
    override fun save(recipient: Recipient) {
        try {
            recipientCommandDAO.save(RecipientDTO.fromRecipient(recipient))
        } catch (ex: DuplicateKeyException) {
            throw DuplicateDataException("email is already used", ex)
        }
    }

    override fun update(recipient: Recipient) {
        val count = try {
            recipientCommandDAO.update(RecipientDTO.fromRecipient(recipient))
        } catch (ex: DuplicateKeyException) {
            throw DuplicateDataException("email is already used", ex)
        }
        when (count) {
            0 -> throw DataNotFoundException("recipient not found: name=${recipient.name.value}")
            1 -> return
            // emailのカラムにUnique制約を設けているので、ここには到達しないはず。到達したら制約が壊れている。
            else -> error("expected to update exactly 1 record, but updated multiple records: count=$count")
        }
    }

    override fun deleteByRecipientId(recipientId: RecipientId) {
        val count = recipientCommandDAO.deleteByRecipientId(recipientId.value)
        if (count > 1) {
            // 主キーなのでここには到達しないはず。ただバグなどで到達した場合の影響が甚大なので安全策で入れている。
            error("expected to delete 0 or 1 record, but deleted multiple records: count=$count")
        }
    }
}

@Component
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
        try {
            return Recipient(
                RecipientId.fromString(id),
                RecipientName(name),
                RecipientEmailAddress.of(email),
                locked
            )
        } catch (ex: IllegalArgumentException) {
            throw DataCorruptionException("data corruption detected: id=$id, name=$name, email=$email")
        }
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
    fun update(recipient: RecipientDTO): Int
    fun deleteByRecipientId(recipientId: String): Int
}