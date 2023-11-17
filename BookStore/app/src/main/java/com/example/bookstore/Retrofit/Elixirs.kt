package com.example.bookstore.Retrofit

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Elixirs")
data class Elixirs(
    @ColumnInfo(name = "difficulty")
    val difficulty: String?,
    @ColumnInfo(name = "effect")
    val effect: String?,
    @PrimaryKey()
    val id: String,
    @ColumnInfo(name = "characteristics")
    val characteristics: String?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "sideEffects")
    val sideEffects: String?
) : Serializable