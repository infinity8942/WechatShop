package cn.bingoogolapple.photopicker.imageloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class BGARVOnScrollListener extends RecyclerView.OnScrollListener {
    private Context mActivity;

    public BGARVOnScrollListener(Context activity) {
        mActivity = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            BGAImage.resume(mActivity);
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            BGAImage.pause(mActivity);
        }
    }
}