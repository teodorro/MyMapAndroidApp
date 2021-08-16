package com.example.mymapandroidapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.mymapandroidapp.databinding.FragmentPointItemBinding
import com.example.mymapandroidapp.dto.MyPoint

class MyPointViewHolder(
    private val binding: FragmentPointItemBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

//    val locationView: MaterialTextView = binding.location
//    val titleView: MaterialTextView = binding.title
//
//    override fun toString(): String {
//        return super.toString() + " '" + titleView.text + "'"
//    }

    fun bind(point: MyPoint){
        //TODO:
        binding.apply {
            location.text = point.latitude.toString() + " " + point.longitude.toString()
            title.text = point.title

            binding.location.setOnClickListener{
                onInteractionListener.onSelect(point)
            }
        }
    }
}