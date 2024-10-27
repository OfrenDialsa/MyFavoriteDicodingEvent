package com.example.mydicodingevent.data

import android.util.Log
import com.example.mydicodingevent.data.remote.response.Event
import com.example.mydicodingevent.data.remote.response.ListEventsItem
import com.example.mydicodingevent.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class EventRepository private constructor(
    private val apiService: ApiService,
) {


    suspend fun getFinishedEvents(query: String? = null, limit: Int): Flow<Resource<List<ListEventsItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getEvent(0, q = query, limit)

            if (response.listEvents.isNotEmpty()) {
                emit(Resource.Success(response.listEvents))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message.toString()}")
            emit(Resource.Error(e.message.toString()))
        }
    }

    suspend fun getUpcomingEvents(query: String? = null, limit: Int): Flow<Resource<List<ListEventsItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getEvent(1, q = query, limit)

            if (response.listEvents.isNotEmpty()) {
                emit(Resource.Success(response.listEvents))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message.toString()}")
            emit(Resource.Error(e.message.toString()))
        }
    }


    suspend fun getDetailEvent(id: String): Flow<Resource<Event>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getDetailEvent(id)
            emit(Resource.Success(response.event))
        } catch (e: Exception) {
            emit(Resource.Error(e.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService)
            }.also { instance = it }
    }
}