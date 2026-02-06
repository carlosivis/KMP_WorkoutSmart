package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.features.group.CreateGroupRequest
import dev.carlosivis.features.group.GroupResponse
import dev.carlosivis.features.group.JoinGroupRequest

interface SocialRepository {
    suspend fun getGroups(): Result<List<GroupResponse>>
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse>
    suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse>
}
