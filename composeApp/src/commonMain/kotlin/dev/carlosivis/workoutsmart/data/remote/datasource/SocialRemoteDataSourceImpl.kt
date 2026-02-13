package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.workoutsmart.core.NetworkWrapper
import dev.carlosivis.workoutsmart.data.remote.service.SocialService
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.models.RankingMember

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

    override suspend fun getRankingMembers(groupId: Int): Result<List<RankingMember>> {
        return try {
            val response = NetworkWrapper.safeCall<List<RankingMember>> {
                service.getRankingMembers(groupId)
            }
            Result.success(response)

        } catch (e: NetworkWrapper.AppNetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}