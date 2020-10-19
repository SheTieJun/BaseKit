package shetj.me.base.utils

import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import me.shetj.base.S
import me.shetj.base.ktx.logi


class DataStoreUtils {

    val dataStore: DataStore<Preferences> = S.app.createDataStore(
            name = "settings"
    )
    private val dataStore2: DataStore<Preferences> =  S.app.createDataStore(
            "settings",
            migrations = listOf(SharedPreferencesMigration(S.app, "sp"))
    )



    suspend fun  save(key:String ){


        dataStore.edit {
            it[preferencesKey<Int>(name = key)] = 1
        }

    }

    suspend fun get(key:String){
        dataStore.data.map { it -> it[preferencesKey<Int>(name = key)] }.collectLatest {
            it.toString().logi()
        }
    }


    fun String.toKey(){

    }

}