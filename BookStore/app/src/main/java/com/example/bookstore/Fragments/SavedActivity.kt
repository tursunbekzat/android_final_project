package com.example.bookstore.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.databinding.ActivitySavedBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedActivity : AppCompatActivity() {

    lateinit var binding: ActivitySavedBinding
    lateinit var viewModel: SavedViewModel
    private val adapter = ElixirAdapter(::onElixirClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SavedViewmodelFactory(this.applicationContext)
        )[SavedViewModel::class.java]
        lifecycleScope.launch {
            viewModel.elixFlow.collectLatest { list ->
                adapter.clearList()
                list.forEach {
                    adapter.addBook(it)
                }
                binding.rcView.layoutManager = LinearLayoutManager(this@SavedActivity)
                binding.rcView.adapter = adapter
            }
        }
    }

    private fun onElixirClickListener(elixirs: Elixirs) {
        val intent = Intent(this, DescriptionActivity::class.java)
        intent.putExtra(DescriptionActivity.KEY, elixirs)
        startActivity(intent)
    }
}
