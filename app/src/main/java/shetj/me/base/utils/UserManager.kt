package shetj.me.base.utils

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.shetj.base.lifecycle.DefCoroutineScope
import me.shetj.base.lifecycle.defScope
import me.shetj.base.ktx.defDataStore
import me.shetj.base.ktx.toBean
import me.shetj.base.ktx.toJson
import shetj.me.base.bean.AbUser
import shetj.me.base.bean.GuestUser

object UserManager {

    private const val KET_SAVE_USER = "user_manager:user"

    private val scope: DefCoroutineScope by defScope()
    val userLiveData = liveData {
        defDataStore.get<String>(KET_SAVE_USER).asLiveData().map {
            it?.toBean<AbUser>() ?: GuestUser
        }.let {
            emitSource(it)
        }
    }

    fun register() {
        scope.register(ProcessLifecycleOwner.get().lifecycle)
    }

    fun saveUser(user: AbUser) {
        scope.launch(Dispatchers.IO) {
            user.toJson()?.let {
                defDataStore.save(KET_SAVE_USER, it)
            }
        }
    }


    fun getUser() = userLiveData.value


    fun clearUser() {
        saveUser(GuestUser)
    }
}