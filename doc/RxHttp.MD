自定义数据结构，支持自定义泛型
```kotlin
         RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
                    .executeCus(object : SimpleNetCallBack<ResultMusic>(this) {
                        override fun onSuccess(data: ResultMusic) {
                            super.onSuccess(data)
                            Timber.i(data.toJson())
                        }

                        override fun onError(e: Exception) {
                            super.onError(e)
                            Timber.e(e)
                        }
                    })
```

当时数据格式是ApiResult<T>   => ApiResult<List<MusicBean>>
```kotlin
            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
                    .execute(object : SimpleNetCallBack<List<MusicBean>>(this) {
                        override fun onSuccess(data: List<MusicBean>) {
                            super.onSuccess(data)
                            Timber.i(data.toJson())
                        }

                        override fun onError(e: Exception) {
                            super.onError(e)
                            Timber.e(e)
                        }
                    })
```