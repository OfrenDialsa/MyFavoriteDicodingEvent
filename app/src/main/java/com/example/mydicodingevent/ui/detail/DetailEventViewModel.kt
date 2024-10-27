package com.example.mydicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.data.remote.response.Event
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _detailEvent = MutableLiveData<Resource<Event>>()
    val detailEvent: LiveData<Resource<Event>> = _detailEvent

    fun fetchDetailEvents(id: String) {
        viewModelScope.launch {
            repository.getDetailEvent(id).collect {
                _detailEvent.value = it
            }
        }
    }


}


