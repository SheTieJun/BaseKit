/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.tools.data


import android.content.Context
import androidx.annotation.NonNull
import androidx.datastore.core.DataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import me.shetj.base.BaseKit


val defDataStoreKit by lazy { BaseKit.app.dataStoreKit() }

fun Context.dataStoreKit(): DataStoreKit {
    return DataStoreKit(this)
}

class DataStoreKit(
    private val context: Context,
    name: String = "_DataStore_Kit",
    private val oldSp: List<String> = listOf()
) {

    private val Context.myDataStore by preferencesDataStore(
        name,
        produceMigrations = { context -> oldSp.map { context.getSPByName(it) } })

    val dataStore: DataStore<Preferences> by lazy { context.myDataStore }

    /**
     * Save 保存
     *
     * @param T [Type]
     * @param key
     * @param value
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    suspend inline fun <reified T : Any> save(@NonNull key: String, value: T): Boolean {
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
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Get Flow by Preferences.Key
     *
     * @param T
     * @param key
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> get(@NonNull key: String, defaultValue: T): Flow<T> {
        val data = dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            when (T::class) {
                Int::class -> {
                    it[intPreferencesKey(key)]
                }
                Double::class -> {
                    it[doublePreferencesKey(key)]
                }
                String::class -> {
                    it[stringPreferencesKey(key)]
                }
                Boolean::class -> {
                    it[booleanPreferencesKey(key)]
                }
                Float::class -> {
                    it[floatPreferencesKey(key)]
                }
                Long::class -> {
                    it[longPreferencesKey(key)]
                }
                Set::class -> {
                    it[stringSetPreferencesKey(key)]
                }
                else -> {
                    null
                }
            } ?: defaultValue
        }
        return data as Flow<T>
    }

    /**
     * Get Flow by Preferences.Key
     *
     * @param T
     * @param key
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> get(@NonNull key: String): Flow<T?> {
        val data = dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            when (T::class) {
                Int::class -> {
                    it[intPreferencesKey(key)]
                }
                Double::class -> {
                    it[doublePreferencesKey(key)]
                }
                String::class -> {
                    it[stringPreferencesKey(key)]
                }
                Boolean::class -> {
                    it[booleanPreferencesKey(key)]
                }
                Float::class -> {
                    it[floatPreferencesKey(key)]
                }
                Long::class -> {
                    it[longPreferencesKey(key)]
                }
                Set::class -> {
                    it[stringSetPreferencesKey(key)]
                }
                else -> {
                    null
                }
            }
        }
        return data as Flow<T?>
    }

    /**
     * Get first 只获取第一个值
     *
     * @param T
     * @param key
     * @param defaultValue
     * @return
     */
    suspend inline fun <reified T : Any> getFirst(@NonNull key: String,@NonNull defaultValue: T): T {
        var resultValue = defaultValue
        dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.first {
            resultValue = (when (T::class) {
                Int::class -> {
                    it[intPreferencesKey(key)]
                }
                Double::class -> {
                    it[doublePreferencesKey(key)]
                }
                String::class -> {
                    it[stringPreferencesKey(key)]
                }
                Boolean::class -> {
                    it[booleanPreferencesKey(key)]
                }
                Float::class -> {
                    it[floatPreferencesKey(key)]
                }
                Long::class -> {
                    it[longPreferencesKey(key)]
                }
                Set::class -> {
                    it[stringSetPreferencesKey(key)]
                }
                else -> {
                    null
                }
            } ?: defaultValue) as T
            true
        }
        return resultValue
    }


    fun clearBlock(): Boolean {
        return runBlocking(Dispatchers.IO) {
            try {
                dataStore.edit {
                    it.clear()
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    /**
     * Get first sync
     * 同步方法获取
     * @param T
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    inline fun <reified T : Any> getFirstBlock(@NonNull key: String, @NonNull defaultValue: T): T {
        //通过阻塞获取
        return runBlocking(Dispatchers.IO) {
            var resultValue = defaultValue
            dataStore.data.first {
                resultValue = (
                        when (T::class) {
                            Int::class -> {
                                it[intPreferencesKey(key)]
                            }
                            Double::class -> {
                                it[doublePreferencesKey(key)]
                            }
                            String::class -> {
                                it[stringPreferencesKey(key)]
                            }
                            Boolean::class -> {
                                it[booleanPreferencesKey(key)]
                            }
                            Float::class -> {
                                it[floatPreferencesKey(key)]
                            }
                            Long::class -> {
                                it[longPreferencesKey(key)]
                            }
                            Set::class -> {
                                it[stringSetPreferencesKey(key)]
                            }
                            else -> {
                                null
                            }
                        } ?: defaultValue
                        ) as T
                true
            }
            resultValue
        }
    }

    /**
     * Save 保存
     *
     * @param T [Type]
     * @param key
     * @param value
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> saveBlock(@NonNull key: String, @NonNull value: T): Boolean {
        return runBlocking(Dispatchers.IO) {
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
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}


/**
 * Get SharedPreferencesMigration by name
 *
 * @param name sp fileName
 * @return
 */
internal fun Context.getSPByName(name: String): SharedPreferencesMigration<Preferences> {
    return SharedPreferencesMigration(this, name)
}