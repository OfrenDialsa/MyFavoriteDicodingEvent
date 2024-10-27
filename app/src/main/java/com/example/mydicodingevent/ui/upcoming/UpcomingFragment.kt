package com.example.mydicodingevent.ui.upcoming

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.databinding.FragmentUpcomingBinding
import com.example.mydicodingevent.ui.EventAdapter
import com.example.mydicodingevent.ui.ViewModelFactory
import com.example.mydicodingevent.ui.detail.DetailEventActivity

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<UpcomingViewModel>{
        ViewModelFactory.getInstance(requireActivity())
    }
    private val eventAdapter = EventAdapter { eventId ->
        DetailEventActivity.start(requireContext(), eventId)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setViewModel()
        setSearchView()
    }

    private fun setSearchView(){
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.text.toString()
                searchQuery(query)
                true
            }
        }
    }

    private fun searchQuery(query: String) {
        if (query.isNotBlank()) {
            viewModel.searchEvents(query)
            binding.searchBar.setText(query)
        } else {
            viewModel.setEvent()
            binding.searchBar.setText("")
        }
        binding.searchView.hide()
        keyboardHide()

    }

    private fun setRecyclerView() {
        binding.rvListEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setViewModel() {

        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    eventAdapter.submitList(result.data)
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                
            }
        }
    }

    private fun keyboardHide() {
        val hide = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}