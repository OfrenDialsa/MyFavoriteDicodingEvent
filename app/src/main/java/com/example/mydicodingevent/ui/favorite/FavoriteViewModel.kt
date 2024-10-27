package com.example.mydicodingevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mydicodingevent.data.FavoriteEventRepository
import com.example.mydicodingevent.data.local.entity.FavoriteEvent

class FavoriteViewModel(repository: FavoriteEventRepository): ViewModel() {

    val favoriteEvent: LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvent()


}