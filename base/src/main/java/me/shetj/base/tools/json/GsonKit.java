package me.shetj.base.tools.json;

import android.support.annotation.Keep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.util.LogUtil;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;

@SuppressWarnings("unchecked")
@Keep
public class GsonKit {
    private static Gson gson = null;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }
    /**
     * 将对象转换成json格式
     * @param ts
     * @return
     */
    public static String objectToJson(@NonNull Object ts) {
        try {
            String jsonStr = null;
            if (gson != null) {
                jsonStr = gson.toJson(ts);
            }
            return jsonStr;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }

    }
    /**
     * 将对象转换成json格式(并自定义日期格式)
     *
     * @param ts
     * @return
     */
    public static String objectToJson(@NonNull Object ts, final String dateformat) {
        try {
            gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                    SimpleDateFormat format = new SimpleDateFormat(dateformat);
                    return new JsonPrimitive(format.format(src));
                }
            }).setDateFormat(dateformat).create();
            return gson.toJson(ts);
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
    /**
     * 将json格式转换成list对象
     * @param jsonStr
     * @return
     */
    public static <T> List<T> jsonToList(@NonNull String jsonStr) {
       try {
           List<T> objList = null;
           if (gson != null) {
               Type type = new TypeToken<List<T>>() {}.getType();
               objList = gson.fromJson(jsonStr, type);
           }
           return objList;
       }catch (Exception e){
           LogUtil.e(e.getMessage());
           return null;
       }
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> GsonToListMaps(@NonNull String gsonString) {
        try {
            List<Map<String, T>> list = null;
            if (gson != null) {
                list = gson.fromJson(gsonString,
                        new TypeToken<List<Map<String, T>>>() {
                        }.getType());
            }
            return list;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
    /**
     * 转成list
     * 解决泛型问题
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(@NonNull String json, Class<T> cls) {
        try {
            Gson gson = new Gson();
            List<T> list = new ArrayList<T>();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for(final JsonElement elem : array){
                list.add(gson.fromJson(elem, cls));
            }
            return list;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> jsonToMap(@NonNull String gsonString) {

        try {
            Map<String, T> map = null;
            if (gson != null) {
                map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
                }.getType());
            }
            return map;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
    /**
     * 将json转换成bean对象
     * @param jsonStr
     * @return
     */
    public static <T> T jsonToBean(@NonNull String jsonStr, Class<T> cl) {
        try {
            T obj = null;
            if (gson != null) {
                obj = gson.fromJson(jsonStr, cl);
            }
            return obj;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
    /**
     * 根据key获得value值
     * @param jsonStr
     * @param key
     * @return
     */
    public static String getJsonValue(@NonNull String jsonStr, String key) {
        try {
            String rusObj = null;
            Map<String, String> rusMap = null;
            if (gson != null) {
                rusMap = gson.fromJson(jsonStr, new TypeToken<Map<String, String>>() {
                }.getType());
            }
            if (rusMap != null && rusMap.size() > 0) {
                rusObj = rusMap.get(key);
            }
            return rusObj;
        }catch (Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
    }
}