@file:JvmName("UriKt")
package me.shetj.base.ktx

import android.net.Uri


fun Uri.parseUriParams(uri: Uri): HashMap<String, String> {
    val map = HashMap<String, String>()
    uri.queryParameterNames.forEach { name ->
        uri.getQueryParameter(name)?.let { map[name] = it }
    }
    return map
}
