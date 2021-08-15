package com.example.mymapandroidapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mymapandroidapp.dto.MyPoint

class MyPointDiffCallback : DiffUtil.ItemCallback<MyPoint>() {
    override fun areItemsTheSame(oldItem: MyPoint, newItem: MyPoint): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyPoint, newItem: MyPoint): Boolean {
        return oldItem == newItem
    }
}