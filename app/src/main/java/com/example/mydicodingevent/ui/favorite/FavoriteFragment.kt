package com.example.mydicodingevent.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mydicodingevent.data.remote.response.ListEventsItem
import com.example.mydicodingevent.databinding.FragmentFavoritesBinding
import com.example.mydicodingevent.ui.EventAdapter
import com.example.mydicodingevent.ui.ViewModelFactory
import com.example.mydicodingevent.ui.detail.DetailEventActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val eventAdapter = EventAdapter { eventId ->
        DetailEventActivity.start(requireContext(), eventId)
    }
    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setRView()

    }

    private fun setRView() {
        binding.rvFavorite.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@FavoriteFragment.eventAdapter
        }
    }

    private fun setViewModel() {
        // Show loading initially
        showLoading(true)

        viewModel.favoriteEvent.observe(viewLifecycleOwner) { favoriteEvents ->
            Log.d("FavoriteFragment", "Favorite events updated: $favoriteEvents")
            val items = favoriteEvents.map { favoriteEvent ->
                ListEventsItem(
                    id = favoriteEvent.id.toInt(),
                    summary = favoriteEvent.summary,
                    name = favoriteEvent.name,
                    imageLogo = favoriteEvent.imageLogo.toString(),
                    description = "",
                    endTime = "",
                    registrants = 0,
                    quota = 0,
                    beginTime = "",
                    category = "",
                    cityName = "",
                    link = "",
                    mediaCover = "",
                    ownerName = ""
                )
            }

            eventAdapter.submitList(items)

            showLoading(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}