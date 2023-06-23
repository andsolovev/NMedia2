package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.error.observe(viewLifecycleOwner) {
            when (it) {
                is AppError -> Toast.makeText(
                    context,
                    R.string.incorrect_login_password,
                    Toast.LENGTH_LONG
                ).show()
                else -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }

        binding.signInButton.setOnClickListener {
            if (binding.login.text.isBlank() || binding.password.text.isBlank()) {
                Toast.makeText(context, R.string.enter_login_and_password, Toast.LENGTH_LONG).show()
            } else {
                viewModel.updateUser(
                    binding.login.text.toString(),
                    binding.password.text.toString()
                )
            }

        }

        viewModel.data.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, R.string.you_are_logged_in, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }

        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        return binding.root
    }
}


















