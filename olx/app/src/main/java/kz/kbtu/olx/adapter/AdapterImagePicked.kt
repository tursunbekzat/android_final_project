package kz.kbtu.olx.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import kz.kbtu.olx.R
import kz.kbtu.olx.databinding.RowImagesPickedBinding
import kz.kbtu.olx.ui.sell.ModelImagePicked

class AdapterImagePicked(

    private val context: Context,
    private val imagesPickedArrayList: ArrayList<ModelImagePicked>
    ) : Adapter<AdapterImagePicked.HolderImagePicked>() {

    private lateinit var binding: RowImagesPickedBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagePicked {

        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagePicked(binding.root)
    }


    override fun getItemCount(): Int {

        return imagesPickedArrayList.size
    }


    override fun onBindViewHolder(holder: HolderImagePicked, position: Int) {

        val model = imagesPickedArrayList[position]
        var imageUri = model.imageUri
        Log.d(TAG, "onBindViewHolder: imageUri: $imageUri")
        try {

            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_image_gray)
                .into(holder.imageIv)
        } catch (e: Exception) {

            Log.e(TAG, "onBindViewHolder: ", e)
        }

        holder.closeBtn.setOnClickListener {

            imagesPickedArrayList.remove(model)
            notifyDataSetChanged()
        }
    }


    inner class HolderImagePicked(itemView: View): ViewHolder(itemView) {

        var imageIv = binding.imageIv
        var closeBtn = binding.closeBtn
    }


    private companion object {

        private const val TAG = "ADAPTER_IMAGE_PICKED"
    }

}