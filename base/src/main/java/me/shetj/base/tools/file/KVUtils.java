package me.shetj.base.tools.file;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.Set;

import timber.log.Timber;

/**
 * <b>@packageName：</b> me.shetj.base.tools.json<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/9/30 0030<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> MMKV 是基于 mmap 内存映射的 key-value 组件，
 * 底层序列化/反序列化使用 protobuf 实现，性能高，稳定性强。 <br>
 */
public class KVUtils {

	private KVUtils(){
		throw new UnsupportedOperationException("u can't instantiate me...");
	}

	private  static MMKV kv = MMKV.defaultMMKV(1,"base_shetj");

	public static void init(Context context){
		String rootDir = MMKV.initialize(context);
		Timber.i("KVUtils rootDir = %s",rootDir);
	}

	public static void  put( String key, Object object){
		if (object instanceof String)
		{
			kv.putString(key, (String) object);
		} else if (object instanceof Integer)
		{
			kv.putInt(key, (Integer) object);
		} else if (object instanceof Boolean)
		{
			kv.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float)
		{
			kv.putFloat(key, (Float) object);
		} else if (object instanceof Long)
		{
			kv.putLong(key, (Long) object);
		}  else if (object instanceof Set)
		{
			kv.putStringSet(key,  (Set<String> )object);
		}else
		{
			kv.putString(key, object.toString());
		}

	}


	public static Object  get(String key, Object defaultObject)
	{
		if (defaultObject instanceof String)
		{
			return kv.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer)
		{
			return kv.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean)
		{
			return kv.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float)
		{
			return kv.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long)
		{
			return kv.getLong(key, (Long) defaultObject);
		}else if (defaultObject instanceof Set){
			return kv.getStringSet(key, (Set<String>) defaultObject);
		}
		return null;
	}

}
