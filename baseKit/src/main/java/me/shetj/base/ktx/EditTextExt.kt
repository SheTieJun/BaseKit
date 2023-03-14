package me.shetj.base.ktx

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow

fun EditText.asFlow(): Flow<String> {
    return Channel<String>(capacity = Channel.UNLIMITED).also { channel ->
        addTextChangedListener(
            beforeTextChanged =
            { _: CharSequence?, _: Int, _: Int, _: Int ->
            },
            afterTextChanged = {
                channel.trySend(it.toString())
            }, onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            }
        )
    }.receiveAsFlow()
}


fun EditText.asLiveData(): LiveData<String> {
    return MutableLiveData<String>().apply {
        addTextChangedListener(
            beforeTextChanged =
            { _: CharSequence?, _: Int, _: Int, _: Int ->
            },
            afterTextChanged = {
                postValue(it.toString())
            }, onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            }
        )
    }
}