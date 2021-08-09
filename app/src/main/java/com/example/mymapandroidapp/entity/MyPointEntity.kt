package com.example.mymapandroidapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymapandroidapp.dto.MyPoint
import com.google.android.gms.maps.model.LatLng

@Entity
data class MyPointEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val position: LatLng,
    val title: String
){
    fun toDto() = MyPoint(id, position, title)

    companion object{
        fun fromDto(dto: MyPoint) = MyPointEntity(dto.id, dto.position, dto.title)
    }
}

fun List<MyPointEntity>.toDto(): List<MyPoint> = map(MyPointEntity::toDto)