package com.example.mymapandroidapp.repository

import com.example.mymapandroidapp.dao.MyPointDao
import com.example.mymapandroidapp.dto.MyPoint
import com.example.mymapandroidapp.entity.MyPointEntity
import com.example.mymapandroidapp.entity.toDto
import com.example.mymapandroidapp.error.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPointRepositoryImpl @Inject constructor(
    private val myPointDao: MyPointDao
) : MyPointRepository {

    override val data = myPointDao.getAll()
        .map(List<MyPointEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAll() {
        try {
            myPointDao.getAll()
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }

    override suspend fun getById(id: Long) {
        try {
            myPointDao.getById(id)
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }

    override suspend fun insert(point: MyPoint) {
        try {
            var pointEntity = MyPointEntity.fromDto(point)
            myPointDao.insert(pointEntity)
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }

    override suspend fun delete(id: Long) {
        try {
            myPointDao.deleteById(id)
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }

    override suspend fun update(id: Long, title: String) {
        try {
            myPointDao.updateTitle(id, title)
        } catch (e: Exception) {
            throw AppError.from(e)
        }
    }
}