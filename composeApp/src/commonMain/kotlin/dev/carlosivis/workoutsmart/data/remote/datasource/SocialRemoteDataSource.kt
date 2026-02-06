package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.features.group.CreateGroupRequest
import dev.carlosivis.features.group.GroupResponse
import dev.carlosivis.features.group.JoinGroupRequest

interface SocialRemoteDataSource {
    suspend fun getGroups(): Result<List<GroupResponse>>
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse>
    suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse>
}