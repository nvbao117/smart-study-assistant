package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.databinding.ItemChatSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatSessionAdapter(
    private val onSessionClick: (ChatSessionEntity) -> Unit,
    private val onDeleteClick: (ChatSessionEntity) -> Unit
) : ListAdapter<ChatSessionEntity, ChatSessionAdapter.SessionViewHolder>(SessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemChatSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SessionViewHolder(private val binding: ItemChatSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: ChatSessionEntity) {
            binding.tvTitle.text = session.title
            val timeAgo = formatTimeAgo(session.createdAt)
            binding.tvProvider.text = "${session.aiProvider.name.lowercase().replaceFirstChar { it.uppercase() }} • $timeAgo"
            binding.root.setOnClickListener { onSessionClick(session) }
            binding.btnDelete.setOnClickListener { onDeleteClick(session) }
        }

        private fun formatTimeAgo(millis: Long): String {
            val diff = System.currentTimeMillis() - millis
            return when {
                diff < 60_000 -> "Just now"
                diff < 3_600_000 -> "${diff / 60_000}m ago"
                diff < 86_400_000 -> "${diff / 3_600_000}h ago"
                else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))
            }
        }
    }

    class SessionDiffCallback : DiffUtil.ItemCallback<ChatSessionEntity>() {
        override fun areItemsTheSame(a: ChatSessionEntity, b: ChatSessionEntity) = a.id == b.id
        override fun areContentsTheSame(a: ChatSessionEntity, b: ChatSessionEntity) = a == b
    }
}
