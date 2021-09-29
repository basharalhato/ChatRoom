package dev.bhato.chatroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.bhato.chatroom.databinding.LayoutItemChatroomBinding
import dev.bhato.chatroom.models.Chatroom

class ChatroomsAdapter :
    ListAdapter<Chatroom, ChatroomsAdapter.ChatroomsViewHolder>(CHATROOM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomsViewHolder {
        val binding =
            LayoutItemChatroomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatroomsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatroomsViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ChatroomsViewHolder(private val binding: LayoutItemChatroomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatroom: Chatroom) {
            binding.apply {
                Glide.with(itemView).load(chatroom.image)
                    .placeholder(ivChatroomImage.drawable).into(ivChatroomImage)

                tvChatroomName.text = chatroom.title

                itemView.setOnClickListener {
                    onChatroomItemClickListener?.let {
                        it(chatroom)
                    }
                }
            }
        }
    }

    fun setOnChatroomItemClickListener(listener: (Chatroom) -> Unit) {
        onChatroomItemClickListener = listener
    }

    private var onChatroomItemClickListener: ((Chatroom) -> Unit)? = null

    companion object {
        private val CHATROOM_COMPARATOR = object : DiffUtil.ItemCallback<Chatroom>() {
            override fun areItemsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
                return oldItem.chatroom_id == newItem.chatroom_id
            }

            override fun areContentsTheSame(oldItem: Chatroom, newItem: Chatroom) =
                oldItem == newItem
        }
    }
}