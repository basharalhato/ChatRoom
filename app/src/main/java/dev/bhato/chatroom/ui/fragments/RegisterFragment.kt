package dev.bhato.chatroom.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.databinding.FragmentRegisterBinding
import dev.bhato.chatroom.utils.Constants.IMAGE_PICK_CODE
import dev.bhato.chatroom.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import dev.bhato.chatroom.utils.GalleryUtility
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.AuthViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register), EasyPermissions.PermissionCallbacks {

    private val viewModel: AuthViewModel by viewModels()

    lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        setupOnClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.profileImageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext()).load(it).into(binding.btnChooseImg)
            binding.btnChooseImg.alpha = 0f
        })

        viewModel.registerState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToChatsFragment()
                    )
                }

                is Resource.Error -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBar(true)
                }
            }
        })
    }

    private fun progressBar(visible: Boolean) {
        binding.apply {
            progressBar.isVisible = visible
            if(visible) {
                btnCreateAccount.visibility = View.INVISIBLE
            } else {
                btnCreateAccount.visibility = View.VISIBLE
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.apply {
            btnChooseImg.setOnClickListener {
                requestPermissions()
            }

            btnCreateAccount.setOnClickListener {
                val username = etRegisterUsername.text.toString()
                val email = etRegisterEmail.text.toString()
                val password = etRegisterPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                viewModel.registerUser(username, email, password, confirmPassword)
            }

            tvMoveToLogin.setOnClickListener {
                findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
            }
        }
    }

    private fun requestPermissions() {
        if (GalleryUtility.hasStoragePermission(requireContext())) {
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept storage permission",
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        pickImageFromGallery()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data.let { uri ->
                uri?.let {
                    viewModel.setProfileImage(it)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}