package com.example.mymapandroidapp.dao

import com.example.mymapandroidapp.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DaoModule {
    @Provides
    fun provideMyPointDao(db: AppDb): MyPointDao = db.myPointDao()
}