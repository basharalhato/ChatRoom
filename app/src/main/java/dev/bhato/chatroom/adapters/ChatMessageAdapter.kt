package dev.bhato.chatroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.bhato.chatroom.databinding.LayoutChatItemBinding
import dev.bhato.chatroom.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter :
    ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(CHATMESSAGE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val binding =
            LayoutChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ChatMessageViewHolder(private val binding: LayoutChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) {
            binding.apply {
                Glide.with(itemView).load(chatMessage.user?.profile_image)
                    .placeholder(ivProfileImage.drawable)
                    .into(ivProfileImage)

                tvProfileName.text = chatMessage.user?.username

                tvMessage.text = chatMessage.message

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = chatMessage.timestamp
                }
                val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault())
                tvTimestamp.text = dateFormat.format(calendar.time)
            }
        }
    }

    companion object {
        private val CHATMESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.message_id == newItem.message_id
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem == newItem
        }
    }
}