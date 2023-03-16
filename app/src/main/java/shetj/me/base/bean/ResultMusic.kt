package shetj.me.base.bean

import androidx.annotation.Keep

/*Copyright 2020 shetiejun(375105540@qq.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */@Keep
class ResultMusic(var msg: String, var code: Int, var data: List<DataBean>) {

    class DataBean {
        /**
         * url : https://media.lycheer.net/lecture/6583/5dba9468334c6837aee49262_transcoded.m4a
         * title : 温暖春天的爱情 / 温馨
         * duration : 03:34
         * imgUrl : https://img.lycheer.net/material/6583/5d70da288069bd42ddc9a961.png
         */
        var url :String ?= null
        var title: String? = null
        var duration: String? = null
        var imgUrl: String? = null
    }
}