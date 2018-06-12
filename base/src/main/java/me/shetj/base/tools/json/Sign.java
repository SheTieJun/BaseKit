package me.shetj.base.tools.json;

import android.support.annotation.Keep;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import me.shetj.base.tools.app.ArmsUtils;

/**
 * 签名算法
 * <p/>
 * 假设参与参数签名计算的请求参数分别是“k1”、“k2”、“k3”，它们的值分别是“v1”、“v2”、“v3”，则参数签名计算方法如下：
 * <p/>
 * 将请求参数格式化为“key=value”格式，即“k1=v1”、“k2=v2”、“k3=v3”；
 * 将格式化好的参数键值对以字典序升序排列后，拼接在一起，即“k1=v1k2=v2k3=v3”；
 * 在拼接好的字符串末尾追加上与服务器约定的应用secret参数值； 上述字符串的MD5值即为签名的值。
 */
@Keep
public class Sign {



    /**
     * 签名生成算法
     *
     * @param params <String,String> params 请求参数集，所有参数必须已转换为字符串类型
     * @param secret  secret 签名密钥
     * @return 签名
     * @throws IOException 输入输出异常
     */

    public static String getSignature(@NonNull Map<String, String> params, @NonNull String secret){
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<>(params);
        Set<Entry<String, String>> entrys = sortedParams.entrySet();

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder baseString = new StringBuilder();
        for (Entry<String, String> param : entrys) {
            baseString.append(param.getKey()).append("=").append(param.getValue());
        }
        baseString.append(secret);
        return ArmsUtils.encodeToMD5(baseString.toString());
    }

}
