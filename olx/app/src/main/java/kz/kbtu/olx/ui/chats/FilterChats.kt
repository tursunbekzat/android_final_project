package kz.kbtu.olx.ui.chats

import android.widget.Filter
import kz.kbtu.olx.adapter.AdapterChats
import kz.kbtu.olx.models.ModelChats

class FilterChats : Filter {

    private val adapterChats: AdapterChats
    private val filterList: ArrayList<ModelChats>


    constructor(adapterChats: AdapterChats, filterList: ArrayList<ModelChats>) : super() {

        this.adapterChats = adapterChats
        this.filterList = filterList
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var constraint: CharSequence? = constraint
        var results = FilterResults()

        if (!constraint.isNullOrEmpty()){

            constraint = constraint.toString().uppercase()

            val filteredModels = ArrayList<ModelChats>()

            for (i in filterList.indices){

                if (filterList[i].name.uppercase().contains(constraint)){

                    filteredModels.add(filterList[i])
                }
            }

            results.count = filteredModels.size
            results.values = filteredModels
        } else {

            results.count = filterList.size
            results.values = filterList
        }

        return results
    }


    override fun publishResults(constraint: CharSequence, results: FilterResults) {

        adapterChats.chatsArrayList = results.values as ArrayList<ModelChats>
        adapterChats.notifyDataSetChanged()
    }

}