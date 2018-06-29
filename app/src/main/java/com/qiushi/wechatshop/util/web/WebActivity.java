package com.qiushi.wechatshop.util.web;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.orhanobut.logger.Logger;
import com.qiushi.wechatshop.R;
import com.qiushi.wechatshop.model.PhoneInfo;
import com.qiushi.wechatshop.net.BaseResponse;
import com.qiushi.wechatshop.net.RetrofitManager;
import com.qiushi.wechatshop.net.exception.Error;
import com.qiushi.wechatshop.rx.BaseObserver;
import com.qiushi.wechatshop.ui.pay.PayResultActivity;
import com.qiushi.wechatshop.util.ToastUtils;
import com.qiushi.wechatshop.util.Utils;
import com.qiushi.wechatshop.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Rylynn on 2018-06-15.
 */
public class WebActivity extends SwipeBackActivity {
    public final static String PARAM_URL = "param_url";
    public final static String PARAM_TITLE = "param_title";
    private SonicSession sonicSession;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra(PARAM_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new HostSonicRuntime(getApplication()), new SonicConfig.Builder().build());
        }

        SonicSessionClientImpl sonicSessionClient;

        // step 2: Create SonicSession
        Map<String, String> header = Utils.getHttpHeaders();
        header.put("is-app", "1");
        header.put("version", PhoneInfo.getInstance().getVersion());
        sonicSession = SonicEngine.getInstance().createSession(url, new SonicSessionConfig.Builder().setCustomRequestHeaders(header).build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
            throw new UnknownError("create session fail!");
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        setContentView(R.layout.activity_webview);

        //Title
        String title = intent.getStringExtra(PARAM_TITLE);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.title)).setText(title);
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    Logger.e("onPageFinished = " + url);
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    //step 6: Call sessionClient.requestResource when host allow the application
                    // to return the local data .
                    Logger.e("shouldInterceptRequest = " + url);
                    if (!url.isEmpty() && url.startsWith("local")) {
                        getPayLocalData(url);
                    } else {
                        return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                    }
                }
                return null;
            }
        });

        WebSettings webSettings = webView.getSettings();

        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // step 5: webview is ready now, just tell session client to bind
        sonicSessionClient.bindWebView(webView);
        sonicSessionClient.clientReady();
    }

    /**
     * 获取支付订单号
     */
    private void getPayLocalData(String url) {
        Uri uri = Uri.parse(url);
        String numbers = uri.getQueryParameter("numbers");
        if (!TextUtils.isEmpty(numbers)) {
            String pay_type = uri.getQueryParameter("pay_type");
            if (!TextUtils.isEmpty(pay_type)) {
                try {
                    int type = Integer.parseInt(pay_type);
                    switch (type) {
                        case 1://微信
                            getWechatPay(numbers);
                            break;
                        case 2://支付宝
                            getAliPay(numbers);
                            break;
                    }
                } catch (NumberFormatException e) {
                    Logger.e("getPayLocalData  Wrong pay_type");
                }
            } else {
                ToastUtils.showError("支付信息出错，请重新下单");
            }
        } else {
            ToastUtils.showError("支付信息出错，请重新下单");
        }
    }

    /**
     * 微信
     */
    private void getWechatPay(String numbers) {
        Disposable disposable = RetrofitManager.INSTANCE.getService().getWechatPay(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new BaseObserver<PayReq>() {
                    @Override
                    protected void onHandleSuccess(PayReq payReq) {
                        if (null != payReq) {
                            if (!WXPayEntryActivity.pay(getApplicationContext(), payReq)) {
                                ToastUtils.showWarning("未安装微信客户端");
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(@NotNull Error e) {
                        ToastUtils.showError(e.getMsg());
                    }
                });
    }

    /**
     * 支付宝
     */
    private void getAliPay(String numbers) {
        Disposable disposable = RetrofitManager.INSTANCE.getService().getAliPay(numbers)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse<String>, ObservableSource<Map<String, String>>>() {
                    @Override
                    public ObservableSource<Map<String, String>> apply(BaseResponse<String> s) {
                        PayTask pay = new PayTask(WebActivity.this);
                        return Observable.just(pay.payV2(s.getData(), true));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Map<String, String>>() {
                    @Override
                    public void onNext(Map<String, String> map) {
                        Logger.d("aliResp status = " + map.get("resultStatus"));
                        Logger.d("aliResp result = " + map.get("result"));
                        Logger.d("aliResp memo = " + map.get("memo"));

                        switch (map.get("resultStatus")) {
                            case "9000":
                                startActivity(new Intent(WebActivity.this, PayResultActivity.class));
                                //PayResultActivity.SUCCESS
                                break;
                            case "8000":
                            case "6004":
                                ToastUtils.showError("支付状态异常");
                                startActivity(new Intent(WebActivity.this, PayResultActivity.class));
                                //PayResultActivity.FAILED
                                break;
                            case "4000":
                                startActivity(new Intent(WebActivity.this, PayResultActivity.class));
                                //PayResultActivity.FAILED
                                break;
                            case "5000":
                                ToastUtils.showWarning("重复请求");
                                break;
                            case "6001":
                                ToastUtils.showWarning("您已取消支付");
                                break;
                            case "6002":
                                ToastUtils.showError("网络错误");
                                break;
                            default:
                                ToastUtils.showError("支付错误");
                                startActivity(new Intent(WebActivity.this, PayResultActivity.class));
                                //PayResultActivity.FAILED
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            e.printStackTrace();
                            ToastUtils.showError(e.getMessage());
                        } else {
                            ToastUtils.showError("获取订单信息失败，请重试");
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
    }
}