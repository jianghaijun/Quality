package com.sx.quality.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sx.quality.activity.R;
import com.sx.quality.activity.ShowPhotosActivity;
import com.sx.quality.bean.ContractorListPhotosBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @time 2017/10/10 0010 21:44
 */

public class UpLoadPhotosAdapter extends RecyclerView.Adapter<UpLoadPhotosAdapter.UpLoadPhoneHolder> {
    private Context mContext;
    private RequestOptions options;
    private List<ContractorListPhotosBean> upLoadPhoneList;

    public UpLoadPhotosAdapter(Context mContext, List<ContractorListPhotosBean> upLoadPhoneList) {
        this.mContext = mContext;
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
                    urls.add(upLoadPhoneList.get(i).getPictureAddress());
                }
                Intent intent = new Intent(mContext, ShowPhotosActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(position));
                mContext.startActivity(intent);
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
