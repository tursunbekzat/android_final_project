package com.example.bookstore.Fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.Retrofit.Elixirs
import kotlinx.coroutines.launch

class DescViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(applicationContext)
    }
}

class DescViewModel(private val context: Context) : ViewModel() {

    val db = ElixirsDB.getBookDb(context)

    fun save(elixirs: Elixirs?) {
        viewModelScope.launch {
            db.getBookDao().insertBook(elixirs ?: return@launch)
        }
    }

    fun delete(elixirs: Elixirs?) {
        viewModelScope.launch {
            db.getBookDao().deleteBookById(elixirs?.id ?: return@launch)
        }
    }
}