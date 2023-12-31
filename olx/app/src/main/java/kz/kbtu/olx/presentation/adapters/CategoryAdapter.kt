package kz.kbtu.olx.presentation.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kz.kbtu.olx.databinding.RowCategoryBinding
import kz.kbtu.olx.domain.models.Category
import kz.kbtu.olx.presentation.activities.ui.RvListenerCategory
import java.util.Random

class CategoryAdapter (

    private val context: Context,
    private val categoryArrayList: ArrayList<Category>,
    private val rvListenerCategory: RvListenerCategory
) : Adapter<CategoryAdapter.HolderModelCategory>() {

    private lateinit var binding: RowCategoryBinding


    inner class HolderModelCategory(itemView: View): ViewHolder(itemView) {

        var categoryIconIv = binding.categoryIconIv
        var categoryTv = binding.categoryTv
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderModelCategory {

        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderModelCategory(binding.root)
    }


    override fun getItemCount(): Int {

        return categoryArrayList.size
    }


    override fun onBindViewHolder(holder: HolderModelCategory, position: Int) {

        val modelCategory = categoryArrayList[position]
        val icon = modelCategory.icon
        val category = modelCategory.category

        val random = Random()
        val color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255))

        holder.categoryIconIv.setImageResource(icon)
        holder.categoryTv.text = category
        holder.categoryIconIv.setBackgroundColor(color)

        holder.itemView.setOnClickListener {

            rvListenerCategory.onCategoryClick(modelCategory)
        }
    }
}