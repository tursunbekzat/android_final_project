package com.example.bookstore.BookDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val imageId: Int? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "cost")
    val cost: Int,
    @ColumnInfo(name = "description")
    val description: String
    )
