package com.qiushi.wechatshop.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiushi.wechatshop.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 载入等待布局
 * Created by Rylynn on 2017/12/19.
 */
public class LoadingDialog extends Dialog {

    private Context context;
    private String msg;
    private TextView loadingText;

    private AVLoadingIndicatorView avLoadingIndicatorView;

    public LoadingDialog(@NonNull Context context, String msg) {
        super(context, R.style.loading_dialog_style);
        this.context = context;
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.layout_loading_dialog, null);
        avLoadingIndicatorView = view.findViewById(R.id.AVLoadingIndicatorView);
        loadingText = view.findViewById(R.id.id_tv_loading_dialog_text);
        loadingText.setText(msg);
        setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        setCanceledOnTouchOutside(false);
    }

    public void setText(String newMsg) {
        if (null != loadingText) {
            loadingText.setText(newMsg);
        }
    }

    @Override
    public void show() {
        if (null != avLoadingIndicatorView) {
            avLoadingIndicatorView.smoothToShow();
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (null != avLoadingIndicatorView) {
            avLoadingIndicatorView.hide();
        }
        super.dismiss();
    }
}