package kz.kbtu.olx.ui

import kz.kbtu.olx.models.Category

interface RvListenerCategory {

    fun onCategoryClick(modelCategory: Category)
}