package org.example.manager.web.api.recipient

import org.example.manager.domain.errors.InvalidRequestDataException
import org.example.manager.domain.errors.NotAllowedEmailDomainException
import org.example.manager.domain.model.ApplicationId
import org.example.manager.domain.model.RecipientEmailAddress
import org.example.manager.domain.model.RecipientId
import org.example.manager.domain.model.RecipientName

fun toEmailOrThrowInvalidRequestData(email: String): RecipientEmailAddress {
    try {
        return RecipientEmailAddress.of(email)
    } catch (e: IllegalArgumentException) {
        throw InvalidRequestDataException("invalid recipient email", e)
    } catch (e: NotAllowedEmailDomainException) {
        throw InvalidRequestDataException("invalid recipient email", e)
    }
}

fun toNameOrThrowInvalidRequestData(name: String): RecipientName {
    try {
        return RecipientName(name)
    } catch (e: IllegalArgumentException) {
        throw InvalidRequestDataException("invalid recipient name", e)
    }
}

fun toIdOrThrowInvalidRequestData(id: String): RecipientId {
    try {
        return RecipientId.fromString(id)
    } catch (e: IllegalArgumentException) {
        throw InvalidRequestDataException("invalid recipient id", e)
    }
}

fun toApplicationIdOrThrowInvalidRequestData(id: String): ApplicationId {
    try {
        return ApplicationId.fromString(id)
    } catch (e: IllegalArgumentException) {
        throw InvalidRequestDataException("invalid application id", e)
    }
}