package cnn.bingoogolapple.photopicker.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.bingoogolapple.photopicker.R;
import cnn.bingoogolapple.photopicker.imageloader.BGAImage;
import cnn.bingoogolapple.photopicker.util.BGABrowserPhotoViewAttacher;
import cnn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cnn.bingoogolapple.photopicker.widget.BGAImageView;
import ukk.co.senab.photoview.PhotoViewAttacher;

public class BGAPhotoPageAdapter extends PagerAdapter {
    private ArrayList<String> mPreviewPhotos;
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;

    public BGAPhotoPageAdapter(PhotoViewAttacher.OnViewTapListener onViewTapListener, ArrayList<String> previewPhotos) {
        mOnViewTapListener = onViewTapListener;
        mPreviewPhotos = previewPhotos;
    }

    @Override
    public int getCount() {
        return mPreviewPhotos == null ? 0 : mPreviewPhotos.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final BGAImageView imageView = new BGAImageView(container.getContext());
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final BGABrowserPhotoViewAttacher photoViewAttacher = new BGABrowserPhotoViewAttacher(imageView);
        photoViewAttacher.setOnViewTapListener(mOnViewTapListener);
        imageView.setDelegate(new BGAImageView.Delegate() {
            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth() && drawable.getIntrinsicHeight() > BGAPhotoPickerUtil.getScreenHeight()) {
                    photoViewAttacher.setIsSetTopCrop(true);
                    photoViewAttacher.setUpdateBaseMatrix();
                } else {
                    photoViewAttacher.update();
                }
            }
        });

        BGAImage.display(imageView, R.mipmap.bga_pp_ic_holder_dark, mPreviewPhotos.get(position), BGAPhotoPickerUtil.getScreenWidth(), BGAPhotoPickerUtil.getScreenHeight());

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public String getItem(int position) {
        return mPreviewPhotos == null ? "" : mPreviewPhotos.get(position);
    }
}