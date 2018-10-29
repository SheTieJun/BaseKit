package me.shetj.base.base;


import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载
 * @author shetj
 */
public interface ImageLoader {

    /**
     * 加载图片
     * @param simpleView 容器
     * @param url url地址
     */
    void load(ImageView simpleView, String url);
    /**
     * 加载图片 是否展示进度
     * @param simpleView 容器
     * @param url url地址
     * @param hasProgress 是否展示进度
     */
    void load(ImageView simpleView, String url, boolean hasProgress);

    /**
     * 渐进式展示图片
     * @param mSimpleView 容器
     * @param url url地址
     */
    void loadProgressive(ImageView mSimpleView, String url);

    /**
     * 加载GIf
     * @param simpleView view
     * @param url url地址(uri)
     * @param isAuto 是否自动播放
     */
    void loadGif(ImageView simpleView, String url, boolean isAuto);


    /**
     * 预加载图片
     * @param url 图片地址
     * @param isDiskCacheOrBitmapCache  true Disk 或者 false 内存
     */
    void prefetchImage(Context context, String url, boolean isDiskCacheOrBitmapCache);

    /**
     * 获取view
     * @param context
     * @return {@link ImageView}
     */
    <T extends ImageView> T getSimpleView(Context context, String url);

    /**
     * 清理内存
     */
    void clearMemCache();

    /**
     * 清理缓存
     */
    void clearCacheFiles();
}
