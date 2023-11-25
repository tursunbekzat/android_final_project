package kz.kbtu.olx.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.RowAdBinding
import kz.kbtu.olx.models.ModelAd
import kz.kbtu.olx.ui.sell.FilterAd

class AdapterAd: Adapter<AdapterAd.AdViewHolder>, Filterable{


    private lateinit var binding: RowAdBinding


    private companion object {

        private const val TAG = "AD_ADAPTER_TAG"
    }


    private var context: Context
    var adArrayList: ArrayList<ModelAd>
    private var firebaseAuth: FirebaseAuth
    private var filteredList: ArrayList<ModelAd>
    private var filter: FilterAd? = null

    constructor(context: Context, adArrayList: ArrayList<ModelAd>) : super() {

        this.context = context
        this.adArrayList = adArrayList
        this.filteredList = adArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }


    inner class AdViewHolder(itemView: View): ViewHolder(itemView) {

        var imageIv = binding.imageIv
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var favBtn = binding.favBtn
        var addressTv = binding.addressTv
        var condition = binding.conditionTv
        var priceTv = binding.priceTv
        var dateTv = binding.dateTv
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {

        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false)

        return AdViewHolder(binding.root)
    }


    override fun getItemCount(): Int {

        return adArrayList.size
    }


    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {

        val modelAd = adArrayList[position]
        val title = modelAd.title
        val description = modelAd.description
        val address = modelAd.address
        val condition = modelAd.condition
        val price = modelAd.price
        val timestamp = modelAd.timestamp
        val formattedData = Utils.formatTimestampDate(timestamp)

        loadFirstImage(modelAd, holder)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.condition.text = condition
        holder.addressTv.text = address
        holder.priceTv.text = price
        holder.dateTv.text = formattedData

    }


    private fun loadFirstImage(modelAd: ModelAd, holder: AdViewHolder) {

        val aId = modelAd.id

        Log.d(TAG, "loadFirstImage: aId: $aId")

        val reference = FirebaseDatabase.getInstance().getReference("Ads")
        reference.child(aId).child("Images").limitToFirst(1)
            .addValueEventListener(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (ds in snapshot.children) {

                        val imageUrl = "${ds.child("imageUrl").value}"
                        Log.d(TAG, "loadFirstImage: onDataChange: imageUrl: $imageUrl")

                        try {

                            Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_image_gray)
                                .into(holder.imageIv)

                            Log.d(TAG, "loadFirstImage: onDataChange: image loaded")
                        } catch (e: Exception) {

                            Log.e(TAG, "loadFirstImage: onDataChange: ", e)
                        }
                    }
                }

                override fun onCancelled(e: DatabaseError) {

                }
            })
    }


    override fun getFilter(): Filter {

        if (filter == null) {

            filter = FilterAd(this, filteredList)
        }

        return filter as FilterAd
    }

}