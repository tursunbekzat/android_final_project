package com.example.bookstore.BookDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookstore.Retrofit.Elixirs
import kotlinx.coroutines.flow.Flow

@Dao
interface ElixirsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Elixirs)

    @Query("select * from Elixirs")
    fun getAllBook(): Flow<List<Elixirs>>

    @Query("delete from Elixirs where id like :Did")
    suspend fun deleteBookById(Did: String)

    @Query("update Elixirs set name=:title, difficulty=:difficulty, characteristics=:characteristics where id=:Did")
    fun updateBook(Did: String, title: String, difficulty: String, characteristics: String)

    @Query("select * from Elixirs order by id")
    fun getAllBookInAscOrder(): Flow<List<Elixirs>>

    @Query("select * from Elixirs order by id desc")
    fun getAllBookInDescOrder(): Flow<List<Elixirs>>

}