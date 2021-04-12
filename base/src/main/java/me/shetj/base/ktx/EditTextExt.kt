package me.shetj.base.ktx

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


fun EditText.asFlow(): Flow<String> {
    return Channel<String>(capacity = Channel.UNLIMITED).also { channel ->
        addTextChangedListener(beforeTextChanged =
        { text: CharSequence?, start: Int, count: Int, after: Int ->
        }, afterTextChanged = {
            channel.offer(it.toString())

        }, onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
        })
    }.receiveAsFlow()
}