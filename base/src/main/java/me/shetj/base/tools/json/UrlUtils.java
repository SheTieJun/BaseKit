package me.shetj.base.tools.json;

import android.support.annotation.Keep;

/**
 * <b>@packageName：</b> com.mobile.pipiti.utils.tools<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/2/2<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

@Keep
public class UrlUtils {
	private static final String HEX_STRING = "0123456789ABCDEF";

	/**
	 * 处理URL
	 * @param word
	 * @return 正常的URL
	 */
	public static String toBrowserCode(String word) {
		word = word.replaceAll(" ","%20");
		byte[] bytes = word.getBytes();

		//不包含中文，不做处理
		if (bytes.length == word.length()) {
			return word;
		}

		StringBuilder browserUrl = new StringBuilder();
		StringBuilder tempStr = new StringBuilder();

		for (int i = 0; i < word.length(); i++) {
			char currentChar = word.charAt(i);

			//不需要处理
			if ((int) currentChar <= 256) {

				if (tempStr.length() > 0) {
					byte[] cBytes = tempStr.toString().getBytes();

					for (int j = 0; j < cBytes.length; j++) {
						browserUrl.append('%');
						browserUrl.append(HEX_STRING.charAt((cBytes[j] & 0xf0) >> 4));
						browserUrl.append(HEX_STRING.charAt((cBytes[j] & 0x0f) >> 0));
					}
					tempStr = new StringBuilder();
				}

				browserUrl.append(currentChar);
			} else {
				//把要处理的字符，添加到队列中
				tempStr.append(currentChar);
			}
		}
		return browserUrl.toString();
	}
}
