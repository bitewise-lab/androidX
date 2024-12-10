package com.example.capstone.view.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.remote.response.CommentRequest
import com.example.capstone.databinding.ItemCommentBinding

class CommentAdapter : ListAdapter<CommentRequest, CommentAdapter.MyViewHolder>(DIFF_CALLBACKS) {

    class MyViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentRequest) {
            binding.txtNameUser.text = comment.commenterName
            binding.txtDescription.text = comment.comment
            Glide.with(itemView.context)
                .load(comment.imageUser)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.imgUser)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemCommentBinding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemCommentBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val postItem = getItem(position)
        holder.bind(postItem)
    }

    companion object {
        val DIFF_CALLBACKS = object : DiffUtil.ItemCallback<CommentRequest>() {
            override fun areItemsTheSame(oldItem: CommentRequest, newItem: CommentRequest): Boolean {
                return oldItem.commenterId == newItem.commenterId
            }

            override fun areContentsTheSame(oldItem: CommentRequest, newItem: CommentRequest): Boolean {
                return oldItem.commenterId == newItem.commenterId
            }
        }
    }
}