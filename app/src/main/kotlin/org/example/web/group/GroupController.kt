package org.example.web.group

import org.example.domain.model.*
import org.example.domain.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/groups")
class GroupController(
    private val groupSearchService: GroupSearchService,
    private val groupCreateService: GroupCreateService,
    private val groupDeleteService: GroupDeleteService,
    private val groupUpdateService: GroupUpdateService,
    private val recipientSearchService: RecipientSearchService
) {
    companion object {
        private const val REDIRECT_TO_GROUP_TOP = "redirect:/groups"
    }

    @GetMapping
    fun showTopPage(model: Model): String {
        val groups = groupSearchService.findAll()
        model.addAttribute("groups", groups)
        return "group/topPage"
    }

    @GetMapping("/create")
    fun showGroupCreationForm(form: NewGroupForm): String {
        return "group/groupCreationForm"
    }

    @PostMapping("/create")
    fun createGroup(form: NewGroupForm): String {
        groupCreateService.create(GroupName(form.name), Description(form.description))
        return REDIRECT_TO_GROUP_TOP
    }

    @GetMapping("/{id}")
    fun showGroupDetailInformation(
        @PathVariable("id") groupId: String,
        form: UpdateGroupForm,
        @RequestParam(name = "searchName", required = false, defaultValue = "") searchName: String,
        model: Model
    ): String {
        val group = groupSearchService.findByGroupId(GroupId.fromString(groupId))
        model.addAttribute("group", group)

        if (!group.isEmpty()) {
            val recipients = recipientSearchService.findRecipientsByRecipientIds(group.members.members)
            model.addAttribute("members", recipients)
        }

        if (searchName != "") {
            val foundRecipients = recipientSearchService.fuzzyFindRecipientsByRecipientName(RecipientName(searchName))
            model.addAttribute("searchResult", foundRecipients)
        }

        return "group/groupDetailForm"
    }

    @PostMapping("/{id}/delete")
    fun deleteGroupById(@PathVariable("id") groupId: String): String {
        groupDeleteService.deleteByGroupId(GroupId.fromString(groupId))
        return REDIRECT_TO_GROUP_TOP
    }

    @PostMapping("/{id}/update")
    fun updateGroup(@PathVariable("id") groupId: String, form: UpdateGroupForm): String {
        val group = Group(GroupId.fromString(groupId), GroupName(form.name), form.locked, Description(form.description))
        groupUpdateService.update(group)
        return REDIRECT_TO_GROUP_TOP
    }
}