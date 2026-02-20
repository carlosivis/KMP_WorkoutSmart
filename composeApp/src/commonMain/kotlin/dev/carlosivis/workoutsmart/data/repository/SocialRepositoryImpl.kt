package dev.carlosivis.workoutsmart.data.repository

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.data.remote.datasource.SocialRemoteDataSource
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.models.RankingMember

class SocialRepositoryImpl(
    private val remoteDataSource: SocialRemoteDataSource
) : SocialRepository {
    override suspend fun getGroups(): Result<List<GroupResponse>> {
        return remoteDataSource.getGroups()
    }

    override suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse> {
        return remoteDataSource.createGroup(request)
    }

    override suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse> {
        return remoteDataSource.joinGroup(request)
    }

    override suspend fun getRankingMembers(groupId: Int): Result<List<RankingMember>> {
        return remoteDataSource.getRankingMembers(groupId)
    }

    override suspend fun registerWorkoutLog(request: WorkoutLogRequest
    ): Result<Unit> {
        return remoteDataSource.registerWorkoutLog(request)

    }
}