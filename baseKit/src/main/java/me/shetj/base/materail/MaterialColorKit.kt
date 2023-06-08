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

fun Context.getColorPrimary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorPrimaryContainer,
        "colorPrimary"
    )
}

fun Context.getColorOnPrimaryContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnPrimaryContainer,
        "colorOnPrimaryContainer"
    )
}

fun Context.getColorBackground(): Int {
    return MaterialColors.getColor(
        this,
          android.R.attr.colorBackground,
        "colorOnPrimaryContainer"
    )
}


fun Context.getColorOnBackground(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnBackground,
        "colorOnPrimaryContainer"
    )
}

fun Context.getColorOnSurface(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSurface,
        "colorOnSurface"
    )
}

fun Context.getColorOnSurfaceInverse(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSurfaceInverse,
        "colorOnSurfaceInverse"
    )
}

fun Context.getColorOnSurfaceVariant(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSurfaceVariant,
        "colorOnSurfaceVariant"
    )
}

fun Context.getColorOnPrimary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnPrimary,
        "colorOnPrimary"
    )
}

fun Context.getColorOnPrimarySurface(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnPrimarySurface,
        "colorOnPrimarySurface"
    )
}

fun Context.getColorOnSecondary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSecondary,
        "colorOnSecondary"
    )
}

fun Context.getColorOnSecondaryContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnSecondaryContainer,
        "colorOnSecondaryContainer"
    )
}

fun Context.getColorOnTertiary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnTertiary,
        "colorOnTertiary"
    )
}

fun Context.getColorOnTertiaryContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnTertiaryContainer,
        "colorOnTertiaryContainer"
    )
}

fun Context.getColorOnContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnContainer,
        "colorOnContainer"
    )
}

fun Context.getColorOnContainerUnchecked(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnContainerUnchecked,
        "colorOnContainerUnchecked"
    )
}

fun Context.getColorOnError(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnError,
        "colorOnError"
    )
}

fun Context.getColorOnErrorContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOnErrorContainer,
        "colorOnErrorContainer"
    )
}

fun Context.getColorOutline(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOutline,
        "colorOutline"
    )
}

fun Context.getColorOutlineVariant(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorOutlineVariant,
        "colorOutlineVariant"
    )
}

fun Context.getColorPrimaryContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorPrimaryContainer,
        "colorPrimaryContainer"
    )
}

fun Context.getColorPrimaryInverse(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorPrimaryInverse,
        "colorPrimaryInverse"
    )
}

fun Context.getColorPrimarySurface(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorPrimarySurface,
        "colorPrimarySurface"
    )
}

fun Context.getColorPrimaryVariant(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorPrimaryVariant,
        "colorPrimaryVariant"
    )
}

fun Context.getColorSecondary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSecondary,
        "colorSecondary"
    )
}

fun Context.getColorSecondaryContainer(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSecondaryContainer,
        "colorSecondaryContainer"
    )
}

fun Context.getColorSecondaryVariant(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSecondaryVariant,
        "colorSecondaryVariant"
    )
}

fun Context.getColorSurface(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSurface,
        "colorSurface"
    )
}

fun Context.getColorSurfaceInverse(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSurfaceInverse,
        "colorSurfaceInverse"
    )
}

fun Context.getColorSurfaceVariant(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorSurfaceVariant,
        "colorSurfaceVariant"
    )
}

fun Context.getColorTertiary(): Int {
    return MaterialColors.getColor(
        this,
        com.google.android.material.R.attr.colorTertiary,
        "colorTertiary"
    )
}
