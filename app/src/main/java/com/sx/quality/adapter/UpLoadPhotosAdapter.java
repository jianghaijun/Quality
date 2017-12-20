package com.sx.quality.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.sx.quality.activity.R;
import com.sx.quality.activity.ShowPhotosActivity;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.PictureBean;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.model.PictureModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Administrator
 * @time 2017/10/10 0010 21:44
 */

public class UpLoadPhotosAdapter extends RecyclerView.Adapter<UpLoadPhotosAdapter.UpLoadPhoneHolder> {
    private Activity mContext;
    private RequestOptions options;
    private List<ContractorListPhotosBean> upLoadPhoneList;

    public UpLoadPhotosAdapter(Context mContext, List<ContractorListPhotosBean> upLoadPhoneList) {
        this.mContext = (Activity) mContext;
        this.upLoadPhoneList = upLoadPhoneList;
        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);
    }

    @Override
    public UpLoadPhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UpLoadPhoneHolder(LayoutInflater.from(mContext).inflate(R.layout.item_uo_laod_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(UpLoadPhoneHolder holder, final int position) {
        ObjectAnimator anim = ObjectAnimator.ofInt(holder.ivUpLoadPhone, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        Glide.with(mContext)
                .load(upLoadPhoneList.get(position).getThumbPath())
                .apply(options)
                .thumbnail(0.1f)
                .into(holder.ivUpLoadPhone);

        holder.ivUpLoadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, ShowPhotosActivity.class);
                intent.putExtra("thumbUrl", upLoadPhoneList.get(position).getThumbPath());
                intent.putExtra("photoUrl", upLoadPhoneList.get(position).getPictureAddress());
                intent.putExtra("isUpload", upLoadPhoneList.get(position).getIsToBeUpLoad());*/
                // 图片浏览
                ArrayList<String> urls = new ArrayList<>();
                int len = upLoadPhoneList.size();
                for (int i = 0; i < len; i++) {
                    String fileUrl = upLoadPhoneList.get(i).getPictureAddress();
                    if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                        fileUrl = ConstantsUtil.FILE_BASE_URL + fileUrl;
                    }
                    urls.add(fileUrl);
                }
                Intent intent = new Intent(mContext, ShowPhotosActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(position));
                mContext.startActivity(intent);
            }
        });

        /**
         * 长按事件
         */
        holder.ivUpLoadPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PromptDialog promptDialog = new PromptDialog(mContext, new ChoiceListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            // 删除照片
                            DataSupport.deleteAll(ContractorListPhotosBean.class, "pictureAddress=?", upLoadPhoneList.get(position).getPictureAddress());
                            upLoadPhoneList.remove(position);
                            UpLoadPhotosAdapter.this.notifyDataSetChanged();
                        }
                    }
                }, "提示", "是否删除此照片？", "否", "是");
                promptDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return upLoadPhoneList.size();
    }

    public class UpLoadPhoneHolder extends RecyclerView.ViewHolder {
        private ImageView ivUpLoadPhone;

        public UpLoadPhoneHolder(View itemView) {
            super(itemView);
            ivUpLoadPhone = (ImageView) itemView.findViewById(R.id.ivUpLoadPhone);
        }
    }

}
