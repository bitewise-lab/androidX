package com.example.capstone.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.remote.response.Post
import com.example.capstone.databinding.ItemPostBinding
import com.example.capstone.view.fragments.CommentFragment

class PostAdapter : ListAdapter<Post, PostAdapter.MyViewHolder>(DIFF_CALLBACKS) {

    class MyViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.txtUsername.text = post.name
            binding.txtDescription.text = post.description
            Glide.with(itemView.context)
                .load(post.photoUrl)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.imgPost)

            Glide.with(itemView.context)
                .load(post.imageUser)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.imgUser)

            binding.btnComment.setOnClickListener {
                val postId = post.id ?: return@setOnClickListener
                Log.d("PostAdapter", "postId: $postId")
                val commentFragment = CommentFragment.newInstance(postId)
                val fragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                commentFragment.show(fragmentManager, "CommentFragment")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val postItem = getItem(position)
        holder.bind(postItem)
    }

    companion object {
        val DIFF_CALLBACKS = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}