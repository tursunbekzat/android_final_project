package kz.kbtu.olx.ui

import kz.kbtu.olx.models.ModelCategory

interface RvListenerCategory {

    fun onCategoryClick(modelCategory: ModelCategory)
}