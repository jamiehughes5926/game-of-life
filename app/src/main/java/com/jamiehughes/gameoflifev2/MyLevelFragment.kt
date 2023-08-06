package com.jamiehughes.gameoflifev2

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class MyLevelFragment : Fragment() {

    private lateinit var levelTextView: TextView
    private lateinit var xpTextView: TextView
    private lateinit var strengthTextView: TextView
    private lateinit var iqTextView: TextView
    private lateinit var luckTextView: TextView
    private lateinit var staminaTextView: TextView
    private lateinit var willpowerTextView: TextView

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var xpCircleProgressBar: ProgressBar
    private lateinit var strengthProgressBar: ProgressBar
    private lateinit var iqProgressBar: ProgressBar
    private lateinit var staminaProgressBar: ProgressBar
    private lateinit var luckProgressBar: ProgressBar
    private lateinit var willpowerProgressBar: ProgressBar








    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize views
        levelTextView = view.findViewById(R.id.levelTextView)
        xpTextView = view.findViewById(R.id.xpTextView)
        strengthTextView = view.findViewById(R.id.strengthTextView)
        strengthProgressBar = view.findViewById(R.id.strengthProgressBar)
        iqTextView = view.findViewById(R.id.iqTextView)
        iqProgressBar = view.findViewById(R.id.strengthProgressBar)
        luckTextView = view.findViewById(R.id.luckTextView)
        luckProgressBar = view.findViewById(R.id.strengthProgressBar)
        staminaTextView = view.findViewById(R.id.staminaTextView)
        staminaProgressBar = view.findViewById(R.id.strengthProgressBar)
        willpowerTextView = view.findViewById(R.id.willpowerTextView)
        willpowerProgressBar = view.findViewById(R.id.strengthProgressBar)

        xpCircleProgressBar = view.findViewById(R.id.xpCircleProgressBar)

        sharedViewModel.xp.observe(viewLifecycleOwner) { xp ->
            val maxXP = sharedViewModel.xpForNextLevel()
            xpCircleProgressBar.max = maxXP
            xpCircleProgressBar.progress = xp
        }
        // Observe changes in ViewModel
        sharedViewModel.level.observe(viewLifecycleOwner) { level ->
            levelTextView.text = "Level $level"
        }

        sharedViewModel.xp.observe(viewLifecycleOwner) { xp ->
            xpTextView.text = "XP: $xp"
        }

        sharedViewModel.strength.observe(viewLifecycleOwner) { newStrength ->
            strengthTextView.text = "Strength: $newStrength"
        }

        sharedViewModel.iq.observe(viewLifecycleOwner) { newIQ ->
            iqTextView.text = "IQ: $newIQ"
        }

        sharedViewModel.luck.observe(viewLifecycleOwner) { newLuck ->
            luckTextView.text = "Luck: $newLuck"
        }

        sharedViewModel.stamina.observe(viewLifecycleOwner) { newStamina ->
            staminaTextView.text = "Stamina: $newStamina"
        }

        sharedViewModel.willpower.observe(viewLifecycleOwner) { newWillpower ->
            willpowerTextView.text = "Willpower: $newWillpower"
        }


//        // Initialize buttons and their click handlers
//        val levelUpButton: Button = view.findViewById(R.id.levelUpButton)
//        levelUpButton.setOnClickListener {
//            sharedViewModel.addXP(100)  // For example, leveling up needs 100 XP
//        }
//
//        val resetLevelButton: Button = view.findViewById(R.id.resetLevelButton)
//        resetLevelButton.setOnClickListener {
//            // Reset logic if needed
//        }
//
//        val addExpButton: Button = view.findViewById(R.id.addExpButton)
//        addExpButton.setOnClickListener {
//            sharedViewModel.addXP(10)  // Add 10 XP for example
//        }
    }
}
