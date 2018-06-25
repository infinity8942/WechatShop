package com.qiushi.wechatshop.util.web;

import android.os.Bundle;
import android.webkit.WebView;

import com.tencent.sonic.sdk.SonicSessionClient;

import java.util.HashMap;

/**
 * Created by Rylynn on 2018-06-15.
 */
public class SonicSessionClientImpl extends SonicSessionClient {
    public WebView getWebView() {
        return webView;
    }

    private WebView webView;

    public void bindWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void loadUrl(String url, Bundle extraData) {
        webView.loadUrl(url);
    }

    @Override
    public void loadDataWithBaseUrl(String baseUrl, String data, String mimeType, String encoding,
                                    String historyUrl) {
        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadDataWithBaseUrlAndHeader(String baseUrl, String data, String mimeType, String encoding, String historyUrl, HashMap<String, String> headers) {
    }
}