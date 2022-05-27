package com.ks.newsapp.ui.article

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout

class ArticleTitleBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<TextView>(context, attributeSet) {


    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean = dependency is Toolbar

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean {
        modifyTitleState(child, dependency)
        return true
    }

    private fun modifyTitleState(title: TextView, dependency: View) {
        title.y = dependency.y + 720
    }
}