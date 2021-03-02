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

package code.name.monkey.retromusic.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import code.name.monkey.appthemehelper.util.ColorUtil;
import java.util.Collections;
import java.util.Comparator;

public class RetroColorUtil {

  @Nullable
  public static Palette generatePalette(@Nullable Bitmap bitmap) {
    return bitmap == null ? null : Palette.from(bitmap).clearFilters().generate();
  }

  @ColorInt
  public static int getColor(@Nullable Palette palette, int fallback) {
    if (palette != null) {
      if (palette.getVibrantSwatch() != null) {
        return palette.getVibrantSwatch().getRgb();
      } else if (palette.getDarkVibrantSwatch() != null) {
        return palette.getDarkVibrantSwatch().getRgb();
      } else if (palette.getLightVibrantSwatch() != null) {
        return palette.getLightVibrantSwatch().getRgb();
      } else if (palette.getMutedSwatch() != null) {
        return palette.getMutedSwatch().getRgb();
      } else if (palette.getLightMutedSwatch() != null) {
        return palette.getLightMutedSwatch().getRgb();
      } else if (palette.getDarkMutedSwatch() != null) {
        return palette.getDarkMutedSwatch().getRgb();
      } else if (!palette.getSwatches().isEmpty()) {
        return Collections.max(palette.getSwatches(), SwatchComparator.getInstance()).getRgb();
      }
    }
    return fallback;
  }

  @ColorInt
  public static int shiftBackgroundColorForLightText(@ColorInt int backgroundColor) {
    while (ColorUtil.INSTANCE.isColorLight(backgroundColor)) {
      backgroundColor = ColorUtil.INSTANCE.darkenColor(backgroundColor);
    }
    return backgroundColor;
  }

  private static class SwatchComparator implements Comparator<Palette.Swatch> {

    private static SwatchComparator sInstance;

    static SwatchComparator getInstance() {
      if (sInstance == null) {
        sInstance = new SwatchComparator();
      }
      return sInstance;
    }

    @Override
    public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
      return lhs.getPopulation() - rhs.getPopulation();
    }
  }
}
