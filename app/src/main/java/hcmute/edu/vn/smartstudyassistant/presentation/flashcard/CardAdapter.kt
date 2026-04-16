package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.CardDifficulty
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.databinding.ItemCardBinding

class CardAdapter(
    private val onCardClick: (FlashcardEntity) -> Unit
) : ListAdapter<FlashcardEntity, CardAdapter.CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: FlashcardEntity) {
            binding.tvFront.text = card.front
            binding.tvBack.text = card.back
            binding.tvDifficulty.text = card.difficulty.name
            val (bgTint, textColor) = when (card.difficulty) {
                CardDifficulty.EASY -> R.color.accent_success_bg to R.color.accent_success
                CardDifficulty.MEDIUM -> R.color.surface_elevated_light to R.color.text_secondary_dark
                CardDifficulty.HARD -> R.color.accent_urgent to R.color.white
            }
            binding.tvDifficulty.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, bgTint)
            binding.tvDifficulty.setTextColor(
                ContextCompat.getColor(binding.root.context, textColor)
            )
            binding.root.setOnClickListener { onCardClick(card) }
        }
    }

    class CardDiffCallback : DiffUtil.ItemCallback<FlashcardEntity>() {
        override fun areItemsTheSame(a: FlashcardEntity, b: FlashcardEntity) = a.id == b.id
        override fun areContentsTheSame(a: FlashcardEntity, b: FlashcardEntity) = a == b
    }
}
