package org.example.web.group

import org.example.domain.model.Description
import org.example.domain.model.Group
import org.example.domain.model.GroupId
import org.example.domain.model.GroupName
import org.example.domain.service.GroupCreateService
import org.example.domain.service.GroupDeleteService
import org.example.domain.service.GroupSearchService
import org.example.domain.service.GroupUpdateService
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
    fun showGroupDetailInformation(@PathVariable("id") groupId: String, form: UpdateGroupForm, model: Model): String {
        val group = groupSearchService.findByGroupId(GroupId.fromString(groupId))
        model.addAttribute("group", group)
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