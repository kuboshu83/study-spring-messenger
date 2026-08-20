package org.example.web.recipient

import org.example.manager.domain.model.Recipient
import org.example.manager.domain.model.RecipientEmailAddress
import org.example.manager.domain.model.RecipientId
import org.example.manager.domain.model.RecipientName
import org.example.manager.domain.service.RecipientCreateService
import org.example.manager.domain.service.RecipientDeleteService
import org.example.manager.domain.service.RecipientSearchService
import org.example.manager.domain.service.RecipientUpdateService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/recipients")
class RecipientController(
    private val recipientCreateService: RecipientCreateService,
    private val recipientSearchService: RecipientSearchService,
    private val recipientDeleteService: RecipientDeleteService,
    private val recipientUpdateService: RecipientUpdateService
) {

    companion object {
        private const val REDIRECT_TO_RECIPIENT_TOP = "redirect:/recipients"
    }

    @GetMapping
    fun showTopPage(model: Model): String {
        val recipients = recipientSearchService.findAll()
        model.addAttribute("recipients", recipients)
        return "recipient/topPage"
    }

    @GetMapping("/create")
    fun showRecipientCreationForm(form: NewRecipientForm): String {
        return "recipient/recipientCreationForm"
    }

    @PostMapping("/create")
    fun createRecipient(form: NewRecipientForm): String {
        val name = RecipientName(form.name)
        val email = RecipientEmailAddress(form.email)
        recipientCreateService.create(name, email)
        return REDIRECT_TO_RECIPIENT_TOP
    }

    @GetMapping("/{id}")
    fun showRecipientDetailInformation(
        @PathVariable("id") recipientId: String,
        form: UpdateRecipientForm,
        model: Model
    ): String {
        val recipient = recipientSearchService.findByRecipientId(RecipientId.fromString(recipientId))
        model.addAttribute("recipient", recipient)
        return "recipient/recipientDetailForm"
    }

    @PostMapping("/{id}/update")
    fun updateRecipient(@PathVariable("id") recipientId: String, form: UpdateRecipientForm): String {
        val recipient = Recipient(
            RecipientId.fromString(recipientId),
            RecipientName(form.name),
            RecipientEmailAddress(form.email),
            form.locked
        )
        recipientUpdateService.update(recipient)
        return REDIRECT_TO_RECIPIENT_TOP
    }

    @PostMapping("/{id}/delete")
    fun deleteRecipientById(@PathVariable("id") recipientId: String): String {
        recipientDeleteService.deleteByRecipientId(RecipientId.fromString(recipientId))
        return REDIRECT_TO_RECIPIENT_TOP
    }
}