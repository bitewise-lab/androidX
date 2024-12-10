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
import com.example.capstone.databinding.ItemPostPerAccountBinding

class MyPostAdapter : ListAdapter<Post, MyPostAdapter.MyViewHolder>(DIFF_CALLBACKS) {

    class MyViewHolder(private val binding: ItemPostPerAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            Glide.with(itemView.context)
                .load(post.photoUrl)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.imgPosted)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemPostPerAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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