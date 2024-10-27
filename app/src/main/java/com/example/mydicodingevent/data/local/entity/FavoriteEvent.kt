package com.example.mydicodingevent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event")
data class FavoriteEvent(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val summary: String = "",
    val imageLogo: String? = null
)

