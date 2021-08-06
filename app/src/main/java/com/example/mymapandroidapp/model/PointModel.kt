package com.example.mymapandroidapp.model

import com.google.android.gms.maps.model.LatLng

data class PointModel(
    val position: LatLng,
    val title: String,
    val additionalInfo: Any? = null
)

