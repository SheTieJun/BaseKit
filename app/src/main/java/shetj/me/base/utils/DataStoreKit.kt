package shetj.me.base.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.shetj.base.S


/**
 * dataStore
 */
object DataStoreKit {

    private val Context.dataStore by preferencesDataStore("filename")

    val dataStore: DataStore<Preferences> by lazy { S.app.dataStore }

    suspend inline fun <reified T : Any> save(key: String, value: T) {
        try {
            dataStore.edit {
                when (T::class) {
                    Int::class -> {
                        it[intPreferencesKey(key)] = value as Int
                    }
                    Double::class -> {
                        it[doublePreferencesKey(key)] = value as Double
                    }
                    String::class -> {
                        it[stringPreferencesKey(key)] = value as String
                    }
                    Boolean::class -> {
                        it[booleanPreferencesKey(key)] = value as Boolean
                    }
                    Float::class -> {
                        it[floatPreferencesKey(key)] = value as Float
                    }
                    Long::class -> {
                        it[longPreferencesKey(key)] = value as Long
                    }
                    Set::class -> {
                        it[stringSetPreferencesKey(key)] = value as Set<String>
                    }
                    else -> {
                        throw IllegalArgumentException(" Can't handle 'value' ")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inline fun <reified T : Any> getFlow(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { it[key] }
    }

    suspend inline fun <reified T : Any> get(key: Preferences.Key<T>): T? {
        return dataStore.data.map { it[key] }.first()
    }

}