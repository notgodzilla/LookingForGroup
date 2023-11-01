package com.notgodzilla.lookingforgroup.polling

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notgodzilla.lookingforgroup.MainActivity
import com.notgodzilla.lookingforgroup.NOTIFICATION_CHANNEL_ID
import com.notgodzilla.lookingforgroup.R
import com.notgodzilla.lookingforgroup.networking.XIVPFRepository
import com.notgodzilla.lookingforgroup.preferences.PreferencesRepository
import kotlinx.coroutines.flow.last

//TODO Prepare for tracking
class PFTracker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        //TODO Inject both of these?
        val repo = XIVPFRepository()
        val prefRepo = PreferencesRepository.get()

        val lastSlotsFilledCount = prefRepo.lastSlotsFilledCount.last()
        return try {

            val slotsFilled =
                //TODO Will remove this hardcoded value
                repo.getPFByAuthor("Herja Avagnar @ Malboro")?.getElementById("total")?.text() ?: ""
            if (slotsFilled.isNotEmpty()) {
                if (slotsFilled != lastSlotsFilledCount) {
                    prefRepo.setSlotsFilledCount(slotsFilled)
                    notifyUser()
                } else {
                    Log.i(TAG, "Slots filled have not changed")
                }
            } else {
                Log.i(TAG, "PF Not Found")
                return Result.success()
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val resources = context.resources
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.slots_filled_change))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_party_member))
            .setContentText(resources.getString(R.string.new_party_members_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }
}