package com.jamiehughes.gameoflifev2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChallengeAdapter(private var challenges: List<Challenge>,
                       private val onCompleteChallenge: (Challenge) -> Unit
) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    inner class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val challengeName: TextView = itemView.findViewById(R.id.challengeNameTextView)
        val challengeCategory: TextView = itemView.findViewById(R.id.challengeCategoryTextView)


        val challengeXP: TextView = itemView.findViewById(R.id.challengeXPTextView)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.challenge_item, parent, false)
        return ChallengeViewHolder(itemView)
    }

    fun updateChallenges(newChallenges: List<Challenge>) {
        this.challenges = newChallenges
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val currentChallenge = challenges[position]
        holder.challengeName.text = currentChallenge.name
        holder.challengeCategory.text = " ${currentChallenge.statCategory}"  // explicitly specify
        holder.challengeXP.text = "XP: ${currentChallenge.xp}"

        holder.completeButton.setOnClickListener {
            onCompleteChallenge(currentChallenge)
        }
    }


    override fun getItemCount() = challenges.size
}
