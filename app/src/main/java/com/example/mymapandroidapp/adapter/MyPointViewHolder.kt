package com.example.mymapandroidapp.adapter

import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymapandroidapp.databinding.FragmentPointItemBinding
import com.example.mymapandroidapp.dto.MyPoint

class MyPointViewHolder(
    private val binding: FragmentPointItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    var pointId: Long = -1

    fun bind(point: MyPoint, clickListener: OnItemClickListener){
        binding.apply {
            location.text = "location: " + point.latitude.toString() + " " + point.longitude.toString()
            title.text = point.title
            pointId = point.id
        }

        itemView.setOnClickListener {
            clickListener.onItemClicked(point)
        }
    }
}