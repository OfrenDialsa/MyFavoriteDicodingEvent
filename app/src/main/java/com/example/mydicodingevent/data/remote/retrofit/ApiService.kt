package com.example.mydicodingevent.data.remote.retrofit

import com.example.mydicodingevent.data.remote.response.DetailEventResponse
import com.example.mydicodingevent.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("events")
    suspend fun getEvent(
        @Query("active") active: Int,
        @Query("q") q: String? = null,
        @Query("limit") limit: Int
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: String): DetailEventResponse

}