package com.example.mydicodingevent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val upcomingEvents: LiveData<Resource<List<ListEventsItem>>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val finishedEvents: LiveData<Resource<List<ListEventsItem>>> = _finishedEvents

    init {
        fetchUpcomingEvents()
        fetchFinishedEvents()
    }

    private fun fetchUpcomingEvents() {
        viewModelScope.launch {
            _upcomingEvents.value = Resource.Loading()
            try {
                repository.getFinishedEvents("", 5).collect { result ->
                    _upcomingEvents.value = result
                }
            } catch (e: Exception) {
                _upcomingEvents.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            _finishedEvents.value = Resource.Loading()
            try {
                repository.getFinishedEvents("", 5).collect { result ->
                    _finishedEvents.value = result
                }
            } catch (e: Exception) {
                _finishedEvents.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

}