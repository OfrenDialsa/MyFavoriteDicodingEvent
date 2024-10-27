package com.example.mydicodingevent.data.local.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mydicodingevent.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_event Where id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?>

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)


}