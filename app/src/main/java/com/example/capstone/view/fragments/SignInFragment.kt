package com.example.capstone.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.capstone.R
import com.example.capstone.databinding.FragmentSignInBinding
import com.example.capstone.view.viewmodel.SignInViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result
import com.example.capstone.view.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignInFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSignInBinding

    private val viewModel by viewModels<SignInViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(true)

            bottomSheetDialog.behavior.peekHeight = 1500
            bottomSheetDialog.behavior.maxHeight = 1500
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.signIn(email, password).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        Log.d("API_REQUEST", "email: $email, password: $password")
                    }
                    is Result.Loading -> {
                        binding.linearProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.linearProgressBar.visibility = View.GONE
                        if (result.data.error == true) {
                            Toast.makeText(requireContext(), result.data.message, Toast.LENGTH_SHORT).show()
                            Log.d("API_REQUEST", "email: $email, password: $password")
                        } else {
                            result.data.loginResult?.let {
                                Log.d("API_REQUEST", "email: $email, password: $password")
                                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                viewModel.saveToken(it.accessToken ?: "")
                                print(it.accessToken)
//                                updateWidget()
                                parentFragmentManager.popBackStack()
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                dismiss()

                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // Menghilangkan action bar
//        activity?.actionBar?.hide()
        requireActivity().actionBar?.hide()
    }
}
