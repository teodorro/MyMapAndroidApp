package com.example.mymapandroidapp.dto

import com.google.android.gms.maps.model.Marker

data class MyPointMarker(
    val point: MyPoint,
    val marker: Marker
)