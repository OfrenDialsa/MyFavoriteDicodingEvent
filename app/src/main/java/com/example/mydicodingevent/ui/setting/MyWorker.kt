package com.example.mydicodingevent.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mydicodingevent.R
import com.example.mydicodingevent.data.remote.response.EventResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val NOTIFICATION_ID = 2
        const val CHANNEL_ID = "channel_02"
        const val CHANNEL_NAME = "Nero channel"
    }


    override fun doWork(): Result {
        return getUpcomingEvent()
    }

    private fun getUpcomingEvent(): Result {
        Log.d(TAG, "getUpcomingEvent: Starting...")
        val client = OkHttpClient()
        val url = "https://event-api.dicoding.dev/events?active=-1&limit=1"
        Log.d(TAG, "getUpcomingEvent: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            val result = response.body?.string() ?: throw Exception("Response body is null")
            Log.d(TAG, result)

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val jsonAdapter = moshi.adapter(EventResponse::class.java)
            val eventResponse = jsonAdapter.fromJson(result)

            eventResponse?.let {
                if (it.listEvents.isNotEmpty()) {
                    val title = it.listEvents[0].name
                    val message ="Recommendation event for you on " + dateFormat(it.listEvents[0].beginTime, it.listEvents[0].endTime)
                    showNotification(title, message)
                } else {
                    Log.d(TAG, "No events found.")
                }
            } ?: run {
                Log.d(TAG, "Response is null.")
            }

            Result.success()
        } catch (e: Exception) {
            showNotification("Get Upcoming event failed", e.message)
            Log.e(TAG, "Error: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(title: String, description: String?) {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
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
}