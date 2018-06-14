package cn.bingoogolapple.photopicker.activity;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.gyf.barlibrary.ImmersionBar;

import cn.bingoogolapple.photopicker.R;

public abstract class BGAPPToolbarActivity extends AppCompatActivity {
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).barColor(android.R.color.white)
                .statusBarDarkFont(true, 0.2f)
                .navigationBarColor(android.R.color.white)
                .init();

        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy(); //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    @SuppressLint("RestrictedApi")
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.textBlack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ViewStubCompat viewStub = findViewById(R.id.viewStub);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewStub.getLayoutParams();
        lp.addRule(RelativeLayout.BELOW, R.id.toolbar);

        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @SuppressLint("RestrictedApi")
    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.textBlack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ViewStubCompat viewStub = findViewById(R.id.viewStub);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}