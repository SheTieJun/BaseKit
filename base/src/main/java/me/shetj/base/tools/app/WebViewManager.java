package me.shetj.base.tools.app;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * WebView管理器，提供常用设置
 * @author Administrator
 */
public class WebViewManager {
	private WebView webView;
	private WebSettings webSettings;
	
	public WebViewManager(WebView webView){
		this.webView = webView;
        webSettings = webView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
	}


	/**
	 * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
	 */
	public void imgReset() {
		webView.loadUrl("javascript:(function(){" +
						"var objs = document.getElementsByTagName('img'); " +
						"for(var i=0;i<objs.length;i++)  " +
						"{"
						+    "var img = objs[i];   " +
						"    img.style.maxWidth = '100%';" +
						"    img.style.height = 'auto';  " +
						"}" +
						"})()");
	}

	/**
	 * 这段js函数的功能就是，
	 * 遍历所有的img节点，
	 * 并添加onclick函数，
	 * 函数的功能是在图片点击的时候调用本地java接口并传递url过去
	 */
	public void addImageClickListner() {
		webView.loadUrl("javascript:(function(){" +
						"var objs = document.getElementsByTagName(\"img\"); " +
						"for(var i=0;i<objs.length;i++)  " +
						"{"
						+ "    objs[i].onclick=function()  " +
						"    {  "
						+ "        window.imagelistner.openImage(this.src);  " +
						"    }  " +
						"}" +
						"})()");
	}

    /**
     * 开启自适应功能
     */
    public WebViewManager enableAdaptive(){
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        return this;
    }

    /**
     * 禁用自适应功能
     */
    public WebViewManager disableAdaptive(){
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        return this;
    }

    /**
     * 开启缩放功能
     */
    public WebViewManager enableZoom(){
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        return this;
    }

    /**
     * 禁用缩放功能
     */
    public WebViewManager disableZoom(){
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setBuiltInZoomControls(false);
        return this;
    }

    /**
     * 开启JavaScript
     */
    @SuppressLint("SetJavaScriptEnabled")
    public WebViewManager enableJavaScript(){
        webSettings.setJavaScriptEnabled(true);
        return this;
    }

    /**
     * 禁用JavaScript
     */
    public WebViewManager disableJavaScript(){
        webSettings.setJavaScriptEnabled(false);
        return this;
    }
    
    /**
     * 开启JavaScript自动弹窗
     */
    public WebViewManager enableJavaScriptOpenWindowsAutomatically(){
    	webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    	return this;
    }
    
    /**
     * 禁用JavaScript自动弹窗
     */
    public WebViewManager disableJavaScriptOpenWindowsAutomatically(){
    	webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
    	return this;
    }

    /**
     * 返回
     * @return true：已经返回，false：到头了没法返回了
     */
	public boolean goBack(){
		if(webView.canGoBack()){
			webView.goBack();
			return true;
		}else{
			return false;
		}
	}
}