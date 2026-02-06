package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.features.group.CreateGroupRequest
import dev.carlosivis.features.group.GroupResponse
import dev.carlosivis.features.group.JoinGroupRequest
import dev.carlosivis.workoutsmart.data.remote.datasource.SocialRemoteDataSource

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
}