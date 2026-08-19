package org.example.web.group

import org.example.domain.model.Group
import org.example.domain.model.GroupName
import org.example.domain.service.GroupSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class GroupSearchResponseDTO(
    val id: String,
    val name: String,
    val locked: Boolean,
    val recipients: List<String>,
    val description: String
) {
    companion object {
        fun fromGroup(group: Group): GroupSearchResponseDTO {
            val recipients = group.members.members.map { recipientId -> recipientId.value }
            return GroupSearchResponseDTO(
                group.id.value,
                group.name.value,
                group.locked,
                recipients,
                group.description.value
            )
        }
    }
}


@RestController
@RequestMapping("/api/groups")
class GroupRestController(private val groupSearchService: GroupSearchService) {
    @GetMapping
    fun findGroup(
        @RequestParam("name", required = false, defaultValue = "") groupName: String
    ): List<GroupSearchResponseDTO> {
        val groups = if (groupName.isEmpty()) {
            groupSearchService.findAll()
        } else {
            groupSearchService.fuzzyFindGroupsByGroupName(GroupName(groupName))
        }
        return groups.map { group -> GroupSearchResponseDTO.fromGroup(group) }
    }
}