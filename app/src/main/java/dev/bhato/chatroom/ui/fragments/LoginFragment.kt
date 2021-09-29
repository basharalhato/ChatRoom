package dev.bhato.chatroom.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private var btnLogin: Button? = null
    private var progressBar: ProgressBar? = null
    private var tvMoveToSignUp: TextView? = null
    private var etLoginEmail: TextInputEditText? = null
    private var etLoginPass: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyUserIsLoggedIn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin = view.findViewById(R.id.btnLogIn)
        progressBar = view.findViewById(R.id.progressBar)
        tvMoveToSignUp = view.findViewById(R.id.tvMoveToSignUp)
        etLoginEmail = view.findViewById(R.id.etLoginEmail)
        etLoginPass = view.findViewById(R.id.etLoginPassword)

        setupOnClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToChatsFragment()
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
        progressBar?.isVisible = visible
        if (visible) {
            btnLogin?.visibility = View.INVISIBLE
        } else {
            btnLogin?.visibility = View.VISIBLE
        }
    }

    private fun setupOnClickListeners() {
        tvMoveToSignUp?.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        btnLogin?.setOnClickListener {
            val email = etLoginEmail?.text.toString()
            val password = etLoginPass?.text.toString()
            viewModel.loginUser(email, password)
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToChatsFragment()
            )
        }
    }
}
