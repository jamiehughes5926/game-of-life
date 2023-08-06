import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import com.jamiehughes.gameoflifev2.R
import java.util.concurrent.TimeUnit

class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val enableNotifications = sharedPreferences.getBoolean("EnableNotifications", true)

        if (enableNotifications) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(applicationContext, "dailyChallenges")
                .setContentTitle("New Challenges Await!")
                .setContentText("Check out today's challenges.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

            notificationManager.notify(1, notification)
            Log.d("NotificationWorker", "doWork called")
        }

        // Re-enqueue the work
        val nextWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(nextWorkRequest)

        return Result.success()
    }
}
