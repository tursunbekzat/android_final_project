package kz.kbtu.olx.presentation.activities.ui.chats

import android.widget.Filter
import kz.kbtu.olx.presentation.adapters.ChatsAdapter
import kz.kbtu.olx.domain.models.Chats

class FilterChats : Filter {

    private val chatsAdapter: ChatsAdapter
    private val filterList: ArrayList<Chats>


    constructor(chatsAdapter: ChatsAdapter, filterList: ArrayList<Chats>) : super() {

        this.chatsAdapter = chatsAdapter
        this.filterList = filterList
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var constraint: CharSequence? = constraint
        var results = FilterResults()

        if (!constraint.isNullOrEmpty()){

            constraint = constraint.toString().uppercase()

            val filteredModels = ArrayList<Chats>()

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

        chatsAdapter.chatsArrayList = results.values as ArrayList<Chats>
        chatsAdapter.notifyDataSetChanged()
    }

}