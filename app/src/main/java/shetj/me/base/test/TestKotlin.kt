package shetj.me.base.test

import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.*


class TestKotlin {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {


            liveData<Int> {



            }

            val uiState: StateFlow<Int> = MutableStateFlow(0)
        }
    }
}