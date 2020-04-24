package me.shetj.base.network.request

import me.shetj.base.network.RxHttp


//自定义apiService
class CustomRequest<S>()  {
    fun create(clazz: Class<S>):S{
        return RxHttp.getInstance().getApiManager(clazz)
    }
}