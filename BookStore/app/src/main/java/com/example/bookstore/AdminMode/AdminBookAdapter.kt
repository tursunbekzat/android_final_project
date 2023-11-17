package com.example.bookstore.AdminMode

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstore.BookDatabase.Book
import com.example.bookstore.BookDatabase.ElixirsDB
import com.example.bookstore.R
import com.example.bookstore.Retrofit.Elixirs
import com.example.bookstore.databinding.AdminBookItemBinding
import java.util.regex.Pattern


class AdminBookAdapter(val context: Context): RecyclerView.Adapter<AdminBookAdapter.AdminBookHolder>() {
    val adminbooklist = ArrayList<Book>()
    class AdminBookHolder(item: View, context: Context): RecyclerView.ViewHolder(item) {
        val binding = AdminBookItemBinding.bind(item)
        val context = context
        var builder = AlertDialog.Builder(context)
        fun bind(book: Book) = with(binding) {
            im.setImageResource(R.drawable.photobook)
            booktitle.text = Editable.Factory.getInstance().newEditable(book.title)
            bookcost.text = Editable.Factory.getInstance().newEditable(book.cost.toString())
            bookdesc.text = Editable.Factory.getInstance().newEditable(book.description)
        }

        fun Clickdelete(book: Book) {
            binding.btndelete.setOnClickListener {
                val db = ElixirsDB.getBookDb(context)
                val id = book.imageId
                builder.setTitle("Delete book")
                    .setMessage("Delete this book? Are you sure to continue?")
                    .setPositiveButton("Yep"){inter, it->
                        Thread{
                            if (id != null) {
//                                db.getBookDao().deleteBookById(id.toString())
                            }
                        }.start()
                        val intent = Intent(context, AdminMode::class.java)
                        context.startActivity(intent)
                    }
                    .setNegativeButton("Nope"){inter, it->
                        inter.cancel()
                    }
                    .show()
            }
        }

        fun ClickUpdate(book: Book) {
            binding.btnupdate.setOnClickListener {
                val db = ElixirsDB.getBookDb(context)
                val id = book.imageId
                val numbers = Pattern.compile("^[0-9]+\$").toRegex()
                if(binding.uptitle.text.toString().isEmpty()) binding.uptitle.error = "Empty title"
                else if(binding.upcost.text.toString().isEmpty()) binding.upcost.error = "Empty cost"
                else if(!binding.upcost.text.matches(numbers)) binding.upcost.error = "Cost should be a numbers"
                else if(binding.updesc.text.toString().isEmpty()) binding.updesc.error = "Empty description"
                else{
                    builder.setTitle("Update book")
                        .setMessage("Update this book?")
                        .setPositiveButton("Yep"){inter, it->
                            Thread{
                                if (id != null) {
                                    db.getBookDao().updateBook(
                                        id.toString(),
                                        binding.uptitle.text.toString(),
                                        binding.upcost.text.toString(),
                                        binding.updesc.text.toString()
                                    )
                                }

                                binding.uptitle.text.clear()
                                binding.upcost.text.clear()
                                binding.updesc.text.clear()

                            }.start()
                            val intent = Intent(context, AdminMode::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Nope"){inter, it->
                            inter.cancel()
                        }
                        .show()
                }
            }
        }

        fun ClickInsert() {
            binding.btninsert.setOnClickListener {
                val db = ElixirsDB.getBookDb(context)
                val numbers = Pattern.compile("^[0-9]+\$").toRegex()
                if(binding.uptitle.text.toString().isEmpty()) binding.uptitle.error = "Empty title"
                else if(binding.upcost.text.toString().isEmpty()) binding.upcost.error = "Empty cost"
                else if(!binding.upcost.text.matches(numbers)) binding.upcost.error = "Cost should be a numbers"
                else if(binding.updesc.text.toString().isEmpty()) binding.updesc.error = "Empty description"
                else{
                    builder.setTitle("Insert book")
                        .setMessage("Insert this book?")
                        .setPositiveButton("Yep"){inter, it->
                            val book = Book(null,
                                binding.uptitle.text.toString(),
                                binding.upcost.text.toString().toInt(),
                                binding.updesc.text.toString())
                            Thread{
//                                db.getBookDao().insertBook(Elixirs())

                                binding.uptitle.text.clear()
                                binding.upcost.text.clear()
                                binding.updesc.text.clear()

                            }.start()
                            val intent = Intent(context, AdminMode::class.java)
                            context.startActivity(intent)
                        }
                        .setNegativeButton("Nope"){inter, it->
                            inter.cancel()
                        }
                        .show()
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_book_item, parent, false)
        return AdminBookHolder(view, context)
    }

    override fun getItemCount(): Int {
        return adminbooklist.size
    }

    override fun onBindViewHolder(holder: AdminBookHolder, position: Int) {
        holder.bind(adminbooklist[position])
        holder.Clickdelete(adminbooklist[position])
        holder.ClickUpdate(adminbooklist[position])
        holder.ClickInsert()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAdminBook(book: Book) {
        adminbooklist.add(book)
        notifyDataSetChanged()
    }
}