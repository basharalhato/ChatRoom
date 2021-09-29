package dev.bhato.chatroom.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.adapters.ChatroomUsersAdapter
import dev.bhato.chatroom.databinding.FragmentChatroomDetailsBinding
import dev.bhato.chatroom.models.Chatroom
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.ChatroomViewModel

@AndroidEntryPoint
class ChatroomDetailsFragment : Fragment(R.layout.fragment_chatroom_details) {

    private val viewModel: ChatroomViewModel by viewModels()
    private val args: ChatroomDetailsFragmentArgs by navArgs()

    private lateinit var chatroom: Chatroom
    private lateinit var chatroomUsersAdapter: ChatroomUsersAdapter

    lateinit var binding: FragmentChatroomDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatroomDetailsBinding.bind(view)
        chatroom = args.chatroom

        initUI()
        setupListeners()
        setUpRecyclerView()
        setupObservers()
    }

    private fun initUI() {
        binding.apply {
            Glide.with(requireContext())
                .load(chatroom.image)
                .placeholder(ivChatroomImage.drawable)
                .into(ivChatroomImage)

            tvChatroomName.text = chatroom.title
        }

        getChatroomUsers()
    }

    private fun getChatroomUsers() {
        viewModel.getChatroomUsers(chatroom.chatroom_id)
    }

    private fun setupListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            tvLeaveChatroom.setOnClickListener {
                leaveChatroom()
            }

            ivMap.setOnClickListener {
                findNavController().navigate(
                    ChatroomDetailsFragmentDirections.actionChatroomDetailsFragmentToChatroomMapFragment(chatroom)
                )
            }
        }
    }

    private fun leaveChatroom() {
        viewModel.leaveChatroom(chatroom.chatroom_id)
        findNavController().navigate(
            ChatroomDetailsFragmentDirections.actionChatroomDetailsFragmentToChatsFragment()
        )
    }

    private fun setupObservers() {
        viewModel.chatroomUsersState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { userList ->
                        chatroomUsersAdapter.submitList(userList)
                        viewModel.getUserLocation(userList)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvUsers.apply {
                chatroomUsersAdapter = ChatroomUsersAdapter()
                adapter = chatroomUsersAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }
}