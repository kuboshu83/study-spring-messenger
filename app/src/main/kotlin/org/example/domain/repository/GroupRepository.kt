package org.example.domain.repository

import org.example.domain.model.Group
import org.example.domain.model.GroupId
import org.example.domain.model.GroupName

interface GroupQuery {
    fun findAll(): List<Group>
    fun findByGroupName(name: GroupName): Group?
    fun findByGroupId(groupId: GroupId): Group?
    fun fuzzyFindGroupsByGroupName(groupName: GroupName): List<Group>
    fun findGroupsByGroupIds(groupIds: Set<GroupId>): List<Group>
}

interface GroupCommand {
    fun save(group: Group)
    fun deleteByGroupId(groupId: GroupId)
    fun update(group: Group)
}