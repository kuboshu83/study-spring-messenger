package org.example.manager.web.api.recipient

import org.example.manager.domain.errors.DataNotFoundException
import org.example.manager.domain.errors.InvalidRequestDataException
import org.example.manager.domain.errors.NotAllowedEmailDomainException
import org.example.manager.domain.model.Recipient
import org.example.manager.domain.model.RecipientEmailAddress
import org.example.manager.domain.model.RecipientId
import org.example.manager.domain.model.RecipientName
import org.example.manager.domain.service.RecipientCreateService
import org.example.manager.domain.service.RecipientDeleteService
import org.example.manager.domain.service.RecipientSearchService
import org.example.manager.domain.service.RecipientUpdateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

data class RecipientQueryResponse(val id: String, val name: String, val email: String, val locked: Boolean) {
    companion object {
        fun fromDomain(recipient: Recipient): RecipientQueryResponse {
            return RecipientQueryResponse(
                recipient.id.value,
                recipient.name.value,
                recipient.email.value,
                recipient.locked
            )
        }
    }
}

data class RecipientCreateRequest(val name: String, val email: String) {
    fun nameOrThrow(): RecipientName {
        try {
            return RecipientName(name)
        } catch (e: IllegalArgumentException) {
            throw InvalidRequestDataException("invalid recipient name", e)
        }
    }

    fun emailOrThrow(): RecipientEmailAddress {
        try {
            return RecipientEmailAddress.of(email)
        } catch (e: IllegalArgumentException) {
            throw InvalidRequestDataException("invalid recipient email", e)
        } catch (e: NotAllowedEmailDomainException) {
            throw InvalidRequestDataException("invalid recipient email", e)
        }
    }
}

data class RecipientCreateResponse(val id: String, val name: String, val email: String)

data class RecipientQueryRequestParameters(
    val name: String?,
    val applicationId: String?,
    val recipientIds: List<String>?
) {
    fun count(): Int {
        var count = 0
        if (name != null) count++
        if (applicationId != null) count++
        if (recipientIds != null) count++
        return count
    }
}

data class RecipientUpdateRequest(val name: String, val email: String, val locked: Boolean) {
    fun toDomainWith(id: RecipientId): Recipient {
        val recipientName = toNameOrThrowInvalidRequestData(name)
        val recipientEmail = toEmailOrThrowInvalidRequestData(email)
        return Recipient(id, recipientName, recipientEmail, locked)
    }
}


@RestController
@RequestMapping("/api/v1/recipients")
class RecipientRestApiController(
    private val recipientSearchService: RecipientSearchService,
    private val recipientCreateService: RecipientCreateService,
    private val recipientDeleteService: RecipientDeleteService,
    private val recipientUpdateService: RecipientUpdateService,
) {
    private fun List<Recipient>.toResponse(): List<RecipientQueryResponse> {
        return this.map { RecipientQueryResponse.fromDomain(it) }
    }

    @GetMapping
    fun findRecipients(params: RecipientQueryRequestParameters): List<RecipientQueryResponse> {
        if (params.count() > 1) {
            throw InvalidRequestDataException("only one or zero parameter is allowed: count=${params.count()}")
        }
        return if (params.name != null) {
            val name = toNameOrThrowInvalidRequestData(params.name)
            recipientSearchService.fuzzyFindRecipientsByRecipientName(name).toResponse()
        } else if (params.recipientIds != null) {
            val ids = params.recipientIds.map { toIdOrThrowInvalidRequestData(it) }
            recipientSearchService.findRecipientsByRecipientIds(ids.toSet()).toResponse()
        } else if (params.applicationId != null) {
            val id = toApplicationIdOrThrowInvalidRequestData(params.applicationId)
            recipientSearchService.findRecipientsByApplicationId(id).toResponse()
        } else {
            recipientSearchService.findAll().toResponse()
        }
    }

    @GetMapping("/{id}")
    fun findByRecipientId(@PathVariable("id") id: String): RecipientQueryResponse {
        val recipientId = toIdOrThrowInvalidRequestData(id)
        return recipientSearchService.findByRecipientId(recipientId)
            ?.let { RecipientQueryResponse.fromDomain(it) }
            ?: throw DataNotFoundException("recipient not found")
    }

    @PostMapping
    fun createRecipient(@RequestBody recipient: RecipientCreateRequest): ResponseEntity<RecipientCreateResponse> {
        val name = recipient.nameOrThrow()
        val email = recipient.emailOrThrow()
        val recipientId = recipientCreateService.create(name, email)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(recipientId.value)
            .toUri()
        return ResponseEntity
            .created(location)
            .body(
                RecipientCreateResponse(recipientId.value, name.value, email.value)
            )
    }

    @DeleteMapping("/{id}")
    fun deleteRecipient(@PathVariable("id") id: String) {
        val recipientId = toIdOrThrowInvalidRequestData(id)
        recipientDeleteService.deleteByRecipientId(recipientId)
    }

    @PatchMapping("/{id}")
    fun updateRecipient(@PathVariable("id") id: String, @RequestBody request: RecipientUpdateRequest) {
        val recipientId = toIdOrThrowInvalidRequestData(id)
        recipientUpdateService.update(request.toDomainWith(recipientId))
    }
}