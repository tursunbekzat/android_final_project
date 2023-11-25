package kz.kbtu.olx.ui.sell

import android.widget.Filter
import kz.kbtu.olx.adapter.AdapterAd
import kz.kbtu.olx.models.ModelAd
import java.util.Locale
import kotlin.collections.ArrayList


class FilterAd(
    private val adapter: AdapterAd,
    private val filterArrayList: ArrayList<ModelAd>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {

        var constraint = constraint
        var results = FilterResults()

        if (!constraint.isNullOrEmpty()) {

            constraint = constraint.toString().uppercase(Locale.getDefault())

            val filteredModels = ArrayList<ModelAd>()

            for (i in filterArrayList.indices) {

                if (filterArrayList[i].brand.uppercase(Locale.getDefault()).contains(constraint) ||
                    filterArrayList[i].category.uppercase(Locale.getDefault()).contains(constraint) ||
                    filterArrayList[i].condition.uppercase(Locale.getDefault()).contains(constraint) ||
                    filterArrayList[i].title.uppercase(Locale.getDefault()).contains(constraint)
                    ) {

                    filteredModels.add(filterArrayList[i])
                }
            }

            results.count = filteredModels.size
            results.values = filteredModels
        } else {

            results.count = filterArrayList.size
            results.values = filterArrayList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {

        adapter.adArrayList = results.values as ArrayList<ModelAd>
        adapter.notifyDataSetChanged()
    }
}