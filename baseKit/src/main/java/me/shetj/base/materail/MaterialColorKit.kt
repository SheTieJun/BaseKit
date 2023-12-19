package me.shetj.base.materail

import android.content.Context
import com.google.android.material.color.MaterialColors
import me.shetj.base.R

/**
 * Material color kit
<attr format="color" name="colorOnContainer"/>
<attr format="color" name="colorOnContainerUnchecked"/>
<attr format="color" name="colorOnError"/>
<attr format="color" name="colorOnErrorContainer"/>
<attr format="color" name="colorOnPrimary"/>
<attr format="color" name="colorOnPrimaryContainer"/>
<attr format="color" name="colorOnPrimarySurface"/>
<attr format="color" name="colorOnSecondary"/>
<attr format="color" name="colorOnSecondaryContainer"/>
<attr format="color" name="colorOnSurface"/>
<attr format="color" name="colorOnSurfaceInverse"/>
<attr format="color" name="colorOnSurfaceVariant"/>
<attr format="color" name="colorOnTertiary"/>
<attr format="color" name="colorOnTertiaryContainer"/>
<attr format="color" name="colorOutline"/>
<attr format="color" name="colorOutlineVariant"/>
<attr format="color" name="colorPrimaryContainer"/>
<attr format="color" name="colorPrimaryInverse"/>
<attr format="color" name="colorPrimarySurface"/>
<attr format="color" name="colorPrimaryVariant"/>
<attr format="color" name="colorSecondary"/>
<attr format="color" name="colorSecondaryContainer"/>
<attr format="color" name="colorSecondaryVariant"/>
<attr format="color" name="colorSurface"/>
<attr format="color" name="colorSurfaceInverse"/>
<attr format="color" name="colorSurfaceVariant"/>
<attr format="color" name="colorTertiary"/>
<attr format="color" name="colorTertiaryContainer"/>
 */

/**----------------------------------------------------------------------**/

/**
 * Material color kit
 * 换一下调用方式，方便使用，以前总是点不出来
 */
@SuppressWarnings("TooManyFunctions")
object MaterialColorKit {

    fun getColorPrimary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorPrimaryContainer,
            "colorPrimary"
        )
    }

    fun getColorOnPrimaryContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnPrimaryContainer,
            "colorOnPrimaryContainer"
        )
    }

    fun getColorBackground(context: Context): Int {
        return MaterialColors.getColor(
            context,
            android.R.attr.colorBackground,
            "colorOnPrimaryContainer"
        )
    }

    fun getColorOnBackground(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnBackground,
            "colorOnPrimaryContainer"
        )
    }

    fun getColorOnSurface(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSurface,
            "colorOnSurface"
        )
    }

    fun getColorOnSurfaceInverse(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSurfaceInverse,
            "colorOnSurfaceInverse"
        )
    }

    fun getColorOnSurfaceVariant(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            "colorOnSurfaceVariant"
        )
    }

    fun getColorOnPrimary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnPrimary,
            "colorOnPrimary"
        )
    }

    fun getColorOnPrimarySurface(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnPrimarySurface,
            "colorOnPrimarySurface"
        )
    }

    fun getColorOnSecondary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSecondary,
            "colorOnSecondary"
        )
    }

    fun getColorOnSecondaryContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnSecondaryContainer,
            "colorOnSecondaryContainer"
        )
    }

    fun getColorOnTertiary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnTertiary,
            "colorOnTertiary"
        )
    }

    fun getColorOnTertiaryContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnTertiaryContainer,
            "colorOnTertiaryContainer"
        )
    }

    fun getColorOnContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnContainer,
            "colorOnContainer"
        )
    }

    fun getColorOnContainerUnchecked(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnContainerUnchecked,
            "colorOnContainerUnchecked"
        )
    }

    fun getColorOnError(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnError,
            "colorOnError"
        )
    }

    fun getColorOnErrorContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOnErrorContainer,
            "colorOnErrorContainer"
        )
    }

    fun getColorOutline(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOutline,
            "colorOutline"
        )
    }

    fun getColorOutlineVariant(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorOutlineVariant,
            "colorOutlineVariant"
        )
    }

    fun getColorPrimaryContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorPrimaryContainer,
            "colorPrimaryContainer"
        )
    }

    fun getColorPrimaryInverse(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorPrimaryInverse,
            "colorPrimaryInverse"
        )
    }

    fun getColorPrimarySurface(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorPrimarySurface,
            "colorPrimarySurface"
        )
    }

    fun getColorPrimaryVariant(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorPrimaryVariant,
            "colorPrimaryVariant"
        )
    }

    fun getColorSecondary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSecondary,
            "colorSecondary"
        )
    }

    fun getColorSecondaryContainer(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSecondaryContainer,
            "colorSecondaryContainer"
        )
    }

    fun getColorSecondaryVariant(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSecondaryVariant,
            "colorSecondaryVariant"
        )
    }

    fun getColorSurface(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSurface,
            "colorSurface"
        )
    }

    fun getColorSurfaceInverse(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSurfaceInverse,
            "colorSurfaceInverse"
        )
    }

    fun getColorSurfaceVariant(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSurfaceVariant,
            "colorSurfaceVariant"
        )
    }

    fun getColorTertiary(context: Context): Int {
        return MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorTertiary,
            "colorTertiary"
        )
    }
}
