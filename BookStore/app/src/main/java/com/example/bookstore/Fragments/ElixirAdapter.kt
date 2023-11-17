package com.example.bookstore.Fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstore.R
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.databinding.BookItemBinding

class ElixirAdapter constructor(
    private val onClickListener: (Elixirs) -> Unit
) : RecyclerView.Adapter<ElixirAdapter.ElixirHolder>() {
    val bookList = ArrayList<Elixirs>()

    class ElixirHolder(item: View): RecyclerView.ViewHolder(item) {

        val binding = BookItemBinding.bind(item)

        @SuppressLint("SetTextI18n")
        fun bind(elixirs: Elixirs, onClickListener: (Elixirs) -> Unit) = with(binding){
            im.setImageResource(R.drawable.photobook)
            booktitle.text = elixirs.name
            bookcost.text = elixirs.difficulty
            bookdesc.text = elixirs.characteristics
            root.setOnClickListener {
                onClickListener(elixirs)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElixirHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return ElixirHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: ElixirHolder, position: Int) {
        holder.bind(bookList[position], onClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addBook(elixirs: Elixirs) {
        bookList.add(elixirs)
        notifyDataSetChanged()
    }

    fun clearList(){
        bookList.clear()
    }
}