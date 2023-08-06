import com.jamiehughes.gameoflifev2.Challenge
import java.util.*

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.content.Context
import java.util.UUID

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("YourPreferenceName", Context.MODE_PRIVATE)

    var level: MutableLiveData<Int> = MutableLiveData()
    var xp: MutableLiveData<Int> = MutableLiveData()
    var strength: MutableLiveData<Int> = MutableLiveData()
    var iq: MutableLiveData<Int> = MutableLiveData()
    var luck: MutableLiveData<Int> = MutableLiveData()
    var stamina: MutableLiveData<Int> = MutableLiveData()
    var willpower: MutableLiveData<Int> = MutableLiveData()
    var challenges: MutableLiveData<List<Challenge>> = MutableLiveData(emptyList())

    var userName: MutableLiveData<String> = MutableLiveData()

    val selectedChallenges: List<Challenge> by lazy {
        selectTodayChallenges()
        challenges.value ?: emptyList()
    }

    init {
        loadStats()
//        if (challenges.value.isNullOrEmpty()) {
//            selectTodayChallenges()
//        }
    }

    private fun loadStats() {
        level.value = sharedPreferences.getInt("level", 1)
        xp.value = sharedPreferences.getInt("xp", 0)
        strength.value = sharedPreferences.getInt("strength", 0)
        iq.value = sharedPreferences.getInt("iq", 0)
        luck.value = sharedPreferences.getInt("luck", 0)
        stamina.value = sharedPreferences.getInt("stamina", 0)
        willpower.value = sharedPreferences.getInt("willpower", 0)
        userName.value = sharedPreferences.getString("userName", "Guest")
    }

    private fun saveStat(key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    private fun saveUserName(name: String) {
        with(sharedPreferences.edit()) {
            putString("userName", name)
            apply()
        }
    }

    fun xpForNextLevel(): Int = (level.value ?: 1) * (level.value ?: 1) * 100

    fun addXP(newXP: Int) {
        var updatedXP = (xp.value ?: 0) + newXP
        var requiredXP = xpForNextLevel()

        while (updatedXP >= requiredXP) {
            levelUp()
            updatedXP -= requiredXP
            requiredXP = xpForNextLevel()
        }

        xp.value = updatedXP
        saveStat("xp", updatedXP)
    }

    fun initializeChallengesIfNeeded() {
        if (challenges.value.isNullOrEmpty()) {
            selectTodayChallenges()
        }
    }

    fun selectTodayChallenges() {
        val randomChallenges = challengeBank.shuffled().take(3)
        challenges.value = randomChallenges
    }

    fun refreshChallenges() {
        selectTodayChallenges()
    }

    fun completeChallenge(challenge: Challenge) {
        val currentChallenges = challenges.value?.toMutableList() ?: mutableListOf()
        currentChallenges.remove(challenge)
        challenges.value = currentChallenges
        addXP(challenge.xp)
        updateStat(challenge.statCategory, challenge.statPoints)
    }

    fun updateStat(category: String, points: Int) {
        when (category.toLowerCase(Locale.ROOT)) {
            "strength" -> {
                strength.value = (strength.value ?: 0) + points
                saveStat("strength", strength.value ?: 0)
            }
            "iq" -> {
                iq.value = (iq.value ?: 0) + points
                saveStat("iq", iq.value ?: 0)
            }
            "luck" -> {
                luck.value = (luck.value ?: 0) + points
                saveStat("luck", luck.value ?: 0)
            }
            "stamina" -> {
                stamina.value = (stamina.value ?: 0) + points
                saveStat("stamina", stamina.value ?: 0)
            }
            "willpower" -> {
                willpower.value = (willpower.value ?: 0) + points
                saveStat("willpower", willpower.value ?: 0)
            }
            else -> throw IllegalArgumentException("Unknown stat category")
        }
    }

    fun updateUserName(name: String) {
        userName.value = name
        saveUserName(name)
    }

    private fun levelUp() {
        level.value = (level.value ?: 1) + 1
        saveStat("level", level.value ?: 1)

        // Increase stats by one each time you level up
        strength.value?.let { saveStat("strength", it + 1) }
        iq.value?.let { saveStat("iq", it + 1) }
        luck.value?.let { saveStat("luck", it + 1) }
        stamina.value?.let { saveStat("stamina", it + 1) }
        willpower.value?.let { saveStat("willpower", it + 1) }

        strength.value = (strength.value ?: 0) + 1
        iq.value = (iq.value ?: 0) + 1
        luck.value = (luck.value ?: 0) + 1
        stamina.value = (stamina.value ?: 0) + 1
        willpower.value = (willpower.value ?: 0) + 1
    }

    val challengeBank = listOf(
        // IQ Challenges
        Challenge(UUID.randomUUID().toString(), "Read Book", "IQ", 50, 2),
        Challenge(UUID.randomUUID().toString(), "Solve Puzzle", "IQ", 40, 2),
        Challenge(UUID.randomUUID().toString(), "Learn a New Word", "IQ", 30, 1),
        Challenge(UUID.randomUUID().toString(), "Do Math Exercise", "IQ", 45, 2),
        Challenge(UUID.randomUUID().toString(), "Play Chess", "IQ", 55, 3),

        // Strength Challenges
        Challenge(UUID.randomUUID().toString(), "Lift Weights", "Strength", 40, 1),
        Challenge(UUID.randomUUID().toString(), "Do Push-ups", "Strength", 35, 1),
        Challenge(UUID.randomUUID().toString(), "Climb Stairs", "Strength", 30, 1),
        Challenge(UUID.randomUUID().toString(), "Do Sit-ups", "Strength", 35, 1),
        Challenge(UUID.randomUUID().toString(), "Perform Squats", "Strength", 45, 2),

        // Luck Challenges
        Challenge(UUID.randomUUID().toString(), "Play Lottery", "Luck", 20, 1),
        Challenge(UUID.randomUUID().toString(), "Find a Four-Leaf Clover", "Luck", 25, 1),
        Challenge(UUID.randomUUID().toString(), "Make a Wish", "Luck", 15, 1),
        Challenge(UUID.randomUUID().toString(), "Catch a Falling Leaf", "Luck", 30, 2),
        Challenge(UUID.randomUUID().toString(), "Break a Wishbone", "Luck", 25, 1),

        // Stamina Challenges
        Challenge(UUID.randomUUID().toString(), "Run", "Stamina", 30, 1),
        Challenge(UUID.randomUUID().toString(), "Swim", "Stamina", 40, 2),
        Challenge(UUID.randomUUID().toString(), "Cycle", "Stamina", 35, 1),
        Challenge(UUID.randomUUID().toString(), "Jump Rope", "Stamina", 30, 1),
        Challenge(UUID.randomUUID().toString(), "Dance", "Stamina", 25, 1),

        // Willpower Challenges
        Challenge(UUID.randomUUID().toString(), "Meditate", "Willpower", 40, 2),
        Challenge(UUID.randomUUID().toString(), "Resist Temptation", "Willpower", 35, 1),
        Challenge(UUID.randomUUID().toString(), "Fast", "Willpower", 50, 2),
        Challenge(UUID.randomUUID().toString(), "Study Without Distraction", "Willpower", 45, 2),
        Challenge(UUID.randomUUID().toString(), "Wake Up Early", "Willpower", 30, 1)
    )
}



