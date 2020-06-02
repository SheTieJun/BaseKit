package shetj.me.base.common.manager;

/**
 * Created by shetj
 * on 2017/9/28.
 *
 * @author shetj
 */

public class HttpOssManager {

    private static HttpOssManager instance = null;

    private HttpOssManager() {
    }

    public static HttpOssManager getInstance() {
        if (instance == null) {
            synchronized (HttpOssManager.class) {
                if (instance == null) {
                    instance = new HttpOssManager();
                }
            }
        }
        return instance;
    }

    public void getOSSFromSever() {
    }

}
