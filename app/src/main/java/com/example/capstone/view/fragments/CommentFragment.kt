package com.example.capstone.view.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.response.CommentRequest
import com.example.capstone.databinding.FragmentCommentBinding
import com.example.capstone.view.viewmodel.CommentViewModel
import com.example.capstone.view.viewmodel.dataStore
import com.example.capstone.data.Result
import com.example.capstone.view.adapters.CommentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CommentFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCommentBinding
    private lateinit var userPref: UserPref
    private lateinit var commentAdapter: CommentAdapter
    private var postId: Int = 0

    private val viewModel by viewModels<CommentViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postId: Int): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putInt(ARG_POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getInt(ARG_POST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(true)

            bottomSheetDialog.behavior.peekHeight = 1500
            bottomSheetDialog.behavior.maxHeight = 1500
        }

        userPref = UserPref.getInstance(requireContext().dataStore)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentAdapter = CommentAdapter()
        binding.rvPost.adapter = commentAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())

        observeViewModel()
        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.commentSendButton.setOnClickListener {
            val comment = binding.commentInput.text.toString()

            lifecycleScope.launch {
                try {
                    val user = userPref.getSession().first()
                    val commenterId = user.token
                    val commenterName = user.name
                    val imageUrl = user.imageURL
                    val timestamp = System.currentTimeMillis().toString()

                    val commentRequest = CommentRequest(
                        commenterId = commenterId,
                        commenterName = commenterName,
                        comment = comment,
                        timestamp = timestamp,
                        imageUser = imageUrl
                    )

                    viewModel.postComment(postId.toString(), commentRequest).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Success -> {
                                binding.commentInput.text?.clear()
                                viewModel.refresh(postId.toString())
                                Toast.makeText(context, "Comment posted", Toast.LENGTH_SHORT).show()
                                Log.d("API_REQUEST", "comment: $comment, commenterId: $commenterId, commenterName: $commenterName, imageUser: $imageUrl, timestamp: $timestamp")
                            }
                            is Result.Error -> {
                                Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                                Log.d("API_REQUEST", "comment: $comment, commenterId: $commenterId, commenterName: $commenterName, imageUser: $imageUrl, timestamp: $timestamp")
                            }
                            is Result.Loading -> {
                                // Handle loading state
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    Log.d("CommentFragment", "Coroutine was cancelled", e)
                } catch (e: Exception) {
                    // Handle other exceptions
                    Log.e("CommentFragment", "Error posting comment", e)
                    Toast.makeText(context, "Error posting comment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.observePostComments(postId.toString()).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    commentAdapter.submitList(result.data)
                    Log.d("CommentFragment", "Data fetched: ${result.data}")
                }
                is Result.Error -> {
                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Handle loading state
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
        requireActivity().actionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }
}