package me.shetj.base.http.xutils.download;

import android.support.annotation.Keep;
import android.text.TextUtils;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.LogUtil;
import org.xutils.db.converter.ColumnConverterFactory;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 下午8:10
 */
@Keep
public final class DownloadManager {

  static {
    // 注册DownloadState在数据库中的值类型映射
    ColumnConverterFactory.registerColumnConverter(DownloadState.class, new DownloadStateConverter());
  }

  private static DownloadManager instance;
  // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
  private final static int MAX_DOWNLOAD_THREAD = 2;

  private final DbManager db;
  private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);
  private final List<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
  private final ConcurrentHashMap<DownloadInfo, DownloadCallback>
  callbackMap = new ConcurrentHashMap<DownloadInfo, DownloadCallback>(5);
  private DownloadManager() {
    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
    .setDbName("download")
    .setDbVersion(1);
    db = x.getDb(daoConfig);
    try {
      List<DownloadInfo> infoList = db.selector(DownloadInfo.class).findAll();
      if (infoList != null) {
        for (DownloadInfo info : infoList) {
          if (info.getState().value() < DownloadState.FINISHED.value()) {
            info.setState(DownloadState.STOPPED);
          }
          downloadInfoList.add(info);
        }
      }
    } catch (DbException ex) {
      LogUtil.e(ex.getMessage(), ex);
    }
  }

  /*package*/
  public static DownloadManager getInstance() {
    if (instance == null) {
      synchronized (DownloadManager.class) {
        if (instance == null) {
          instance = new DownloadManager();
        }
      }
    }
    return instance;
  }

  public void updateDownloadInfo(DownloadInfo info) throws DbException {
    db.update(info);
  }

  public int getDownloadListCount() {
    return downloadInfoList.size();
  }

  public DownloadInfo getDownloadInfo(int index) {
    return downloadInfoList.get(index);
  }

  public List<DownloadInfo> getDownloadInfo(){
    return downloadInfoList;
  }
  
  public synchronized void startDownload(String url, String label, String savePath,String labelUrl,
      boolean autoResume, boolean autoRename,
      DownloadViewHolder viewHolder) throws DbException {
    String fileSavePath = new File(savePath).getAbsolutePath();
    DownloadInfo downloadInfo = db.selector(DownloadInfo.class)
        .where("label", "=", label)
        .and("fileSavePath", "=", fileSavePath)
        .findFirst();
    if (downloadInfo != null) {
      DownloadCallback callback = callbackMap.get(downloadInfo);
      if (callback != null) {
        if (viewHolder == null) {
          viewHolder = new DefaultDownloadViewHolder(null, downloadInfo);
        }
        if (callback.switchViewHolder(viewHolder)) {
          return;
        } else {
          callback.cancel();
        }
      }
    }

    if (downloadInfo == null) {
      downloadInfo = new DownloadInfo();
      downloadInfo.setUrl(url);
      downloadInfo.setAutoRename(autoRename);
      downloadInfo.setAutoResume(autoResume);
      downloadInfo.setLabel(label);
      downloadInfo.setLabelUrl(labelUrl);
      downloadInfo.setFileSavePath(fileSavePath);
      db.saveBindingId(downloadInfo);
    }

    // start downloading
    if (viewHolder == null) {
      viewHolder = new DefaultDownloadViewHolder(null, downloadInfo);
    } else {
      viewHolder.update(downloadInfo);
    }
    DownloadCallback callback = new DownloadCallback(viewHolder);
    callback.setDownloadManager(this);
    RequestParams params = new RequestParams(url);
    params.setAutoResume(downloadInfo.isAutoResume());
    params.setAutoRename(downloadInfo.isAutoRename());
    params.setSaveFilePath(downloadInfo.getFileSavePath());
    params.setExecutor(executor);
    params.setCancelFast(true);
    Callback.Cancelable cancelable = x.http().get(params, callback);
    callback.setCancelable(cancelable);
    callbackMap.put(downloadInfo, callback);
    if (downloadInfoList.contains(downloadInfo)) {
      int index = downloadInfoList.indexOf(downloadInfo);
      downloadInfoList.remove(downloadInfo);
      downloadInfoList.add(index, downloadInfo);
    } else {
      downloadInfoList.add(downloadInfo);
    }
  }

  public void stopDownload(int index) {
    DownloadInfo downloadInfo = downloadInfoList.get(index);
    stopDownload(downloadInfo);
  }

  public void stopDownload(DownloadInfo downloadInfo) {
    Callback.Cancelable cancelable = callbackMap.get(downloadInfo);
    if (cancelable != null) {
      cancelable.cancel();
    }
  }

  public void stopAllDownload() {
    for (DownloadInfo downloadInfo : downloadInfoList) {
      Callback.Cancelable cancelable = callbackMap.get(downloadInfo);
      if (cancelable != null) {
        cancelable.cancel();
      }
    }
  }

  public void removeDownload(int index) throws DbException {
    DownloadInfo downloadInfo = downloadInfoList.get(index);
    db.delete(downloadInfo);
    stopDownload(downloadInfo);
    downloadInfoList.remove(index);
  }

  public void removeDownload(DownloadInfo downloadInfo) throws DbException {
    db.delete(downloadInfo);
    stopDownload(downloadInfo);
    downloadInfoList.remove(downloadInfo);
  }




  /**
   * 方法名：  iscache	<br>
   * 方法描述：TODO  是否缓存了<br>
   * 修改备注： 如果缓存了 并且完成了 就播放缓存 否则播放网络数据<br>
   * 创建时间： 2016-4-26上午10:03:01<br>
   * @return
   */
  public String isCache(String url){
    if (url.startsWith("http:")) {
      for (DownloadInfo downloadInfo : downloadInfoList) {
        if (TextUtils.equals(downloadInfo.getUrl(), url)) {
          if (downloadInfo.getState() == DownloadState.FINISHED) {
            if (new File(downloadInfo.getFileSavePath()).exists()) {
              return downloadInfo.getFileSavePath();
            }else{
              try {
                removeDownload(downloadInfo);
                return url;
              } catch (DbException e) {
                e.printStackTrace();
              }
            }
          } else {
            try {
              startDownload(
                      downloadInfo.getUrl(),
                      downloadInfo.getLabel(),
                      downloadInfo.getFileSavePath(),
                      downloadInfo.getLabelUrl(),
                      downloadInfo.isAutoResume(),
                      downloadInfo.isAutoRename(),
                      null);
            } catch (DbException e) {
              e.printStackTrace();
            }
            return url;
          }
        }
      }
    }
    return url;
  }


  /**
   * 方法名：  iscache	<br>
   * 方法描述：TODO  是否缓存了<br>
   * 修改备注： 如果缓存了 并且完成了 就播放缓存 否则播放网络数据<br>
   * 创建时间： 2016-4-26上午10:03:01<br>
   * @return
   */
  public boolean isExist(String url){
    for (DownloadInfo downloadInfo : downloadInfoList) {
      if(TextUtils.equals(downloadInfo.getUrl(),url))
      {
        return true;
      }
    }
    return false;
  }
}
