package com.example.bookstore.Fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.Retrofit.Elixirs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class SavedViewmodelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(applicationContext)
    }
}

class SavedViewModel(private val applicationContext: Context) : ViewModel() {

    private val db: ElixirsDB = ElixirsDB.getBookDb(applicationContext)

    private val _elixFlow = MutableStateFlow<List<Elixirs>>(emptyList())
    val elixFlow: StateFlow<List<Elixirs>> = _elixFlow.asStateFlow()

    init {
        viewModelScope.launch {
            db.getBookDao().getAllBook().collectLatest {
                Log.e("TAG", ": elix = $it", )
                _elixFlow.emit(it)
            }
        }
    }
}
