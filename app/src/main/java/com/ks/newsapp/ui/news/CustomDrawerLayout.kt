package com.ks.newsapp.ui.news

import android.content.Context
import android.util.AttributeSet
import androidx.drawerlayout.widget.DrawerLayout

class CustomDrawerLayout(context: Context, attributeSet: AttributeSet) : DrawerLayout(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY
        )
        var heightMeasureSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}