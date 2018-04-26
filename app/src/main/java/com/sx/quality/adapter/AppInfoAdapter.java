package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sx.quality.activity.R;
import com.sx.quality.activity.UpLoadPhotosActivity;
import com.sx.quality.activity.V_2ContractorActivity;
import com.sx.quality.bean.AppInfoBean;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import java.util.List;

/**
 * Create dell By 2018/4/8 10:44
 */
public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.AppInfoHold> {
    private Activity mContext;
    private List<AppInfoBean> appInfoBeanList;

    public AppInfoAdapter(Context mContext, List<AppInfoBean> appInfoBeanList) {
        this.mContext = (Activity) mContext;
        this.appInfoBeanList = appInfoBeanList;
    }

    @Override
    public AppInfoHold onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHold(LayoutInflater.from(mContext).inflate(R.layout.item_app_info, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoHold holder, final int position) {
        Drawable top = ContextCompat.getDrawable(mContext, appInfoBeanList.get(position).getImgUrl());
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        holder.btnInfo.setCompoundDrawables(null, top, null, null);
        holder.btnInfo.setText(appInfoBeanList.get(position).getTitle());

        holder.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (position) {
                    case 4:
                        intent = new Intent(mContext, V_2ContractorActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "0");
                        mContext.startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(mContext, V_2ContractorActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "1");
                        mContext.startActivity(intent);
                        break;
                    // 待上传照片
                    case 6:
                        intent = new Intent(mContext, UpLoadPhotosActivity.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        ToastUtil.showShort(mContext, "该功能正在开发中，敬请期待!");
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfoBeanList.size();
    }

    public class AppInfoHold extends RecyclerView.ViewHolder {
        private Button btnInfo;

        public AppInfoHold(View itemView) {
            super(itemView);
            btnInfo = (Button) itemView.findViewById(R.id.btnInfo);
        }
    }

}
