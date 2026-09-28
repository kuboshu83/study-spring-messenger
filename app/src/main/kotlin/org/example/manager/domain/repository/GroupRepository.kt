package org.example.manager.domain.repository

import org.example.manager.domain.model.Group
import org.example.manager.domain.model.GroupId
import org.example.manager.domain.model.GroupName

interface GroupQuery {
    fun findAll(): List<Group>
    fun findByGroupName(name: GroupName): Group?
    fun findByGroupId(groupId: GroupId): Group?
    fun fuzzyFindGroupsByGroupName(groupName: GroupName): List<Group>
    fun findGroupsByGroupIds(groupIds: Set<GroupId>): List<Group>
}

interface GroupCommand {
    /*
    例外:
    DataNotFoundException:
        - 未登録の受信者をメンバーとして所有するグループが入力された場合。
    DuplicateDataException:
        - 登録済みのグループ名のグループが入力された場合。
     */
    fun save(group: Group)
    fun deleteByGroupId(groupId: GroupId)

    /*
    例外:
    DataNotFoundException:
        - 未登録のグループが入力された場合。
        - 未登録の受信者をメンバーとして所有するグループが入力された場合。
     */
    fun update(group: Group)
}