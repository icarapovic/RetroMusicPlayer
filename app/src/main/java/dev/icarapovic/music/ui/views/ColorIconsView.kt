/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package dev.icarapovic.music.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.retromusic.R
import dev.icarapovic.music.extensions.isDarkThemeOn

/**
 * Draws a colored circle with an icon inside.
 * In the dark theme the circle color is desaturated and the icon is colored in a dark gray.
 * In light theme the circle is semi-transparent with the icon being in vibrant color.
 * */
class ColorIconsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ColorIconsView, 0, 0)
        val bgColor = attributes.getColor(R.styleable.ColorIconsView_civ_backgroundColor, Color.RED)

        setup(bgColor)
        attributes.recycle()
    }

    fun setup(bgColor: Int) {
        background = ContextCompat.getDrawable(context, R.drawable.circle)

        if (context.isDarkThemeOn()) {
            // dark theme // desaturated background color + dark grey/window background colored icon
            backgroundTintList = ColorStateList.valueOf(ColorUtil.desaturate(bgColor))
            imageTintList = ColorStateList.valueOf(context.getColor(R.color.md_grey_900))
        } else {
            // light theme // semi-transparent background + colored icon
            backgroundTintList = ColorStateList.valueOf(ColorUtil.adjustAlpha(bgColor))
            imageTintList = ColorStateList.valueOf(bgColor)
        }
    }
}
