package org.example.manager.web.recipient

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/recipients")
class RecipientController(
) {
    @GetMapping
    fun showTopPage(): String {
        return "recipient/topPage"
    }

    @GetMapping("/create")
    fun showRecipientCreationForm(): String {
        return "recipient/recipientCreationForm"
    }

    @GetMapping("/{id}")
    fun showRecipientDetailInformation(): String {
        return "recipient/recipientDetailForm"
    }
}