package com.jamiehughes.gameoflifev2


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.jamiehughes.gameoflifev2.OnUserNameUpdatedListener


class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var listener: OnUserNameUpdatedListener? = null
    private var isUserInitiatedChange = true  // Add this flag to prevent unintended recreation

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUserNameUpdatedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnUserNameUpdatedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val editTextNewName: EditText = view.findViewById(R.id.editTextNewName)
        val buttonChangeName: Button = view.findViewById(R.id.buttonChangeName)
        val enableNotificationSwitch: Switch = view.findViewById(R.id.notificationSwitch)
        val themeSwitch: Switch = view.findViewById(R.id.themeSwitch)

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Initialize the UI elements with the current settings
        editTextNewName.hint = sharedPreferences.getString("UserName", "Enter new name")
        enableNotificationSwitch.isChecked = sharedPreferences.getBoolean("EnableNotifications", true)
        themeSwitch.isChecked = sharedPreferences.getString("AppTheme", "Light") == "Dark"


        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInitiatedChange) {
                with(sharedPreferences.edit()) {
                    putString("AppTheme", if (isChecked) "Dark" else "Light")
                    putBoolean("IsThemeChange", true)  // Add this line
                    apply()
                }
                activity?.recreate()
            }
        }



        // Inside SettingsFragment
        enableNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInitiatedChange) {
                with(sharedPreferences.edit()) {
                    putBoolean("EnableNotifications", isChecked)
                    apply()
                }
                (activity as? MainActivity)?.onNotificationsToggled(isChecked)
            }
        }



        buttonChangeName.setOnClickListener {
            val newName = editTextNewName.text.toString()
            if (newName.isNotBlank()) {
                with(sharedPreferences.edit()) {
                    putString("UserName", newName)
                    apply()
                }
                listener?.onUserNameUpdated(newName)
            }
        }

        // Set the flag to true after the initial setup is done
        isUserInitiatedChange = true

        return view
    }
}

