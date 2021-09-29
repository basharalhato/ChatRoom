package dev.bhato.chatroom.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.databinding.FragmentProfileBinding
import dev.bhato.chatroom.models.User
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.ChatroomViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ChatroomViewModel by viewModels()

    lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        initUI()
        setupListeners()
        setupObservers()
    }

    private fun initUI() {
        viewModel.getCurrentlyLoggedInUser()
    }

    private fun setupListeners() {
        binding.apply {
            tvLogout.setOnClickListener {
                confirmLogout()
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to Logout")
            .setPositiveButton("Logout") { dialog, _ ->
                viewModel.logout()
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                )
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.currentUserState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    val user = it.data
                    setUserDetails(user)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUserDetails(user: User?) {
        binding.apply {
            Glide.with(requireContext()).load(user?.profile_image).into(ivUserImage)
            tvUserEmail.text = user?.email
            tvUsername.text = user?.username
        }
    }
}