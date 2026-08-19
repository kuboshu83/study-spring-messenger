package org.example.web.application

data class ApplicationCreationForm(var name: String = "", var description: String = "")

data class ApplicationUpdateForm(
    var name: String = "",
    var locked: Boolean = false,
    var description: String = "",
    var groups: List<String> = emptyList(),
)