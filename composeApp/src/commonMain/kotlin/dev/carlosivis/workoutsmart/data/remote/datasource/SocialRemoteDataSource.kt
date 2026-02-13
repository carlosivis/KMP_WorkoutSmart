package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.models.RankingMember

interface SocialRemoteDataSource {
    suspend fun getGroups(): Result<List<GroupResponse>>
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse>
    suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse>
    suspend fun getRankingMembers(groupId: Int): Result<List<RankingMember>>
}