package com.example.bookstore.UserDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database (entities = [User::class], version = 1)
abstract class UserDb: RoomDatabase() {
    abstract fun getUserDao(): UserDao

    companion object {
        fun getUserDb(context: Context): UserDb {
            return Room.databaseBuilder(
                context.applicationContext,
                UserDb::class.java,
                name = "user.db"
            ).build()
        }
    }
}