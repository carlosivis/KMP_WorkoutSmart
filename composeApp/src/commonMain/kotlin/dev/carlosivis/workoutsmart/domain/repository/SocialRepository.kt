package dev.carlosivis.workoutsmart.domain.repository

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.shared.CreateGroupRequest
import dev.carlosivis.workoutsmart.shared.GroupResponse
import dev.carlosivis.workoutsmart.shared.JoinGroupRequest
import dev.carlosivis.workoutsmart.shared.RankingMember

interface SocialRepository {
    suspend fun getGroups(): Result<List<GroupResponse>>
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse>
    suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse>
    suspend fun getRankingMembers(groupId: Int): Result<List<RankingMember>>
    suspend fun registerWorkoutLog(request: WorkoutLogRequest): Result<Unit>
}
