package ru.sokolov_diplom.nework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import ru.sokolov_diplom.nework.databinding.FragmentSignInBinding
import ru.sokolov_diplom.nework.error.ApiException
import ru.sokolov_diplom.nework.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolov_diplom.nework.R

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        authViewModel.error.observe(viewLifecycleOwner) {
            when (it) {
                is ApiException -> Toast.makeText(
                    context,
                    R.string.incorrect_credentials,
                    Toast.LENGTH_SHORT
                ).show()

                else -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.authorizeButton.setOnClickListener {
            if (binding.login.text.isBlank() && binding.password.text.isBlank()) {
                Toast.makeText(context, R.string.error_blank_auth, Toast.LENGTH_SHORT).show()
            } else if (binding.login.text.isBlank()) {
                Toast.makeText(context, R.string.error_blank_username, Toast.LENGTH_SHORT).show()
            } else if (binding.password.text.isBlank()) {
                Toast.makeText(context, R.string.error_blank_password, Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.updateUser(
                    binding.login.text.toString(),
                    binding.password.text.toString()
                )
                Toast.makeText(context, R.string.auth_successful, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signInFragment_to_navigation_posts)
            }
        }

        return binding.root
    }
}
