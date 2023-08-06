package com.jamiehughes.gameoflifev2

import SharedViewModel
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class HomeFragment : Fragment() {

    private lateinit var challengeRecyclerView: RecyclerView
    private lateinit var challengeAdapter: ChallengeAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var levelTextView: TextView  // Ensure this is declared in class scope
    private lateinit var xpTextView: TextView  // Ensure this is declared in class scope

    private lateinit var welcomeTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var xpProgressBar: ProgressBar




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateWelcomeText()  // Call this method here as well

    }

     fun updateWelcomeText() {
        val userName = sharedPreferences.getString("UserName", "Guest")
        welcomeTextView.text = "Welcome $userName"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

// Initialize Level and XP TextViews
        var levelTextView: TextView = view.findViewById(R.id.levelTextView)
        var xpTextView: TextView = view.findViewById(R.id.xpTextView)

        xpProgressBar = view.findViewById(R.id.xpProgressBar)

        if (sharedViewModel.challenges.value.isNullOrEmpty()) {
            sharedViewModel.selectTodayChallenges()
        }

        // Observe changes in ViewModel
        sharedViewModel.xp.observe(viewLifecycleOwner) { xp ->
            val maxXP = sharedViewModel.xpForNextLevel() // Assuming this is public
            xpProgressBar.max = maxXP
            xpProgressBar.progress = xp
        }

        sharedViewModel.level.observe(viewLifecycleOwner) { level ->
            val maxXP = sharedViewModel.xpForNextLevel() // Assuming this is public
            xpProgressBar.max = maxXP
        }

// Observe the level and XP
        sharedViewModel.level.observe(viewLifecycleOwner) { level ->
            levelTextView.text = "Level: $level"
        }
        sharedViewModel.xp.observe(viewLifecycleOwner) { xp ->
            xpTextView.text = "XP: $xp"
        }


        // Initialize Welcome TextView
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        updateWelcomeText()  // Call this method here
        // Initialize RecyclerView
        challengeRecyclerView = view.findViewById(R.id.challengeRecyclerView)
        challengeAdapter = ChallengeAdapter(listOf()) { challenge ->
            sharedViewModel.completeChallenge(challenge)
        }
        challengeRecyclerView.adapter = challengeAdapter
        challengeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe changes in challenges
        sharedViewModel.challenges.observe(viewLifecycleOwner) { updatedChallenges ->
            challengeAdapter.updateChallenges(updatedChallenges)
        }

        val refreshChallengesButton: Button = view.findViewById(R.id.refreshChallengesButton)
        refreshChallengesButton.setOnClickListener {
            sharedViewModel.refreshChallenges()
        }


    }



}
