package com.sx.quality.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sx.quality.activity.R;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.ShowPhotoListener;
import com.sx.quality.utils.FileUtil;
import com.sx.quality.utils.ImageUtil;
import com.sx.quality.view.MLImageView;

import java.util.List;

/**
 * @author Administrator
 * @time 2017/10/10 0010 21:44
 */

public class ContractorDetailsAdapter extends RecyclerView.Adapter<ContractorDetailsAdapter.ContractorDetailsHolder> {
    private Context mContext;
    private ShowPhotoListener listener;
    private List<ContractorListPhotosBean> phoneListBean;
    private RequestOptions options;

    public ContractorDetailsAdapter(Context mContext, List<ContractorListPhotosBean> phoneListBean, ShowPhotoListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.phoneListBean = phoneListBean;
        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);
    }

    @Override
    public ContractorDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContractorDetailsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_uo_laod_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContractorDetailsHolder holder, final int position) {
        ObjectAnimator anim = ObjectAnimator.ofInt(holder.ivUpLoadPhone, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        if (position == 0) {
            //holder.ivUpLoadPhone.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.add));
            Glide.with(mContext).load(R.drawable.add).apply(options).into(holder.ivUpLoadPhone);
        } else {
            Glide.with(mContext)
                    .load(phoneListBean.get(position).getThumbPath())
                    .apply(options)
                    .thumbnail(0.1f)
                    .into(holder.ivUpLoadPhone);
        }

        holder.ivUpLoadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用相机或相册
                if (position == 0) {
                    listener.selectWayOrShowPhoto(true, "", "", 0);
                } else {
                    listener.selectWayOrShowPhoto(false, phoneListBean.get(position).getThumbPath(), phoneListBean.get(position).getPictureAddress(), phoneListBean.get(position).getIsToBeUpLoad());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneListBean.size();
    }

    public class ContractorDetailsHolder extends RecyclerView.ViewHolder {
        private ImageView ivUpLoadPhone;

        public ContractorDetailsHolder(View itemView) {
            super(itemView);
            ivUpLoadPhone = (ImageView) itemView.findViewById(R.id.ivUpLoadPhone);
        }
    }

}
