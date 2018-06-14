package cn.bingoogolapple.photopicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

public class BGAPhotoPickerAdapter extends BGARecyclerViewAdapter<String> {
    private ArrayList<String> mSelectedPhotos = new ArrayList<>();
    private int mPhotoSize;
    private boolean mTakePhotoEnabled;

    public BGAPhotoPickerAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.bga_pp_item_photo_picker);
        mPhotoSize = BGAPhotoPickerUtil.getScreenWidth() / 6;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTakePhotoEnabled && position == 0) {
            return R.layout.bga_pp_item_photo_camera;
        } else {
            return R.layout.bga_pp_item_photo_picker;
        }
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        if (viewType == R.layout.bga_pp_item_photo_camera) {
            helper.setItemChildClickListener(R.id.iv_item_photo_camera_camera);
        } else {
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_flag);
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_photo);
        }
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String model) {
        if (getItemViewType(position) == R.layout.bga_pp_item_photo_picker) {
            BGAImage.display(helper.getImageView(R.id.iv_item_photo_picker_photo), R.mipmap.bga_pp_ic_holder_dark, model, mPhotoSize);

            if (mSelectedPhotos.contains(model)) {
                for (int i = 0; i < mSelectedPhotos.size(); i++) {
                    if (model.equals(mSelectedPhotos.get(i))) {
                        ((TextView) helper.getView(R.id.iv_item_photo_picker_flag)).setText(String.valueOf(i + 1));
                    }
                }
                helper.getView(R.id.iv_item_photo_picker_flag).setBackgroundResource(R.mipmap.bga_pp_ic_cb_selected);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(helper.getConvertView().getResources().getColor(R.color.bga_pp_photo_selected_mask));
            } else {
                helper.getView(R.id.iv_item_photo_picker_flag).setBackgroundResource(R.mipmap.bga_pp_ic_cb_normal);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(null);
                ((TextView) helper.getView(R.id.iv_item_photo_picker_flag)).setText("");
            }
        }
    }

    public void setSelectedPhotos(ArrayList<String> selectedPhotos) {
        if (selectedPhotos != null) {
            mSelectedPhotos = selectedPhotos;
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public int getSelectedCount() {
        return mSelectedPhotos.size();
    }

    public void setPhotoFolderModel(BGAPhotoFolderModel photoFolderModel) {
        mTakePhotoEnabled = photoFolderModel.isTakePhotoEnabled();
        setData(photoFolderModel.getPhotos());
    }
}