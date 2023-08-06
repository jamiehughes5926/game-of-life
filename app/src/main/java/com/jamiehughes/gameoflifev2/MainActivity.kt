package com.jamiehughes.gameoflifev2

import NotificationWorker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit
import android.widget.EditText
import android.widget.LinearLayout

class MainActivity : AppCompatActivity(), OnUserNameUpdatedListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onUserNameUpdated(newName: String) {
        saveUserName(newName)
        refreshHomeFragment()
    }


    private fun isFirstRun(): Boolean {
        // Check for a flag indicating if this is the first run.
        return sharedPreferences.getBoolean("IsFirstRun", true)
    }

    private fun setFirstRunFlagShown() {
        with(sharedPreferences.edit()) {
            putBoolean("IsFirstRun", false)
            apply()
        }
    }

    fun onNotificationsToggled(enable: Boolean) {
        saveNotificationSetting(enable)
        handleNotificationState()
    }

    private fun handleNotificationState() {
        if (isNotificationsEnabled()) {
            scheduleNotificationWorker()
        } else {
            cancelNotificationWorker()
        }
    }

    private fun scheduleNotificationWorker() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "uniqueWorkName",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelNotificationWorker() {
        WorkManager.getInstance(this).cancelUniqueWork("uniqueWorkName")
    }

    private fun saveNotificationSetting(enable: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("EnableNotifications", enable)
            apply()
        }
    }

    private fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("EnableNotifications", true)
    }

    private fun saveUserName(name: String) {
        with(sharedPreferences.edit()) {
            putString("UserName", name)
            apply()
        }
    }

    private fun refreshHomeFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val homeFragment = navHostFragment?.childFragmentManager?.fragments?.get(0) as? HomeFragment
        homeFragment?.updateWelcomeText()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSharedPreferences()
        setAppTheme()
        setContentView(R.layout.activity_main)
        setupNavigation()
        manageThemeChange()
        if (isFirstRun()) {
            handleNotificationState()
            // Any other actions that should only happen on the first run.
        }
    }

    private fun initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    private fun setAppTheme() {
        val themeSetting = sharedPreferences.getString("AppTheme", "Light")
        if (themeSetting == "Dark") {
            setTheme(R.style.Theme_GameofLifev2_Dark)
        } else {
            setTheme(R.style.Theme_GameofLifev2_Light)
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    private fun manageThemeChange() {
        if (sharedPreferences.getBoolean("IsThemeChange", false)) {
            resetThemeChangeFlag()
        } else {
            createNotificationChannel()
            checkNotificationPermission()
        }
    }

    private fun resetThemeChangeFlag() {
        with(sharedPreferences.edit()) {
            putBoolean("IsThemeChange", false)
            apply()
        }
    }

    private fun promptForName() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Your Name")
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val enteredName = input.text.toString()
            saveUserName(enteredName)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Challenges"
            val descriptionText = "Reminders for daily challenges"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("dailyChallenges", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (isFirstRun()) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) {
                promptForNotificationPermission()
            }
            setFirstRunFlagShown() // Set the flag to false after showing the prompt for the first time.
        }
    }

    private fun promptForNotificationPermission() {
        val builder = AlertDialog.Builder(this)
            .setMessage("Notifications are disabled for this app. Would you like to enable them?")
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                })
                saveNotificationSetting(true)
            }
            .setNegativeButton("No", null)
        val dialog = builder.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }
}
