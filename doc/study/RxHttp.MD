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

使用header配置缓存
 @Headers("Cache-Control: public, max-age=3600)


###  新增可以切换不同的 Api + baseUrl  进行组合使用,解决baseURL 不一样的问题
 ```
  RxHttp.getInstance().getApiManager(BApi::class.java,baseUrl = "http://baidy.com"))
 ```

### 新增本地HttpDns,可以自行设置host 对应的ip
```
 RxHttp.getInstance().addDnsMap(HashMap<String,String>().apply{
            put("baidy1.com","127.0.0.1")
            put("baidy2.com","127.0.0.1")
            put("baidy3.com","127.0.0.1")
        })
```