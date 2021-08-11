package com.example.mymapandroidapp.dto

import com.google.android.gms.maps.model.LatLng

data class MyPoint(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val title: String
)

