package com.example.mymapandroidapp.entity

import com.google.android.gms.maps.model.LatLng

data class LatLngEmbeddable(
    var lat: Double,
    var lng: Double
){
    fun toDto() = LatLng(lat, lng)

    companion object{
        fun fromDto(dto: LatLng) = dto.let { LatLngEmbeddable(dto.latitude, dto.longitude) }
    }
}