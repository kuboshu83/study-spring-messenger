package org.example.domain.service

import org.example.domain.errors.DataConflictedException
import org.example.domain.errors.DataNotFoundException
import org.example.domain.model.Description
import org.example.domain.model.Group
import org.example.domain.model.GroupId
import org.example.domain.model.GroupName
import org.example.domain.repository.GroupCommand
import org.example.domain.repository.GroupQuery
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupSearchService(private val groupQuery: GroupQuery) {
    fun findAll(): List<Group> {
        return groupQuery.findAll()
    }

    fun findByGroupId(groupId: GroupId): Group {
        val group = groupQuery.findByGroupId(groupId)
            ?: throw DataNotFoundException("specified group not found: groupId=${groupId.value}")
        return group
    }

    fun fuzzyFindGroupsByGroupName(groupName: GroupName): List<Group> {
        return groupQuery.fuzzyFindGroupsByGroupName(groupName)
    }

    fun findGroupsByGroupIds(groupIds: Set<GroupId>): List<Group> {
        if (groupIds.isEmpty()) {
            throw IllegalStateException("no search keyword(group id) is specified")
        }
        return groupQuery.findGroupsByGroupIds(groupIds)
    }
}

@Service
class GroupCreateService(private val groupCommand: GroupCommand, private val groupQuery: GroupQuery) {
    @Transactional
    fun create(groupName: GroupName, description: Description): GroupId {
        if (groupQuery.findByGroupName(groupName) != null) {
            throw DataConflictedException("specified group name already used: groupName=${groupName.value}")
        }

        val group = Group.create(groupName, description)
        groupCommand.save(group)
        return group.id
    }
}

@Service
class GroupDeleteService(private val groupCommand: GroupCommand) {
    @Transactional
    fun deleteByGroupId(groupId: GroupId) {
        groupCommand.deleteByGroupId(groupId)
    }
}

@Service
class GroupUpdateService(private val groupCommand: GroupCommand, private val groupQuery: GroupQuery) {
    @Transactional
    fun update(group: Group) {
        if (groupQuery.findByGroupId(group.id) == null) {
            throw DataNotFoundException("specified group not found: groupId=${group.id.value}")
        }

        val foundGroup = groupQuery.findByGroupName(group.name)
        if (foundGroup != null && foundGroup.id != group.id) {
            throw DataConflictedException("specified group name already used: groupName=${group.name.value}")
        }

        groupCommand.update(group)
    }
}