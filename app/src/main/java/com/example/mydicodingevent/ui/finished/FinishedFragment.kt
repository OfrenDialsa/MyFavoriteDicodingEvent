package com.example.mydicodingevent.ui.finished

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.databinding.FragmentFinishedBinding
import com.example.mydicodingevent.ui.EventAdapter
import com.example.mydicodingevent.ui.ViewModelFactory
import com.example.mydicodingevent.ui.detail.DetailEventActivity

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FinishedViewModel>{
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
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRView()
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
            viewModel.setEvents()
            binding.searchBar.setText("")
        }
        binding.searchView.hide()
        keyboardHide()
    }

    private fun keyboardHide() {
        val hide = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setRView() {
        binding.rvListEventFin.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL )
            adapter = eventAdapter
        }
    }

    private fun setViewModel() {

        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}