package shetj.me.base.bean;
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
 */

import androidx.annotation.Keep;
import java.util.List;

@Keep
public class ResultMusic {

    public ResultMusic(String msg, int code, List<DataBean> data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    private String msg;
    private int code;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * url : https://media.lycheer.net/lecture/6583/5dba9468334c6837aee49262_transcoded.m4a
         * title : 温暖春天的爱情 / 温馨
         * duration : 03:34
         * imgUrl : https://img.lycheer.net/material/6583/5d70da288069bd42ddc9a961.png
         */

        public int url;
        public String title;
        public String duration;
        public String imgUrl;
    }
}
