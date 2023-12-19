package kz.kbtu.olx.presentation.activities.ui

import kz.kbtu.olx.domain.models.Category

interface RvListenerCategory {

    fun onCategoryClick(modelCategory: Category)
}