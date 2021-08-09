package com.example.mymapandroidapp.dto

import com.google.android.gms.maps.model.LatLng

data class MyPoint(
    val id: Long,
    val position: LatLng,
    val title: String
)

