package com.example.bookstore.Fragments

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.databinding.FragmentDescriptionBinding

class DescriptionActivity : AppCompatActivity() {

    lateinit var binding: FragmentDescriptionBinding
    lateinit var viewModel: DescViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDescriptionBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            DescViewModelFactory(this.applicationContext)
        )[DescViewModel::class.java]
        setContentView(binding.root)
        val elixirs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(KEY, Elixirs::class.java)
        } else {
            intent.getSerializableExtra(KEY) as? Elixirs
        }
        binding.text.text = elixirs?.name
        binding.btnupdate.setOnClickListener {
            save()
            // todo add toast
        }
        binding.btnupdate3.setOnClickListener {
            delete()
            // todo add toast
        }
    }

    private fun save() {
        val elixirs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(KEY, Elixirs::class.java)
        } else {
            intent.getSerializableExtra(KEY) as? Elixirs
        }
        viewModel.save(elixirs)
    }

    private fun delete() {
        val elixirs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(KEY, Elixirs::class.java)
        } else {
            intent.getSerializableExtra(KEY) as? Elixirs
        }
        viewModel.delete(elixirs)
    }

    companion object {
        const val KEY = "elix_key"
    }
}