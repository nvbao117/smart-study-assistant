package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole
import hcmute.edu.vn.smartstudyassistant.databinding.ItemMessageAiBinding
import hcmute.edu.vn.smartstudyassistant.databinding.ItemMessageUserBinding

class MessageAdapter : ListAdapter<ChatMessageEntity, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_AI = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).role == MessageRole.USER) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            UserMessageViewHolder(
                ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            AiMessageViewHolder(
                ItemMessageAiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AiMessageViewHolder -> holder.bind(message)
        }
    }

    class UserMessageViewHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageEntity) {
            binding.tvContent.text = message.content
        }
    }

    class AiMessageViewHolder(private val binding: ItemMessageAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageEntity) {
            binding.tvContent.text = message.content
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessageEntity>() {
        override fun areItemsTheSame(a: ChatMessageEntity, b: ChatMessageEntity) = a.id == b.id
        override fun areContentsTheSame(a: ChatMessageEntity, b: ChatMessageEntity) = a == b
    }
}
