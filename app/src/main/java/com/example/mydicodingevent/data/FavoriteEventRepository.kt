package com.example.mydicodingevent.data

import androidx.lifecycle.LiveData
import com.example.mydicodingevent.data.local.entity.FavoriteEvent
import com.example.mydicodingevent.data.local.room.FavoriteEventDao

class FavoriteEventRepository(private val dao: FavoriteEventDao) {
    suspend fun insert(favoriteEvent: FavoriteEvent){
        dao.insertEvents(favoriteEvent)
    }

    suspend fun delete(favoriteEvent: FavoriteEvent){
        dao.delete(favoriteEvent)
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> {
        return dao.getFavoriteEventById(id)
    }

    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>> {
        return dao.getAllFavoriteEvent()
    }
}