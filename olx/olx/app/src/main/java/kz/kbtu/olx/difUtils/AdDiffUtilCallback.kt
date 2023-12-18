package kz.kbtu.olx.difUtils


import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import kz.kbtu.olx.models.ModelAd


class AdDiffUtilCallback(

    private var oldList: ArrayList<ModelAd>?,
    private var newList: ArrayList<ModelAd>?
): DiffUtil.Callback() {
    
    override fun getOldListSize(): Int {

        return oldList!!.size
    }


    override fun getNewListSize(): Int {

        return newList!!.size
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        Log.d(TAG, "areItemsTheSame: ")
        
        val oldProduct: ModelAd = oldList!![oldItemPosition]
        val newProduct: ModelAd = newList!![newItemPosition]
        return oldProduct.id === newProduct.id
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        Log.d(TAG, "areContentsTheSame: ")
        
        val oldProduct: ModelAd = oldList!![oldItemPosition]
        val newProduct: ModelAd = newList!![newItemPosition]
        return (oldProduct.uid == newProduct.uid)
//                && oldProduct.uid === newProduct.uid
//                && oldProduct.title === newProduct.title
//                && oldProduct.description === newProduct.description
//                && oldProduct.category === newProduct.category
//                && oldProduct.address === newProduct.address
//                && oldProduct.price === newProduct.price
//                && oldProduct.favorite == newProduct.favorite
//                && oldProduct.status === newProduct.status
//                && oldProduct.timestamp == newProduct.timestamp
//                && oldProduct.latitude == newProduct.latitude
//                && oldProduct.longitude == newProduct.longitude)
    }


    private companion object {

        private const val TAG = "AD_DIF_UTIL_CALLBACK"
    }
}