package com.example.mymapandroidapp.model

import com.example.mymapandroidapp.dto.MyPoint

data class FeedModel(
    val points: List<MyPoint> = emptyList(),
    val empty: Boolean = false,
)
