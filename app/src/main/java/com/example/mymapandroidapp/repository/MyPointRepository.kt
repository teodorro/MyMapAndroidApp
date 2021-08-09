package com.example.mymapandroidapp.repository

import com.example.mymapandroidapp.dto.MyPoint
import kotlinx.coroutines.flow.Flow

interface MyPointRepository {
    val data: Flow<List<MyPoint>>
    suspend fun getAll()
    suspend fun getById(id: Long)
    suspend fun insert(point: MyPoint)
    suspend fun delete(id: Long)
    suspend fun update(id: Long, title: String)
}