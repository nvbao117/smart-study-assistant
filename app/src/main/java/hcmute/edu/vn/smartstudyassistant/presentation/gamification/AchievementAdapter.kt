package hcmute.edu.vn.smartstudyassistant.presentation.gamification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AchievementEntity
import hcmute.edu.vn.smartstudyassistant.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<AchievementEntity, AchievementAdapter.AchievementViewHolder>(DiffCallback()) {

    private val unlockedIds = mutableSetOf<Long>()

    fun setUnlocked(ids: Set<Long>) {
        unlockedIds.clear()
        unlockedIds.addAll(ids)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position), unlockedIds.contains(getItem(position).id))
    }

    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: AchievementEntity, isUnlocked: Boolean) {
            binding.tvName.text = achievement.name
            binding.tvDescription.text = achievement.description
            binding.tvXp.text = "+${achievement.xpReward} XP"

            // Set badge icon based on requirement type
            val icon = when {
                achievement.requirementType.contains("TASK") -> "✅"
                achievement.requirementType.contains("POMODORO") -> "⏱️"
                achievement.requirementType.contains("STREAK") -> "🔥"
                achievement.requirementType.contains("CARD") -> "🃏"
                else -> "⭐"
            }
            binding.tvBadgeIcon.text = icon

            // Locked/unlocked state
            binding.cardLock.visibility = if (isUnlocked) android.view.View.GONE else android.view.View.VISIBLE
            binding.cardBadge.strokeColor = if (isUnlocked)
                ContextCompat.getColor(binding.root.context, R.color.accent_success)
            else
                ContextCompat.getColor(binding.root.context, R.color.border_light)

            binding.tvName.alpha = if (isUnlocked) 1f else 0.5f
            binding.tvDescription.alpha = if (isUnlocked) 1f else 0.5f
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AchievementEntity>() {
        override fun areItemsTheSame(a: AchievementEntity, b: AchievementEntity) = a.id == b.id
        override fun areContentsTheSame(a: AchievementEntity, b: AchievementEntity) = a == b
    }
}
