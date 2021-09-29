package dev.bhato.chatroom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.bhato.chatroom.databinding.LayoutItemChatroomUserBinding
import dev.bhato.chatroom.models.User

class ChatroomUsersAdapter :
    ListAdapter<User, ChatroomUsersAdapter.ChatroomUsersViewHolder>(CHATROOMUSERS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomUsersViewHolder {
        val binding = LayoutItemChatroomUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatroomUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatroomUsersViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    class ChatroomUsersViewHolder(private val binding: LayoutItemChatroomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatroomUser: User) {

            binding.apply {
                Glide.with(itemView)
                    .load(chatroomUser.profile_image)
                    .placeholder(ivUserImage.drawable)
                    .into(ivUserImage)

                tvUsername.text = chatroomUser.username
            }
        }
    }

    companion object {
        private val CHATROOMUSERS_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.user_id == newItem.user_id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}