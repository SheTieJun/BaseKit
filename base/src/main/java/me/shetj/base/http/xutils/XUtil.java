package me.shetj.base.http.xutils;



import android.app.Application;
import android.support.annotation.Keep;
import android.text.TextUtils;

import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

import me.shetj.base.tools.json.GsonKit;

/**
 * xutils 的工具类
 * @author shetj
 */
@Keep
public class XUtil {

    public static void init(Application application,boolean isDebug){
        x.Ext.init(application);
        x.Ext.setDebug(isDebug);
    }

    /**
     * 发送get请求 
     * @param <T> 
     */
    public static <T> Cancelable Get(String url, String headToken, Map<String,String> map, CommonCallback<T> callback){
        LogUtil.i("url = " + url);
        LogUtil.i("map = " + GsonKit.objectToJson(map));
        RequestParams params=new RequestParams(url);
        if (!TextUtils.isEmpty(headToken)) {
            params.setHeader("Authorization", "Bearer " + headToken);
        }
        if(null!=map){
            for(Map.Entry<String, String> entry : map.entrySet()){
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().get(params, callback);
    }

    /**
     * 发送get请求
     * @param <T>
     */
    public static <T> Cancelable Get(String url, Map<String,String> map, CommonCallback<T> callback){
        return Get(url,null,map,callback);
    }

    /**
     * 发送post请求
     * @param <T>
     */
    public static <T> Cancelable Post(String url, String headToken, Map<String,String> map, CommonCallback<T> callback){
        LogUtil.i("url = " + url);
        LogUtil.i("map = " + GsonKit.objectToJson(map));
        RequestParams params=new RequestParams(url);
        params.setHeader("Content-Type","application/json");
        if (!TextUtils.isEmpty(headToken)) {
            params.setHeader("Authorization", "Bearer " + headToken);
        }
        if(null!=map){
            for(Map.Entry<String, String> entry : map.entrySet()){
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().post(params, callback);
    }


    /**
     * 发送post请求
     * @param <T>
     */
    public static <T> Cancelable Post(String url, Map<String,String> map, CommonCallback<T> callback){
        return Post(url,null,map,callback);
    }
    /**
     * 发送post请求
     * @param <T>
     *           params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());
     */
    public static <T> Cancelable PostJson(String url, String headToken, String json, CommonCallback<T> callback){
        LogUtil.i("url = " + url);
        LogUtil.i("json = " + json);
        RequestParams params=new RequestParams(url);
        params.setHeader("Content-Type","application/json");
        if (null != headToken &&!TextUtils.isEmpty(headToken)) {
            params.setHeader("Authorization", "Bearer " + headToken);
        }
        params.setAsJsonContent(true);
        params.setBodyContent(json);
        return x.http().post(params, callback);
    }

    /**
     * 发送post请求
     * @param <T>
     *           params.setAsJsonContent(true);
    params.setBodyContent(js_request.toString());
     */
    public static <T> Cancelable PostJson(String url, String json, CommonCallback<T> callback){
        return PostJson(url,null,json,callback);
    }

    /**
     * 上传文件 
     * @param <T> 
     */
    public static <T> Cancelable UpLoadFile(String url, Map<String,String> map, CommonCallback<T> callback){
        LogUtil.i("url = " + url);
        LogUtil.i("UpLoadFile = " + GsonKit.objectToJson(map));
        RequestParams params=new RequestParams(url);
        if(null!=map){
            for(Map.Entry<String, String> entry : map.entrySet()){
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setMultipart(true);
        return x.http().post(params, callback);
    }

    /**
     * 下载文件 
     * @param <T> 
     */
    public static <T> Cancelable DownLoadFile(String url, String filePath, CommonCallback<T> callback){
        LogUtil.i("url = " + url);
        LogUtil.i("DownLoadFile = " + filePath);
        RequestParams params=new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(filePath);
        return x.http().post(params, callback);
    }

}


