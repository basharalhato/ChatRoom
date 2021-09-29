package dev.bhato.chatroom.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.adapters.ChatMessageAdapter
import dev.bhato.chatroom.databinding.FragmentChatroomBinding
import dev.bhato.chatroom.models.Chatroom
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.ChatroomViewModel

@AndroidEntryPoint
class ChatroomFragment : Fragment(R.layout.fragment_chatroom) {

    private val args: ChatroomFragmentArgs by navArgs()

    private val viewModel: ChatroomViewModel by viewModels()

    private lateinit var chatroom: Chatroom
    private lateinit var chatMessageAdapter: ChatMessageAdapter

    lateinit var binding: FragmentChatroomBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatroomBinding.bind(view)

        chatroom = args.chatroom
        joinChatroom()

        initUI()
        setupListeners()
        setUpRecyclerView()
        setupObservers()
    }

    private fun joinChatroom() {
        viewModel.joinChatroom(chatroom.chatroom_id)
    }

    private fun initUI() {
        binding.apply {
            Glide.with(requireContext())
                .load(chatroom.image)
                .placeholder(ivChatroomImage.drawable)
                .into(ivChatroomImage)

            tvChatroom.text = chatroom.title
        }

        getChatMessages()
    }

    private fun setupListeners() {
        binding.apply {
            ivBackBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            ivSendBtn.setOnClickListener {
                insertNewMessage()
                clearMessage()
            }

            toolbarChats.setOnClickListener {
                findNavController().navigate(
                    ChatroomFragmentDirections.actionChatroomFragmentToChatroomDetailsFragment(
                        chatroom
                    )
                )
            }
        }
    }

    private fun insertNewMessage() {
        val message = binding.etMessage.text.toString()
        if (message.isNotBlank()) {
            viewModel.insertNewMessage(chatroom.chatroom_id, message)
        }
    }

    private fun getChatMessages() {
        viewModel.getChatMessages(chatroom.chatroom_id)
    }

    private fun setupObservers() {
        viewModel.newMessageState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    scrollToBottom()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.unsetNewMessageState()
        })

        viewModel.chatMessagesState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillFetch(false)
                    it.data?.let { chatMessageList ->
                        chatMessageAdapter.submitList(chatMessageList)
                        scrollToBottom()
                    }
                }
                is Resource.Error -> {
                    progressBarTillFetch(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    progressBarTillFetch(true)
                }
            }
            viewModel.unsetChatMessagesState()
        })
    }

    private fun clearMessage() {
        binding.etMessage.setText("")
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvChat.apply {
                chatMessageAdapter = ChatMessageAdapter()
                adapter = chatMessageAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            rvChat.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (bottom < oldBottom) {
                    scrollToBottom()
                }
            }
        }
    }

    private fun scrollToBottom() {
        if (chatMessageAdapter.currentList.size > 0) {
            binding.rvChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
        }
    }

    private fun progressBarTillFetch(visible: Boolean) {
        binding.progressBarFetching.isVisible = visible
    }
}