package com.example.bookstore.Fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.Retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainpageViewmodelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(applicationContext)
    }
}

enum class SortKind {
    ASC,
    DESC
}

class MainpageViewmodel(
    applicationcontext:Context
) : ViewModel() {
    val db = ElixirsDB.getBookDb(applicationcontext)
    private val _elixirs = MutableStateFlow<List<Elixirs>>(emptyList())
    private val searchText = MutableStateFlow("")
    private val sortKind = MutableStateFlow<SortKind?>(null)

    val elixirs: StateFlow<List<Elixirs>> = combine(
        _elixirs,
        searchText,
        sortKind
    ) { list, text, sort ->
        val filtered = list.filter { it.name?.contains(text) ?: false }
        when (sort) {
            SortKind.ASC -> filtered.sortedBy { it.name }
            SortKind.DESC -> filtered.sortedByDescending { it.name }
            null -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        getElixirs()
    }

    fun setText(text: String)
    {
        searchText.value = text
    }

    fun setSort(sort: SortKind) {
        sortKind.value = sort
    }

    private fun getElixirs() {
        viewModelScope.launch {
            val elixirs = RetrofitInstance.apinterface.getData()
            _elixirs.emit(elixirs)
        }
    }
}