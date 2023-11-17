package com.example.bookstore.BookDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookstore.Retrofit.Elixirs

@Database(entities = [Elixirs::class], version = 1)
abstract class ElixirsDB: RoomDatabase() {
    abstract fun getBookDao(): ElixirsDao

    companion object {
        fun getBookDb(context: Context): ElixirsDB {
            return Room.databaseBuilder(
                context.applicationContext,
                ElixirsDB::class.java,
                name = "books.db"
            ).build()
        }
    }
}