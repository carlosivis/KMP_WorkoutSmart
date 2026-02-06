package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.features.group.CreateGroupRequest
import dev.carlosivis.features.group.GroupResponse
import dev.carlosivis.features.group.JoinGroupRequest
import dev.carlosivis.workoutsmart.core.NetworkWrapper
import dev.carlosivis.workoutsmart.data.remote.service.SocialService

class SocialRemoteDataSourceImpl(
    private val service: SocialService
) : SocialRemoteDataSource {
    override suspend fun getGroups(): Result<List<GroupResponse>> {
        return try {
            val response = NetworkWrapper.safeCall<List<GroupResponse>> {
                service.getGroups()
            }
            Result.success(response)

        } catch (e: NetworkWrapper.AppNetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGroup(request: CreateGroupRequest): Result<GroupResponse> {
        return try {
            val response = NetworkWrapper.safeCall<GroupResponse> {
                service.createGroup(request)
            }
            Result.success(response)

        } catch (e: NetworkWrapper.AppNetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinGroup(request: JoinGroupRequest): Result<GroupResponse> {
        return try {
            val response = NetworkWrapper.safeCall<GroupResponse> {
                service.joinGroup(request)
            }
            Result.success(response)

        } catch (e: NetworkWrapper.AppNetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}