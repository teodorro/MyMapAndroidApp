package com.example.mymapandroidapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymapandroidapp.entity.MyPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyPointDao {
    @Query("SELECT * FROM MyPointEntity ORDER BY title")
    fun getAll(): Flow<List<MyPointEntity>>

    @Query("SELECT * FROM MyPointEntity WHERE id = :id")
    suspend fun getById(id: Long): MyPointEntity

    @Query("SELECT MAX(id) FROM MyPointEntity")
    suspend fun getMaxId(): Long

    @Query("SELECT COUNT(*) == 0 FROM MyPointEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(myPointEntity: MyPointEntity)

    @Query("DELETE FROM MyPointEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE MyPointEntity SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

}