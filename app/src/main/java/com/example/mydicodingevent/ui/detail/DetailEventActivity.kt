package com.example.mydicodingevent.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mydicodingevent.R
import com.example.mydicodingevent.data.Resource
import com.example.mydicodingevent.data.local.entity.FavoriteEvent
import com.example.mydicodingevent.data.remote.response.Event
import com.example.mydicodingevent.databinding.ActivityDetailEventBinding
import com.example.mydicodingevent.ui.ViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel by viewModels<DetailEventViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private val favoriteViewModel: FavoriteFavViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private var isFavorite = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        eventId?.let { viewModel.fetchDetailEvents(it) }

        setEventData()

        binding.btnFav.setOnClickListener {
            toggleFavorite()
        }


    }

    private fun toggleFavorite() {

        val currentEvent = viewModel.detailEvent.value?.data ?: return
        val favoriteEvent = FavoriteEvent(
            id = currentEvent.id.toString(),
            name = currentEvent.name,
            summary = currentEvent.summary,
            imageLogo = currentEvent.mediaCover)

        if (isFavorite) {
            favoriteViewModel.delete(favoriteEvent)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoriteViewModel.insert(favoriteEvent)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        isFavorite = !isFavorite
        updateFavoriteIcon()
    }


    private fun updateFavoriteIcon() {
        binding.btnFav.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_on else R.drawable.ic_favorite_off
        )
    }

    private fun checkFavoriteStatus(eventId: String) {
        favoriteViewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteIcon()
        }
    }



    private fun setEventData() {

        viewModel.detailEvent.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    result.data?.let { event ->
                        showLoading(false)
                        updateUI(event)
                        checkFavoriteStatus(event.id.toString())
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this@DetailEventActivity, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(event: Event) {
        val totalQuota = event.quota - event.registrants
        with(binding) {
            tvDetCategory.text = event.category
            tvDetOwnerName.text = event.ownerName
            tvDetDesc.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvDetName.text = event.name
            tvDetQuotaLeft.text = totalQuota.toString()
            tvDetCityName.text = event.cityName
            tvDetTime.text = dateFormat(event.beginTime, event.endTime)
            Glide.with(this@DetailEventActivity)
                .load(event.mediaCover)
                .into(ivDetMediaCover)

            btnLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun dateFormat(beginTime: String, endTime: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val output = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        try {
            val beginDate = input.parse(beginTime)
            val endDate = input.parse(endTime)
            return "${beginDate?.let { output.format(it) }} - ${endDate?.let {
                output.format(
                    it
                )
            }}"
        } catch (e: ParseException) {
            e.printStackTrace()
            return "$beginTime - $endTime"
        }
    }

    companion object {
        private const val EXTRA_EVENT_ID = "extra_event_id"

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, DetailEventActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            context.startActivity(intent)
        }
    }
}

