package org.example.web.group

data class NewGroupForm(var name: String = "", var description: String = "")

data class UpdateGroupForm(
    var name: String = "",
    var description: String = "",
    var locked: Boolean = false,
    var members: List<String> = emptyList()
)