/*
 * Copyright (C) 2017 Clever Rock Inc. All rights reserved.
 */

package me.shetj.base.tools.app;

import android.os.Build;
import android.support.annotation.Keep;
import android.text.Html;
import android.text.Spanned;

/**
 * Helper for accessing features in {@link Html}
 * introduced after API level 24 in a backwards compatible fashion.
 * @author shetj
 * @date 17/4/18
 */
@Keep
public class HtmlCompat {

    //--- Constructors -----------------------------------------------------------------------------

    /**
     * This class should not be instantiated, but the constructor must be
     * visible for the class to be extended.
     */
    protected HtmlCompat() {
        // Not publicly instantiable, but may be extended.
    }

    //--- Public static methods --------------------------------------------------------------------

    /**
     * Returns displayable styled text from the provided HTML string with the legacy flags
     * {@link Html#FROM_HTML_MODE_LEGACY}.
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        Spanned text;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(source, 0x00000000, null, null);
        } else {
            text = Html.fromHtml(source);
        }
        return text;
    }
}
