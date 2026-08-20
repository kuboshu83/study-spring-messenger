package org.example.manager.domain.service

import org.example.manager.domain.errors.DataConflictedException
import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.model.*
import org.example.manager.domain.repository.RecipientCommand
import org.example.manager.domain.repository.RecipientQuery
import org.springframework.stereotype.Service

@Service
class RecipientSearchService(private val recipientQuery: RecipientQuery) {
    fun findAll(): List<Recipient> {
        return recipientQuery.findAll()
    }

    fun findByRecipientId(recipientId: RecipientId): Recipient {
        val recipient = recipientQuery.findByRecipientId(recipientId)
            ?: throw DataNotFoundException("specified recipient not found: id=${recipientId.value}")
        return recipient
    }

    fun findRecipientsByRecipientIds(recipientIds: Set<RecipientId>): List<Recipient> {
        if (recipientIds.isEmpty()) {
            throw IllegalStateException("no search keyword(recipient id) is specified")
        }
        return recipientQuery.findRecipientsByRecipientIds(recipientIds)
    }

    fun fuzzyFindRecipientsByRecipientName(recipientName: RecipientName): List<Recipient> {
        return recipientQuery.fuzzyFindRecipientsByRecipientName(recipientName)
    }

    fun findRecipientsByApplicationId(applicationId: ApplicationId): List<Recipient> {
        return recipientQuery.findRecipientsByApplicationId(applicationId)
    }
}

@Service
class RecipientCreateService(
    private val recipientCommand: RecipientCommand,
    private val recipientQuery: RecipientQuery
) {
    fun create(name: RecipientName, email: RecipientEmailAddress): RecipientId {
        if (recipientQuery.findByEmail(email) != null) {
            throw DataConflictedException("specified email address is already used: address=${email.value}")
        }

        val recipient = Recipient.create(name, email)
        recipientCommand.save(recipient)
        return recipient.id
    }
}

@Service
class RecipientUpdateService(
    private val recipientCommand: RecipientCommand,
    private val recipientQuery: RecipientQuery
) {
    fun update(recipient: Recipient) {
        if (recipientQuery.findByRecipientId(recipient.id) == null) {
            throw DataNotFoundException("specified recipient not found: id=${recipient.id.value} ")
        }

        val foundRecipient = recipientQuery.findByEmail(recipient.email)
        if (foundRecipient != null && foundRecipient.id != recipient.id) {
            throw DataConflictedException("specified email address is already used: address=${recipient.email.value}")
        }

        recipientCommand.update(recipient)
    }
}

@Service
class RecipientDeleteService(private val recipientCommand: RecipientCommand) {
    fun deleteByRecipientId(recipientId: RecipientId) {
        recipientCommand.deleteByRecipientId(recipientId)
    }
}