package dev.icarapovic.music.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES

fun Context.isTablet() = resources.configuration.smallestScreenWidthDp >= 600
fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
fun Context.isDarkThemeOn() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES