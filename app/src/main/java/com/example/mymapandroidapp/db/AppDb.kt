package com.example.mymapandroidapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymapandroidapp.dao.MyPointDao
import com.example.mymapandroidapp.entity.MyPointEntity

@Database(entities = [MyPointEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun myPointDao(): MyPointDao
}