package org.example.web.recipient

import org.example.domain.model.Recipient
import org.example.domain.model.RecipientName
import org.example.domain.service.RecipientSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class SearchRecipientResponseDTO(val id: String, val name: String, val email: String, val locked: Boolean) {
    companion object {
        fun fromRecipient(recipient: Recipient): SearchRecipientResponseDTO {
            return SearchRecipientResponseDTO(
                recipient.id.value,
                recipient.name.value,
                recipient.email.value,
                recipient.locked
            )
        }
    }
}

@RestController
@RequestMapping("/api/recipients")
class RecipientRestController(private val recipientSearchService: RecipientSearchService) {
    @GetMapping
    fun findRecipient(
        @RequestParam(name = "name", required = false, defaultValue = "") name: String
    ): List<SearchRecipientResponseDTO> {
        if (name.isNotEmpty()) {
            val recipients = recipientSearchService.fuzzyFindRecipientsByRecipientName(RecipientName(name))
            return recipients.map { SearchRecipientResponseDTO.fromRecipient(it) }
        }
        return recipientSearchService.findAll().map { SearchRecipientResponseDTO.fromRecipient(it) }
    }
}