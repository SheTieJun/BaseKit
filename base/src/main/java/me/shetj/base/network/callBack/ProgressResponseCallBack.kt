package me.shetj.base.network.callBack

interface ProgressResponseCallBack {
    /**
     * 回调进度
     *
     * @param bytesWritten  当前读取响应体字节长度
     * @param contentLength 总长度
     * @param done          是否读取完成
     */
    fun onResponseProgress(bytesWritten: Long, contentLength: Long, done: Boolean)
}