package me.shetj.base.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri


fun Intent?.getQueryParameter(key:String): String? {
    val uri = this?.data
    return uri?.getQueryParameter(key)
}


fun  Context.openMarket(){
    val uri: Uri = Uri.parse("market://details?id=$packageName")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}



