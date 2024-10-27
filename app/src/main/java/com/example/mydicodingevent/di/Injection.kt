package com.example.mydicodingevent.di

import android.content.Context
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.FavoriteEventRepository
import com.example.mydicodingevent.data.local.room.EventDatabase
import com.example.mydicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(ignoredContext: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        return EventRepository.getInstance(apiService)
    }

    fun provideFavoriteRepository(context: Context): FavoriteEventRepository {
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return FavoriteEventRepository(dao)
    }

}