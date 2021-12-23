/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.network.body;

import androidx.annotation.NonNull;
import java.io.IOException;
import me.shetj.base.network.callBack.ProgressResponseCallBack;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import timber.log.Timber;

public class UploadProgressRequestBody extends RequestBody {

  protected RequestBody delegate;
  protected ProgressResponseCallBack progressCallBack;

  protected CountingSink countingSink;

  public UploadProgressRequestBody(ProgressResponseCallBack listener) {
    this.progressCallBack = listener;
  }

  public UploadProgressRequestBody(
      RequestBody delegate, ProgressResponseCallBack progressCallBack) {
    this.delegate = delegate;
    this.progressCallBack = progressCallBack;
  }

  public void setRequestBody(RequestBody delegate) {
    this.delegate = delegate;
  }

  @Override
  public MediaType contentType() {
    return delegate.contentType();
  }

  /** 重写调用实际的响应体的contentLength */
  @Override
  public long contentLength() {
    try {
      return delegate.contentLength();
    } catch (IOException e) {
      Timber.e(e);
      return -1;
    }
  }

  @Override
  public void writeTo(@NonNull BufferedSink sink) throws IOException {
    BufferedSink bufferedSink;

    countingSink = new CountingSink(sink);
    bufferedSink = Okio.buffer(countingSink);

    delegate.writeTo(bufferedSink);

    bufferedSink.flush();
  }

  protected final class CountingSink extends ForwardingSink {
    private long bytesWritten = 0;
    private long contentLength = 0; // 总字节长度，避免多次调用contentLength()方法
    private long lastRefreshUiTime; // 最后一次刷新的时间

    public CountingSink(Sink delegate) {
      super(delegate);
    }

    @Override
    public void write(@NonNull Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);
      if (contentLength <= 0) contentLength = contentLength(); // 获得contentLength的值，后续不再调用
      // 增加当前写入的字节数
      bytesWritten += byteCount;

      long curTime = System.currentTimeMillis();
      // 每100毫秒刷新一次数据,防止频繁无用的刷新
      if (curTime - lastRefreshUiTime >= 100 || bytesWritten == contentLength) {
        progressCallBack.onResponseProgress(
            bytesWritten, contentLength, bytesWritten == contentLength);
        lastRefreshUiTime = System.currentTimeMillis();
      }
      Timber.i("bytesWritten=" + bytesWritten + " ,totalBytesCount=" + contentLength);
    }
  }
}
