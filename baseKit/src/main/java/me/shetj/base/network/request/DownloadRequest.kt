package me.shetj.base.network.request

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.func.RetryExceptionFunc
import me.shetj.base.network.subscriber.DownloadSubscriber
import okhttp3.ResponseBody

class DownloadRequest(url: String) : BaseRequest<DownloadRequest>(url) {
    private var savePath: String? = null
    private var saveName: String? = null

    /**
     * 下载文件路径<br></br>
     * 默认在：/storage/emulated/0/Android/data/包名/files/1494647767055<br></br>
     */
    fun savePath(savePath: String?): DownloadRequest {
        this.savePath = savePath
        return this
    }

    /**
     * 下载文件名称<br></br>
     * 默认名字是时间戳生成的<br></br>
     */
    fun saveName(saveName: String?): DownloadRequest {
        this.saveName = saveName
        return this
    }



    override fun <T> execute(callback: NetCallBack<T>): Disposable {
        return build().generateRequest()!!.compose { upstream ->
                upstream!!.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
        }.onErrorResumeNext(HttpResponseFunc()).retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(DownloadSubscriber(savePath, saveName, callback)) as Disposable
    }

    override fun generateRequest(): Observable<ResponseBody> {
        return apiManager!!.downloadFile(url)
    }
}