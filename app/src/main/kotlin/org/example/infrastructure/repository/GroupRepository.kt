package org.example.infrastructure.repository

import org.apache.ibatis.annotations.Mapper
import org.example.domain.model.*
import org.example.domain.repository.GroupCommand
import org.example.domain.repository.GroupQuery
import org.springframework.stereotype.Repository

@Repository
class GroupQueryImpl(private val groupQueryDAO: GroupQueryDAO) : GroupQuery {
    override fun findAll(): List<Group> {
        val groupDTOs = groupQueryDAO.findAll()
        return GroupDtoCollection(groupDTOs).toGroups()
    }

    override fun findByGroupName(name: GroupName): Group? {
        val groupDTOs = groupQueryDAO.findGroupByGroupName(name.value)
        val groups = GroupDtoCollection(groupDTOs).toGroups()

        return when (groups.size) {
            0 -> null
            1 -> groups.first()
            else -> throw IllegalStateException("group search result by name must be 0 or 1: resultCount=${groups.size}")
        }
    }

    override fun findByGroupId(groupId: GroupId): Group? {
        val groupDTOs = groupQueryDAO.findGroupByGroupId(groupId.value)
        val groups = GroupDtoCollection(groupDTOs).toGroups()

        return when (groups.size) {
            0 -> null
            1 -> groups.first()
            else -> throw IllegalStateException("group search result by name must be 0 or 1: resultCount=${groups.size}")
        }
    }

    override fun fuzzyFindGroupsByGroupName(groupName: GroupName): List<Group> {
        // TODO: 検索条件あGroupNameだと１文字とかで検索できないから、SearchKeyworkd的なクラスを作る
        val groupDTOs = groupQueryDAO.fuzzyFindGroupsByGroupName(groupName.value)
        return GroupDtoCollection(groupDTOs).toGroups()
    }

    override fun findGroupsByGroupIds(groupIds: Set<GroupId>): List<Group> {
        val groupDTOs = groupQueryDAO.findGroupsByGroupIds(groupIds.map { it.value })
        return GroupDtoCollection(groupDTOs).toGroups()
    }
}

@Repository
class GroupCommandImpl(
    private val groupCommandDAO: GroupCommandDAO,
    private val groupMembershipCommandDAO: GroupMemberShipCommandDAO,
    private val groupQueryDAO: GroupQueryDAO
) : GroupCommand {
    override fun save(group: Group) {
        val groupDTO = GroupDtoCollection.fromGroup(group).toList().first()
        groupCommandDAO.save(groupDTO)

        val memberships = GroupMembershipDtoCollection.fromGroup(group).toList()
        if (memberships.isNotEmpty()) {
            groupMembershipCommandDAO.save(memberships)
        }
    }

    override fun deleteByGroupId(groupId: GroupId) {
        groupCommandDAO.deleteByGroupId(groupId.value)
    }

    override fun update(group: Group) {

        val groupDTO = GroupDtoCollection.fromGroup(group).toList().first()
        groupCommandDAO.update(groupDTO)

        val foundMembers = mutableListOf<RecipientId>()
        for (dto in groupQueryDAO.findGroupByGroupId(group.id.value)) {
            if (dto.recipientId != null) {
                foundMembers.add(RecipientId.fromString(dto.recipientId))
            }
        }
        val previousMembers = GroupUniqueMembers(foundMembers)
        val currentMembers = group.members

        val deleteMembers = previousMembers.subtract(currentMembers)
        val newMembers = currentMembers.subtract(previousMembers)

        val deleteMemberships = deleteMembers.members.map { member -> GroupMemberShipDTO(member, group.id) }
        if (deleteMemberships.isNotEmpty()) {
            groupMembershipCommandDAO.delete(deleteMemberships)
        }
        val newMemberships = newMembers.members.map { member -> GroupMemberShipDTO(member, group.id) }
        if (newMemberships.isNotEmpty()) {
            groupMembershipCommandDAO.save(newMemberships)
        }
    }
}

data class GroupDTO(
    val id: String,
    val name: String,
    val locked: Boolean,
    val recipientId: String?,
    val description: String
)

class GroupDtoCollection(private val groupDTOs: List<GroupDTO>) {
    companion object {
        fun fromGroup(group: Group): GroupDtoCollection {
            val id = group.id.value
            val name = group.name.value
            val locked = group.locked
            val description = group.description.value

            // これが無いとメンバーがゼロのグループの場合に変換結果のリストが空になってしまう
            if (group.members.members.isEmpty()) {
                return GroupDtoCollection(listOf(GroupDTO(id, name, locked, null, description)))
            }

            val dtos = mutableListOf<GroupDTO>()
            for (recipientId in group.members.members) {
                dtos.add(GroupDTO(id, name, locked, recipientId.value, description))
            }
            // メンバーが空の場合はrecipientId=nullのレコードが１つ作成されているはず
            if (dtos.isEmpty()) {
                throw IllegalStateException("group convert to zero dts")
            }
            return GroupDtoCollection(dtos.toList())
        }
    }

    fun toList(): List<GroupDTO> {
        return groupDTOs
    }

    fun toGroups(): List<Group> {
        val groups = mutableMapOf<GroupId, Group>()

        for (groupDTO in groupDTOs) {
            val groupId = GroupId.fromString(groupDTO.id)
            val name = GroupName(groupDTO.name)
            val locked = groupDTO.locked
            val recipientId = groupDTO.recipientId?.let { id -> RecipientId.fromString(id) }
            val description = Description(groupDTO.description)

            val group = groups[groupId]
            if (group == null) {
                groups[groupId] = if (recipientId == null) {
                    Group(groupId, name, locked, description)
                } else {
                    Group(groupId, name, locked, description).addMember(recipientId)
                }
            } else {
                groups[groupId] = if (recipientId == null) {
                    group
                } else {
                    group.addMember(recipientId)
                }
            }
        }
        return groups.values.toList()
    }
}

data class GroupMemberShipDTO(val recipientId: String, val groupId: String) {
    constructor(recipientId: RecipientId, groupId: GroupId) : this(recipientId.value, groupId.value)
}

class GroupMembershipDtoCollection(private val groupMemberDTOs: List<GroupMemberShipDTO>) {
    companion object Companion {
        fun fromGroup(group: Group): GroupMembershipDtoCollection {
            val dtos = mutableListOf<GroupMemberShipDTO>()
            for (recipientId in group.members.members) {
                dtos.add(GroupMemberShipDTO(recipientId.value, group.id.value))
            }
            return GroupMembershipDtoCollection(dtos.toList())
        }
    }

    fun toList(): List<GroupMemberShipDTO> {
        return groupMemberDTOs
    }
}

@Mapper
interface GroupQueryDAO {
    fun findAll(): List<GroupDTO>
    fun findGroupByGroupName(groupName: String): List<GroupDTO>
    fun findGroupByGroupId(groupId: String): List<GroupDTO>
    fun fuzzyFindGroupsByGroupName(groupName: String): List<GroupDTO>
    fun findGroupsByGroupIds(groupIds: List<String>): List<GroupDTO>
}

@Mapper
interface GroupCommandDAO {
    fun save(group: GroupDTO)
    fun deleteByGroupId(groupId: String)
    fun update(group: GroupDTO)
}

@Mapper
interface GroupMemberShipCommandDAO {
    fun save(memberships: List<GroupMemberShipDTO>)
    fun delete(memberships: List<GroupMemberShipDTO>)
}
