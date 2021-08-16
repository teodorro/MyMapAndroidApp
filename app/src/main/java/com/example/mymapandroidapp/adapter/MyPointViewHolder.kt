package com.example.mymapandroidapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.mymapandroidapp.databinding.FragmentPointItemBinding
import com.example.mymapandroidapp.dto.MyPoint

class MyPointViewHolder(
    private val binding: FragmentPointItemBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(point: MyPoint){
        binding.apply {
            location.text = "location: " + point.latitude.toString() + " " + point.longitude.toString()
            title.text = point.title

            binding.avatar.setOnClickListener{
                onInteractionListener.onSelect(point)
            }
        }
    }
}