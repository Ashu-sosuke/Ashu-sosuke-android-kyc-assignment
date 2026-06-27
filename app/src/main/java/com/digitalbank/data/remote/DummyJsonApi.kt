package com.digitalbank.data.remote

import com.digitalbank.data.remote.dto.UserDto
import com.digitalbank.data.remote.dto.UsersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyJsonApi {
    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): UsersResponseDto

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): UserDto
}
