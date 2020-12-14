package shetj.me.base.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import me.shetj.base.S
import me.shetj.base.ktx.logi


class DataStoreUtils {

    val dataStore: DataStore<Preferences> = S.app.createDataStore(
            name = "settings"
    ).apply {
    }
    private val dataStore2: DataStore<Preferences> =  S.app.createDataStore(
            "settings",
            migrations = listOf(SharedPreferencesMigration(S.app, "sp"))
    )



    suspend fun  save(key:String ){


        dataStore.edit {
            it[preferencesKey<Int>(name = key)] = 123456
        }

    }

    suspend fun get(key:String){
        dataStore.data.map { it -> it[preferencesKey<Int>(name = key)] }.asLiveData().observeForever {
            it.toString().logi()
        }
        //等价下面的
//        dataStore.data.map { it -> it[preferencesKey<Int>(name = key)] }.collectLatest {
//                it.toString().logi()
//        }
    }


    fun String.toKey(){

    }

}