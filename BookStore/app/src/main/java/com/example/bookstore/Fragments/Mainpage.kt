package com.example.bookstore.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookstore.MainActivity
import com.example.bookstore.R
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.databinding.FragmentMainpageBinding
import kotlinx.coroutines.launch


class Mainpage : Fragment() {

    lateinit var binding: FragmentMainpageBinding
    lateinit var myViewModel: MainpageViewmodel
    private val adapter = ElixirAdapter(::onElixirClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myViewModel = ViewModelProvider(
            this,
            MainpageViewmodelFactory(requireActivity().applicationContext)
        )[MainpageViewmodel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainpageBinding.inflate(inflater)
        lifecycleScope.launch {
            myViewModel.elixirs.collect { list ->
                adapter.clearList()
                list.forEach {
                    adapter.addBook(it)
                }
                binding.rcView.layoutManager = LinearLayoutManager(activity)
                binding.rcView.adapter = adapter
            }
        }

//        myViewModel.db.getBookDao().getAllBook().asLiveData().observe(viewLifecycleOwner) { list ->
//            list.forEach {
//                adapter.addBook(it)
//            }
//            binding.rcView.layoutManager = LinearLayoutManager(activity)
//            binding.rcView.adapter = adapter
//        }

        binding.sortAsc.setOnClickListener {
            myViewModel.setSort(SortKind.ASC)
        }

        binding.sortDesc.setOnClickListener {
            myViewModel.setSort(SortKind.DESC)
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    myViewModel.setText(newText)
                }
                return true
            }
        })


        return binding.root
    }

    private fun onElixirClickListener(elixirs: Elixirs) {
        val intent = Intent(requireContext(), DescriptionActivity::class.java)
        intent.putExtra(DescriptionActivity.KEY, elixirs)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = Mainpage()
    }
}