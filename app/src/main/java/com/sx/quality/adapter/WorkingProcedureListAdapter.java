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

    public WorkingProcedureListAdapter(Context mContext) {
        this.mContext = (Activity) mContext;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_working_procedure, parent, false));
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
        }

        public void bind(WorkingBean data) {
            txtReviewProgress.setText("待审核");
            txtProcedureName.setText(data.getProcessName());
            txtProcedurePath.setText(data.getLevelNameAll().replace(",", "→"));
            txtProcedureState.setText("待拍照");
            txtPersonals.setText(StrUtil.isEmpty(data.getCheckNameAll()) ? "未审核" : data.getCheckNameAll());
            txtCheckTime.setText(DateUtil.format(DateUtil.date(data.getEnterTime() == 0.0 || data.getEnterTime() == 0 ? System.currentTimeMillis() : data.getEnterTime()), "yyyy-MM-dd HH:mm:ss"));
            imgViewTakePhoto.setOnClickListener(new onClick(data));
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
                    if (!workingBean.getProcessState().equals("0") || isHave) {
                        // 直接拍照--->详情
                        takePhotoActivity(workingBean, true);
                    } else {
                        // 提示拍照要求--->详情
                        workingBean.setProcessName("混凝土");
                        workingBean.setPhotoNumber("3");
                        workingBean.setPhotoDistance("整体+局部特写");
                        workingBean.setPhotoContent("必须入像：标识牌（工程名称、分项/工序、技术员名字、监理名字、拍摄时间），责任人（现场技术员、现场监理、班组长），工程工序");
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
