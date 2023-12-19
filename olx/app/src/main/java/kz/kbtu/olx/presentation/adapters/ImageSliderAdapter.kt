package kz.kbtu.olx.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import kz.kbtu.olx.R
import kz.kbtu.olx.databinding.RowImageSliderBinding
import kz.kbtu.olx.domain.models.ImageSlider

class ImageSliderAdapter: Adapter<ImageSliderAdapter.ImageSliderHolder> {

    private lateinit var binding: RowImageSliderBinding
    private var context: Context
    private var imageArrayList: ArrayList<ImageSlider>

    constructor(context: Context, imageArrayList: ArrayList<ImageSlider>) {
        this.context = context
        this.imageArrayList = imageArrayList
    }

    inner class ImageSliderHolder(itemView: View): ViewHolder(itemView) {

        var imageIv: ShapeableImageView = binding.imageIv
        var imageCountIv: TextView = binding.imageCountTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderHolder {

        binding = RowImageSliderBinding.inflate(LayoutInflater.from(context), parent, false)

        return ImageSliderHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageSliderHolder, position: Int) {

        val modelImageSlider = imageArrayList[position]
        val imageUrl = modelImageSlider.imageUrl
        val imageCount = "${position+1}/${imageArrayList.size}"

        holder.imageCountIv.text = imageCount

        try {

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_gray)
                .into(holder.imageIv)
        } catch (e: Exception) {

            Log.e(TAG, "onBindViewHolder: ", e)
        }

        holder.itemView.setOnClickListener {


        }
    }

    override fun getItemCount(): Int {

        return imageArrayList.size
    }


    private companion object {

        private const val TAG = "ADAPTER_IMAGE_SLIDER_TAG"
    }
}