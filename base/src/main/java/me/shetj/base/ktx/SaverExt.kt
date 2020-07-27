package me.shetj.base.ktx

import io.reactivex.rxjava3.core.Completable
import me.shetj.base.saver.Saver
import me.shetj.base.saver.SaverDao
import org.koin.java.KoinJavaComponent.get

val saverDB: SaverDao
    get() {
        return get(SaverDao::class.java)
    }


fun Saver.updateToDB(): Completable {
    this.updateTime = System.currentTimeMillis()
    return saverDB.updateSaver(this)
}

fun saverCreate(group:String ="base",key:String,value: String): Saver {
   return  Saver(groupName = group,keyName = key,value = value).also {
       System.currentTimeMillis().apply {
           it.createTime = this
           it.updateTime = this
       }
   }
}

