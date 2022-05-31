package com.ks.newsapp.ui.article

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView

class ArticleContentBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<NestedScrollView>(context, attributeSet) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: NestedScrollView,
        dependency: View
    ): Boolean = dependency is TextView

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: NestedScrollView,
        dependency: View
    ): Boolean {
        modifyArticleContent(child, dependency)
        return true
    }

    private fun modifyArticleContent(scrollView: NestedScrollView, dependency: View) {
        scrollView.y = dependency.y + dependency.height + 70
    }
}