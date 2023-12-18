package kz.kbtu.olx.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.RowImagesPickedBinding
import kz.kbtu.olx.models.ModelImagePicked

class AdapterImagePicked(

    private val context: Context,
    private val imagesPickedArrayList: ArrayList<ModelImagePicked>,
    private val adId: String
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

        if (model.fromInternet){

            try {

                val imageUrl = model.imageUrl
                Log.d(TAG, "onBindViewHolder: imageUrl: $imageUrl")

                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_gray)
                    .into(holder.imageIv)
            } catch (e: Exception){

                Log.e(TAG, "onBindViewHolder: ", e)
            }
        } else {

            try {

                var imageUri = model.imageUri
                Log.d(TAG, "onBindViewHolder: imageUri: $imageUri")

                Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_image_gray)
                    .into(holder.imageIv)
            } catch (e: Exception) {

                Log.e(TAG, "onBindViewHolder: ", e)
            }
        }


        holder.closeBtn.setOnClickListener {

            if (model.fromInternet){

                deleteImageFirebase(model, holder, position)
            } else{

                imagesPickedArrayList.remove(model)
                notifyDataSetChanged()
            }

        }
    }

    private fun deleteImageFirebase(model: ModelImagePicked, holder: HolderImagePicked, position: Int) {

        val imageId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adId).child("Images").child(imageId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(TAG, "deleteImageFirebase: Image $imageId deleted")
                Utils.toast(context, "Image deleted")
                try {

                    imagesPickedArrayList.remove(model)
                    notifyItemRemoved(position)
                } catch (e: Exception){

                    Log.e(TAG, "deleteImageFirebase: ", e)
                }

                Log.d(TAG, "deleteImageFirebase: Successfully deleted")
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "deleteImageFirebase: ", e)
                Utils.toast(context, "Failed to delete image due to ${e.message}")
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