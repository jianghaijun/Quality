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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sx.quality.activity.ContractorDetailsActivity;
import com.sx.quality.activity.R;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.NewContractorListBean;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.ShowPhotoListener;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.FileUtil;
import com.sx.quality.utils.ImageUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;
import com.sx.quality.view.MLImageView;

import org.litepal.crud.DataSupport;

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
    private String nodeId;

    public ContractorDetailsAdapter(Context mContext, List<ContractorListPhotosBean> phoneListBean, ShowPhotoListener listener, String nodeId) {
        this.mContext = mContext;
        this.listener = listener;
        this.nodeId = nodeId;
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
            Glide.with(mContext).load(R.drawable.add).apply(options).into(holder.ivUpLoadPhone);
            holder.txtStatus.setVisibility(View.GONE);
            holder.txtStatus.setText("");
            holder.ivIsChoose.setVisibility(View.GONE);
        } else {
            String fileUrl = phoneListBean.get(position).getThumbPath();
            if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                fileUrl = ConstantsUtil.FILE_BASE_URL + fileUrl;
            }

            if (phoneListBean.get(position).isCanSelect()) {
                holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_select));
            } else {
                holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_un_select));
            }

            if (ContractorDetailsActivity.isCanSelect) {
                holder.ivIsChoose.setVisibility(View.VISIBLE);
            } else {
                holder.ivIsChoose.setVisibility(View.GONE);
            }

            holder.txtStatus.setVisibility(View.VISIBLE);

            if (phoneListBean.get(position).getCheckFlag().equals("0")) {
                holder.txtStatus.setText("待审核");
            } else if (phoneListBean.get(position).getCheckFlag().equals("1") || phoneListBean.get(position).getCheckFlag().equals("2") || phoneListBean.get(position).getCheckFlag().equals("3")) {
                holder.txtStatus.setText("审核中");
            } else if (phoneListBean.get(position).getCheckFlag().equals("4")) {
                holder.txtStatus.setText("审核通过");
                // 如果有审核通过的就设置该工序为已完成状态
                List<NewContractorListBean> bean = DataSupport.where("nodeId = ?", nodeId).find(NewContractorListBean.class);
                if (bean != null && bean.size() != 0) {
                    bean.get(0).setIsFinish("1");
                    bean.get(0).saveOrUpdate("nodeId=?", nodeId);
                }
            } else if (phoneListBean.get(position).getCheckFlag().equals("5")) {
                holder.txtStatus.setText("审核未通过");
            } else {
                holder.txtStatus.setText("未上传");
            }

            Glide.with(mContext)
                    .load(fileUrl)
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
                    //listener.selectWayOrShowPhoto(false, phoneListBean.get(position).getThumbPath(), phoneListBean.get(position).getPictureAddress(), phoneListBean.get(position).getIsToBeUpLoad());
                    // 图片浏览方式
                    listener.selectWayOrShowPhoto(false, String.valueOf(position), "", phoneListBean.get(position).getIsToBeUpLoad());
                }
            }
        });

        holder.ivIsChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneListBean.get(position).getIsToBeUpLoad() == 1) {
                    ToastUtil.showShort(mContext, "未上传的照片不能进行审核操作，请先上传照片。");
                } else {
                    if (phoneListBean.get(position).getCheckFlag().equals("1") || phoneListBean.get(position).getCheckFlag().equals("2") || phoneListBean.get(position).getCheckFlag().equals("3")) {
                        ToastUtil.showShort(mContext, "照片正在审核中，不能再次提交审核！");
                    } else if (phoneListBean.get(position).getCheckFlag().equals("4")) {
                        ToastUtil.showShort(mContext, "照片已审核通过，不能再次提交审核！");
                    } else if (phoneListBean.get(position).getCheckFlag().equals("5")) {
                        ToastUtil.showShort(mContext, "照片审核未通过，不能再次提交审核！");
                    } else {
                        if (phoneListBean.get(position).isCanSelect()) {
                            holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_un_select));
                        } else {
                            holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_select));
                        }
                        phoneListBean.get(position).setCanSelect(!phoneListBean.get(position).isCanSelect());
                    }
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
        private ImageView ivIsChoose;
        private TextView txtStatus;

        public ContractorDetailsHolder(View itemView) {
            super(itemView);
            ivUpLoadPhone = (ImageView) itemView.findViewById(R.id.ivUpLoadPhone);
            ivIsChoose = (ImageView) itemView.findViewById(R.id.ivIsChoose);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        }
    }

}
