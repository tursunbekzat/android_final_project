package kz.kbtu.olx.presentation.adapters.difUtils


import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import kz.kbtu.olx.domain.models.Ad


class AdDiffUtilCallback(

    private var oldList: ArrayList<Ad>?,
    private var newList: ArrayList<Ad>?
): DiffUtil.Callback() {
    
    override fun getOldListSize(): Int {

        return oldList!!.size
    }


    override fun getNewListSize(): Int {

        return newList!!.size
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        Log.d(TAG, "areItemsTheSame: ")
        
        val oldProduct: Ad = oldList!![oldItemPosition]
        val newProduct: Ad = newList!![newItemPosition]
        return oldProduct.id === newProduct.id
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        Log.d(TAG, "areContentsTheSame: ")
        
        val oldProduct: Ad = oldList!![oldItemPosition]
        val newProduct: Ad = newList!![newItemPosition]
        return (oldProduct.uid == newProduct.uid)
    }


    private companion object {

        private const val TAG = "AD_DIF_UTIL_CALLBACK"
    }
}