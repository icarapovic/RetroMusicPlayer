package dev.icarapovic.music.extensions

import android.content.Context
import android.content.res.Configuration

fun Context.isTablet() = resources.configuration.smallestScreenWidthDp >= 600
fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE