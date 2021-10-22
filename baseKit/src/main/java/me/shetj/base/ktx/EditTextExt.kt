package me.shetj.base.ktx

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn


fun EditText.asFlow(): Flow<String> {
    return Channel<String>(capacity = Channel.UNLIMITED).also { channel ->
        addTextChangedListener(beforeTextChanged =
        { _: CharSequence?, _: Int, _: Int, _: Int ->
        }, afterTextChanged = {
            channel.offer(it.toString())

        }, onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
        })
    }.receiveAsFlow()
}