package com.example.mymapandroidapp.error

import android.database.SQLException

sealed class AppError (var code: String): RuntimeException(){
    companion object{
        fun from(e: Throwable): AppError = when(e){
            is AppError -> e
            is SQLException -> DbError
            else -> UnknownError
        }
    }
}

object UnknownError: AppError("error_unknown")
object DbError: AppError("error_db")