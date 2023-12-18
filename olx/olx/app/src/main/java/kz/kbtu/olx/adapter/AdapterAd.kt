package kz.kbtu.olx.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.RowAdBinding
import kz.kbtu.olx.models.ModelAd
import kz.kbtu.olx.ui.my_ads.AdDetailsActivity
import kz.kbtu.olx.ui.sell.FilterAd

class AdapterAd(
    private var context: Context,
    var adArrayList: ArrayList<ModelAd>
) : Adapter<AdapterAd.AdViewHolder>(), Filterable{


    private lateinit var binding: RowAdBinding


    //    var adArrayList: ArrayList<ModelAd>
    private var firebaseAuth: FirebaseAuth
    private var filteredList: ArrayList<ModelAd> = adArrayList
    private var filter: FilterAd? = null

    init {
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

        Utils.loadFirstImage(modelAd, holder, context)

        if (firebaseAuth.currentUser != null) Utils.checkIsFavorite(modelAd, holder, context, firebaseAuth)

        holder.titleTv.text = modelAd.title
        holder.descriptionTv.text = modelAd.description
        holder.condition.text = modelAd.condition
        holder.addressTv.text = modelAd.address
        holder.priceTv.text = modelAd.price
        holder.dateTv.text = Utils.formatTimestampDate(modelAd.timestamp)

        binding.favBtn.setOnClickListener {

            if (modelAd.favorite) Utils.removeFromFavorite(context, modelAd.id) else Utils.addToFavorite(context, modelAd.id)
        }

        holder.itemView.setOnClickListener {

            val intent = Intent(context, AdDetailsActivity::class.java)
            intent.putExtra("adId", modelAd.id)
            context.startActivity(intent)
        }

    }


    override fun getFilter(): Filter {

        if (filter == null) filter = FilterAd(this, filteredList)

        return filter as FilterAd
    }

}