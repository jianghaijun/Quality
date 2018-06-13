package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.activity.ReviewProgressActivity;
import com.sx.quality.activity.V_3ContractorDetailsActivity;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.dialog.PhotoRequirementsDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.SpUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

public class WorkingProcedureListAdapter extends BaseAdapter<List<WorkingBean>> {
    private Activity mContext;
    private int pageType;

    public WorkingProcedureListAdapter(Context mContext, int pageType) {
        this.mContext = (Activity) mContext;
        this.pageType = pageType;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_working_procedure, parent, false));
        } else {
            return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_working_procedure_to_do, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MsgHolder) holder).bind((WorkingBean) getDataSet().get(position));
    }

    public class MsgHolder extends RecyclerView.ViewHolder {
        private TextView txtReviewProgress; // 审核状态
        private TextView txtProcedureName;  // 工序名称
        private TextView txtProcedurePath;  // 工序部位
        private ImageView imgViewProgress; // 拍照
        private ImageView imgViewTakePhoto; // 拍照
        private TextView txtProcedureState; // 拍照状态
        private TextView txtPersonals;      // 审核人员
        private TextView txtCheckTime;      // 检查时间
        private RelativeLayout rlProcedurePath;      // 检查时间
        private RelativeLayout rlBottom;      //

        public MsgHolder(View itemView) {
            super(itemView);
            txtReviewProgress = (TextView) itemView.findViewById(R.id.txtReviewProgress);
            txtProcedureName = (TextView) itemView.findViewById(R.id.txtProcedureName);
            txtProcedurePath = (TextView) itemView.findViewById(R.id.txtProcedurePath);
            txtProcedureState = (TextView) itemView.findViewById(R.id.txtProcedureState);
            txtPersonals = (TextView) itemView.findViewById(R.id.txtPersonals);
            txtCheckTime = (TextView) itemView.findViewById(R.id.txtCheckTime);
            imgViewTakePhoto = (ImageView) itemView.findViewById(R.id.imgViewTakePhoto);
            imgViewProgress = (ImageView) itemView.findViewById(R.id.imgViewProgress);
            rlProcedurePath = (RelativeLayout) itemView.findViewById(R.id.rlProcedurePath);
            rlBottom = (RelativeLayout) itemView.findViewById(R.id.rlBottom);
        }

        public void bind(WorkingBean data) {
            txtReviewProgress.setText(data.getTrackStatus());
            txtProcedureName.setText(data.getNodeName());
            txtProcedurePath.setText(data.getTitle());
            if (pageType == 1) {
                txtProcedureState.setText("待拍照");
                txtPersonals.setText(StrUtil.isEmpty(data.getCheckNameAll()) ? "未审核" : data.getCheckNameAll());
                txtCheckTime.setText(DateUtil.format(DateUtil.date(data.getEnterTime() == 0 ? System.currentTimeMillis() : data.getEnterTime()), "yyyy-MM-dd HH:mm:ss"));
                imgViewTakePhoto.setOnClickListener(new onClick(data));
            } else {
                txtProcedureState.setText(data.getNodeName());
                txtPersonals.setText(data.getSendUserName());
                txtCheckTime.setText(DateUtil.format(DateUtil.date(data.getSendTime() == 0 ? System.currentTimeMillis() : data.getSendTime()), "yyyy-MM-dd HH:mm:ss"));
            }

            rlBottom.setOnClickListener(new onClick(data));
            imgViewProgress.setOnClickListener(new onClick(data));
            txtReviewProgress.setOnClickListener(new onClick(data));
            rlProcedurePath.setOnClickListener(new onClick(data));
        }
    }

    /**
     * 点击事件
     */
    private class onClick implements View.OnClickListener {
        private WorkingBean workingBean;

        public onClick(WorkingBean workingBean) {
            this.workingBean = workingBean;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgViewTakePhoto:
                    List<ContractorListPhotosBean> phoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ?", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), workingBean.getProcessId()).find(ContractorListPhotosBean.class);
                    boolean isHave = phoneList == null || phoneList.size() == 0 ? false : true;
                    if (!workingBean.getTrackStatus().equals("0") || isHave) {
                        // 直接拍照--->详情
                        takePhotoActivity(workingBean, true);
                    } else {
                        // 提示拍照要求--->详情
                        PhotoRequirementsDialog dialog = new PhotoRequirementsDialog(mContext, new ChoiceListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    takePhotoActivity(workingBean, true);
                                }
                            }
                        }, workingBean);
                        dialog.show();
                    }
                    break;
                case R.id.imgViewProgress:
                case R.id.txtReviewProgress:
                    reviewProgressActivity(workingBean.getProcessId());
                    break;
                case R.id.rlBottom:
                case R.id.rlProcedurePath:
                    takePhotoActivity(workingBean, false);
                    break;
            }
        }
    }

    /**
     * 跳转到详情
     */
    private void takePhotoActivity(WorkingBean bean, boolean isPopTakePhoto) {
        Intent intent = new Intent(mContext, V_3ContractorDetailsActivity.class);
        intent.putExtra("processId", bean.getProcessId());
        intent.putExtra("processState", bean.getProcessState());
        intent.putExtra("processPath", bean.getLevelNameAll());
        intent.putExtra("taskId", bean.getTaskId());
        intent.putExtra("canCheck", bean.getCanCheck());
        intent.putExtra("isPopTakePhoto", isPopTakePhoto);
        intent.putExtra("flowId", bean.getFlowId());
        intent.putExtra("workId", bean.getWorkId());
        intent.putExtra("flowName", bean.getFlowName());
        intent.putExtra("isPopTakePhoto", isPopTakePhoto);
        mContext.startActivity(intent);
    }

    /**
     * 跳转审核进度界面
     */
    private void reviewProgressActivity(String processId) {
        Intent intent = new Intent(mContext, ReviewProgressActivity.class);
        intent.putExtra("processId", processId);
        mContext.startActivity(intent);
    }
}
