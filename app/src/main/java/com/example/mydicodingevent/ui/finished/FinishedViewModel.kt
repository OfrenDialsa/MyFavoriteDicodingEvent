package com.example.mydicodingevent.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: EventRepository) : ViewModel() {

    private val _finishedEvents = MutableLiveData<Resource<List<ListEventsItem>>>()
    val finishedEvents: LiveData<Resource<List<ListEventsItem>>> = _finishedEvents

    init {
        fetchFinishedEvents()
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            repository.getFinishedEvents("", 40).collect {
                _finishedEvents.value = Resource.Loading() // Set loading state before collecting

                try {
                    repository.getFinishedEvents("", 40).collect { result ->
                        _finishedEvents.value = result // Emit the result from the repository
                    }
                } catch (e: Exception) {
                    _finishedEvents.value = Resource.Error(e.message ?: "Unknown error")
                }
            }
        }
    }


    fun setEvents() {
        fetchFinishedEvents()
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _finishedEvents.value = Resource.Loading() // Set loading state before collecting
            try {
                repository.getFinishedEvents(query, 40).collect { result ->
                    _finishedEvents.value = result // Emit the result from the repository
                }
            } catch (e: Exception) {
                _finishedEvents.value = Resource.Error(e.message ?: "Unknown error")
            }
        }

    }

}
