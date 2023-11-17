package com.example.bookstore.UserDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "password")
    var password: String
)
