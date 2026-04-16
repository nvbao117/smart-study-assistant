package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.data.local.dao.DeckWithCardCount
import hcmute.edu.vn.smartstudyassistant.databinding.ItemDeckBinding

class DeckAdapter(
    private val onDeckClick: (DeckWithCardCount) -> Unit
) : ListAdapter<DeckWithCardCount, DeckAdapter.DeckViewHolder>(DeckDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeckViewHolder(private val binding: ItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: DeckWithCardCount) {
            binding.tvDeckName.text = deck.name
            binding.tvCardCount.text = "${deck.totalCards} cards"
            binding.tvDueCount.text = if (deck.dueCards > 0) "${deck.dueCards} due" else "✓ Done"
            binding.root.setOnClickListener { onDeckClick(deck) }
        }
    }

    class DeckDiffCallback : DiffUtil.ItemCallback<DeckWithCardCount>() {
        override fun areItemsTheSame(a: DeckWithCardCount, b: DeckWithCardCount) = a.id == b.id
        override fun areContentsTheSame(a: DeckWithCardCount, b: DeckWithCardCount) = a == b
    }
}
