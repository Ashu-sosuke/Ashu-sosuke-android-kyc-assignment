package com.digitalbank.data.remote

import com.digitalbank.data.remote.dto.IfscDto
import retrofit2.http.GET
import retrofit2.http.Path

interface IfscApi {
    @GET("{ifsc}")
    suspend fun resolveIfsc(
        @Path("ifsc") ifsc: String
    ): IfscDto
}
