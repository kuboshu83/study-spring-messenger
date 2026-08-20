package org.example.web.manager.group

import org.example.manager.domain.model.*
import org.example.manager.domain.service.*
import org.example.web.manager.GroupViewModel
import org.example.web.manager.RecipientViewModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

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
        model: Model
    ): String {
        val group = groupSearchService.findByGroupId(GroupId.fromString(groupId))
        val groupViewModel = GroupViewModel.fromDomain(group)
        model.addAttribute("group", groupViewModel)

        val registeredRecipients = if (group.isEmpty()) {
            emptyList()
        } else {
            recipientSearchService.findRecipientsByRecipientIds(group.members.members)
        }
        val registeredRecipientViewModes = registeredRecipients.map { RecipientViewModel.fromDomain(it) }
        model.addAttribute("registeredRecipients", registeredRecipientViewModes)

        return "group/groupDetailForm"
    }

    @PostMapping("/{id}/delete")
    fun deleteGroupById(@PathVariable("id") groupId: String): String {
        groupDeleteService.deleteByGroupId(GroupId.fromString(groupId))
        return REDIRECT_TO_GROUP_TOP
    }

    @PostMapping("/{id}/update")
    fun updateGroup(@PathVariable("id") groupId: String, form: UpdateGroupForm): String {
        val members = GroupUniqueMembers(form.members.map { RecipientId.fromString(it) })
        val group = Group(
            GroupId.fromString(groupId),
            GroupName(form.name),
            form.locked,
            members,
            Description(form.description)
        )
        groupUpdateService.update(group)
        return REDIRECT_TO_GROUP_TOP
    }
}