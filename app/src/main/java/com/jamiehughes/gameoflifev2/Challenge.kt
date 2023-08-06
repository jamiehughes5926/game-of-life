package com.jamiehughes.gameoflifev2

import kotlinx.serialization.Serializable

@Serializable
data class Challenge(
    val id: String,
    val name: String,
    val statCategory: String,
    val xp: Int,
    val statPoints: Int,
    val isCompleted: Boolean = false
)
