package com.example.mymapandroidapp.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymapandroidapp.dto.MyPoint
import com.google.android.gms.maps.model.LatLng

@Entity
data class MyPointEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
//    @Embedded
//    val position: LatLngEmbeddable,
    val latitude: Double,
    val longitude: Double,
    val title: String
){
//    fun toDto() = MyPoint(id, position.toDto(), title)
//    fun toDto() = MyPoint(id, LatLng(latitude, longitude), title)
    fun toDto() = MyPoint(id, latitude, longitude, title)

    companion object{
//        fun fromDto(dto: MyPoint) = MyPointEntity(dto.id, LatLngEmbeddable.fromDto(dto.position), dto.title)
//        fun fromDto(dto: MyPoint) = MyPointEntity(dto.id, dto.position.latitude, dto.position.longitude, dto.title)
        fun fromDto(dto: MyPoint) = MyPointEntity(dto.id, dto.latitude, dto.longitude, dto.title)
    }
}

fun List<MyPointEntity>.toDto(): List<MyPoint> = map(MyPointEntity::toDto)