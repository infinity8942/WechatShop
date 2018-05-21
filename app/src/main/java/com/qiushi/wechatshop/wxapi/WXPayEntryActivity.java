package com.qiushi.wechatshop.wxapi;

import android.content.Context;
import android.os.Bundle;

import com.qiushi.wechatshop.R;
import com.qiushi.wechatshop.base.WActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends WActivity implements IWXAPIEventHandler {
    private static String APP_ID;

    @Override
    public int layoutId() {
        return R.layout.activity_wxpay_entry;
    }

    public static boolean pay(Context context, PayReq payReq) {
        APP_ID = payReq.appId;
        IWXAPI api = WXAPIFactory.createWXAPI(context, APP_ID);
        if (api.isWXAppInstalled()) {
            api.registerApp(APP_ID);
            api.sendReq(payReq);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        RxBus.getDefault().post(resp);//TODO
        finish();
    }

    @Override
    protected void onDestroy() {
        APP_ID = null;
        super.onDestroy();
    }
}