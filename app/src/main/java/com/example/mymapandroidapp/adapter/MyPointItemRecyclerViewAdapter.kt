package com.example.mymapandroidapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.mymapandroidapp.databinding.FragmentPointItemBinding
import com.example.mymapandroidapp.dto.MyPoint


interface OnInteractionListener {
    fun onSelect(point: MyPoint) {}
    fun onDelete(point: MyPoint) {}
    fun onEdit(point: MyPoint) {}
}

class MyPointItemRecyclerViewAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<MyPoint, MyPointViewHolder>(MyPointDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPointViewHolder {
        var binding =
            FragmentPointItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyPointViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: MyPointViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}