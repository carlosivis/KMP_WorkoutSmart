package dev.carlosivis.workoutsmart.data.remote.service

import dev.carlosivis.features.group.CreateGroupRequest
import dev.carlosivis.features.group.JoinGroupRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class SocialService(private val client: HttpClient) {

    suspend fun createGroup(request: CreateGroupRequest): HttpResponse {
        return client.post("/groups") {
            setBody(request)
        }
    }

    suspend fun joinGroup(inviteCode: JoinGroupRequest): HttpResponse {
        return client.post("/groups/join") {
            setBody(inviteCode)
        }
    }

    suspend fun getGroups(): HttpResponse {
        return client.get("/groups"){
        }
    }
}
