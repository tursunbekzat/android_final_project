package com.example.bookstore.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bookstore.BookDatabase.Book
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.databinding.FragmentCreateBookBinding
import java.util.regex.Pattern


class CreateBook : Fragment() {
    lateinit var binding: FragmentCreateBookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateBookBinding.inflate(inflater)
//
        val numbers = Pattern.compile("^[0-9]+\$").toRegex()

        val db = ElixirsDB.getBookDb(requireActivity())

        binding.addbook.setOnClickListener {
            if(binding.addtitle.text.toString().isEmpty()) binding.addtitle.error = "Empty title"
            else if(binding.addcost.text.toString().isEmpty()) binding.addcost.error = "Empty cost"
            else if(!binding.addcost.text.matches(numbers)) binding.addcost.error = "Cost should be a numbers"
            else if(binding.adddesc.text.toString().isEmpty()) binding.adddesc.error = "Empty description"
            else{
                val book = Book(null,
                    binding.addtitle.text.toString(),
                    binding.addcost.text.toString().toInt(),
                    binding.adddesc.text.toString())
                Thread{
//                    db.getBookDao().insertBook(book)
                    binding.addtitle.text.clear()
                    binding.addcost.text.clear()
                    binding.adddesc.text.clear()
                }.start()
                Toast.makeText(requireActivity(), "Book Successfully added!", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = CreateBook()
    }
}