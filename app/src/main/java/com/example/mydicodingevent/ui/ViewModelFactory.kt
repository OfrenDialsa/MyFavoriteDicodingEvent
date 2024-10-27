package com.example.mydicodingevent.ui

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mydicodingevent.data.EventRepository
import com.example.mydicodingevent.data.FavoriteEventRepository
import com.example.mydicodingevent.di.Injection
import com.example.mydicodingevent.ui.detail.DetailEventViewModel
import com.example.mydicodingevent.ui.detail.FavoriteFavViewModel
import com.example.mydicodingevent.ui.favorite.FavoriteViewModel
import com.example.mydicodingevent.ui.finished.FinishedViewModel
import com.example.mydicodingevent.ui.home.HomeViewModel
import com.example.mydicodingevent.ui.setting.SettingPreferences
import com.example.mydicodingevent.ui.setting.SettingViewModel
import com.example.mydicodingevent.ui.setting.dataStore
import com.example.mydicodingevent.ui.upcoming.UpcomingViewModel

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val favoriteEventRepository: FavoriteEventRepository,
    private val pref: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> {
                UpcomingViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FinishedViewModel::class.java) -> {
                FinishedViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(DetailEventViewModel::class.java) -> {
                DetailEventViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteFavViewModel::class.java) -> {
                FavoriteFavViewModel(favoriteEventRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(favoriteEventRepository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val applicationContext = when (context) {
                    is Application -> context
                    is FragmentActivity -> context.applicationContext
                    else -> context.applicationContext
                }
                val eventRepository = Injection.provideRepository(applicationContext)
                val favoriteEventRepository = Injection.provideFavoriteRepository(applicationContext)
                val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
                ViewModelFactory(eventRepository, favoriteEventRepository, settingPreferences).also {
                    INSTANCE = it
                }
            }
        }
    }
}