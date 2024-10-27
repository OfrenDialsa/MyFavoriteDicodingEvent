package com.example.mydicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.databinding.FragmentHomeBinding
import com.example.mydicodingevent.ui.detail.DetailEventActivity
import com.example.mydicodingevent.ui.setting.SettingPreferences
import com.example.mydicodingevent.ui.setting.SettingViewModel
import com.example.mydicodingevent.ui.setting.ViewModelFactory
import com.example.mydicodingevent.ui.setting.dataStore

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>{
        HomeViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var settingViewModel: SettingViewModel
    private val homeFinishedEventAdapter = HomeFinishedEventAdapter { eventId ->
        DetailEventActivity.start(requireContext(), eventId)
    }

    private val homeUpcomingEventAdapter = HomeUpcomingEventAdapter { eventId ->
        DetailEventActivity.start(requireContext(), eventId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRvView()
        setViewModel()

        val pref = SettingPreferences.getInstance(requireActivity().applicationContext.dataStore)

        settingViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setRvView() {
        binding.rvUpcoming.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL )
            adapter = homeUpcomingEventAdapter
        }

        binding.rvFinished.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL )
            adapter = homeFinishedEventAdapter
        }
    }

    private fun setViewModel() {

        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading indicator
                    showLoading(true)
                }
                is Resource.Success -> {
                    // Hide loading indicator and update the adapter with the list of events
                    showLoading(false)
                    homeUpcomingEventAdapter.submitList(result.data) // result.data is List<ListEventsItem>
                }
                is Resource.Error -> {
                    // Hide loading indicator and show error message
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    homeFinishedEventAdapter.submitList(result.data)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}