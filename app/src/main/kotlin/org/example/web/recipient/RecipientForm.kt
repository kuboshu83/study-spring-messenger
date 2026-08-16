package org.example.web.recipient

data class NewRecipientForm(var name: String = "", var email: String = "")

data class UpdateRecipientForm(var name: String = "", var email: String = "", var locked: Boolean = false)