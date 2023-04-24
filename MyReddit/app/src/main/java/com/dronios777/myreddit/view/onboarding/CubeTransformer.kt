package com.dronios777.myreddit.view.onboarding

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CubeTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val deltaY = 0.5f
        val pivotX = if (position < 0f) page.width else 0f
        val pivotY: Float = page.height * deltaY
        val rotationY = 45f * position
        page.pivotX = pivotX.toFloat()
        page.pivotY = pivotY
        page.rotationY = rotationY
    }
}
