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


    public void refreshAlbum(String fileAbsolutePath, boolean isVideo) {
        MediaScannerConnection mMediaScanner = new MediaScannerConnection(mContext, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                        if (mMediaScanner.isConnected()) {
                                if (isVideo) {
                                        mMediaScanner.scanFile(fileAbsolutePath, "video/mp4");
                                } else {
                                        mMediaScanner.scanFile(fileAbsolutePath, "image/jpeg");
                                }
                        }
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                }

        });
        mMediaScanner.connect();
    }



    public ResultMusic(String msg, int code, List<DataBean> data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    /**
     * msg : ok
     * code : 0
     * data : [{"url":"https://media.lycheer.net/lecture/6583/5dba9468334c6837aee49262_transcoded.m4a","title":"温暖春天的爱情 / 温馨","duration":"03:34","imgUrl":"https://img.lycheer.net/material/6583/5d70da288069bd42ddc9a961.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba946faa26622516464ad3_transcoded.m4a","title":"浪漫的灵感 / 轻松","duration":"02:30","imgUrl":"https://img.lycheer.net/material/6583/5d6b813e06d1a720bf6a8b33.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba9470a1b47d37b30a8db7_transcoded.m4a","title":"Landra's Dream / 愉悦","duration":"01:29","imgUrl":"https://img.lycheer.net/material/6583/5d6b81ca06d1a720bf6a8b3b.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba946b6f4a44252e51fbb6_transcoded.m4a","title":"致爱丽丝 / 舒缓","duration":"03:14","imgUrl":"https://img.lycheer.net/material/6583/5d70da2822c29c3fcd99bdf3.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba946a5336c737b1df4758_transcoded.m4a","title":"同行的人 / 动感","duration":"01:45","imgUrl":"https://img.lycheer.net/material/6583/5d6f9acbdeb39c5cd002bef8.jpg"},{"url":"https://media.lycheer.net/lecture/6583/5dba94726ba78d255494162d_transcoded.m4a","title":"Funshine / 活力","duration":"02:45","imgUrl":"https://img.lycheer.net/material/6583/5d6b7dd59d5f1420b317a70f.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba946b6ba78d255494162c_transcoded.m4a","title":"异国与异国的人们 / 舒缓","duration":"01:26","imgUrl":"https://img.lycheer.net/material/6583/5d8517d09fe1eb291dc3ed74.png"},{"url":"https://media.lycheer.net/lecture/6583/5dba946fb186562536b8113f_transcoded.m4a","title":"G弦之歌 / 感染力","duration":"05:24","imgUrl":"https://img.lycheer.net/material/6583/5d85171eabf8e73b90cecf57.png"}]
     */

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

        private String url;
        private String title;
        private String duration;
        private String imgUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }
}
