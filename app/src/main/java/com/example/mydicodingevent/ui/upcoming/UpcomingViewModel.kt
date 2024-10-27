package com.example.mydicodingevent.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydicodingevent.data.Resource
import androidx.lifecycle.viewModelScope
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class UpcomingViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val upcomingEvents: LiveData<Resource<List<ListEventsItem>>> = _upcomingEvents

    init {
        fetchUpcomingEvents()
    }

    private fun fetchUpcomingEvents() {
        viewModelScope.launch {
            repository.getUpcomingEvents("", 40).collect {
                _upcomingEvents.value = Resource.Loading()
                try {
                    repository.getUpcomingEvents("", 5).collect { result ->
                        _upcomingEvents.value = result
                    }
                } catch (e: Exception) {
                    _upcomingEvents.value = Resource.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun setEvent(){
        fetchUpcomingEvents()
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _upcomingEvents.value = Resource.Loading()
            try {
                repository.getFinishedEvents(query, 5).collect { result ->
                    _upcomingEvents.value = result
                }
            } catch (e: Exception) {
                _upcomingEvents.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

}