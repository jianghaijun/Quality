package com.sx.quality.adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.R;
import com.sx.quality.activity.V_2ContractorDetailsActivity;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.NewContractorListBean;
import com.sx.quality.bean.PictureBean;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.ShowPhotoListener;
import com.sx.quality.model.PictureModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 */
public class V_2ContractorDetailsAdapter extends RecyclerView.Adapter<V_2ContractorDetailsAdapter.ContractorDetailsHolder> {
    private Activity mContext;
    private ShowPhotoListener listener;
    private List<ContractorListPhotosBean> phoneListBean;
    private RequestOptions options;
    private String levelId;

    public V_2ContractorDetailsAdapter(Context mContext, List<ContractorListPhotosBean> phoneListBean, ShowPhotoListener listener, String levelId) {
        this.mContext = (Activity) mContext;
        this.listener = listener;
        this.levelId = levelId;
        this.phoneListBean = phoneListBean;
        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);
    }

    @Override
    public ContractorDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContractorDetailsHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contractor_delite, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContractorDetailsHolder holder, final int position) {
        ObjectAnimator anim = ObjectAnimator.ofInt(holder.ivUpLoadPhone, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        String fileUrl = phoneListBean.get(position).getThumbPath();
        if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
            fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
        }

        // 是否可以选择
        if (phoneListBean.get(position).isCanSelect()) {
            holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_select));
        } else {
            holder.ivIsChoose.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_image_un_select));
        }

        if (V_2ContractorDetailsActivity.isCanSelect) {
            holder.ivIsChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsChoose.setVisibility(View.GONE);
        }

        holder.txtStatus.setVisibility(View.VISIBLE);

        if (phoneListBean.get(position).getCheckFlag().equals("0")) {
            holder.txtStatus.setText("待审核");
        } else if (phoneListBean.get(position).getCheckFlag().equals("1") || phoneListBean.get(position).getCheckFlag().equals("2")) {
            holder.txtStatus.setText("审核中");
        } else if (phoneListBean.get(position).getCheckFlag().equals("4")) {
            holder.txtStatus.setText("审核通过");
            // 如果有审核通过的就设置该工序为已完成状态
            List<NewContractorListBean> bean = DataSupport.where("levelId = ?", levelId).find(NewContractorListBean.class);
            if (bean != null && bean.size() != 0) {
                bean.get(0).setIsFinish("1");
                bean.get(0).saveOrUpdate("levelId=?", levelId);
            }
        } else if (phoneListBean.get(position).getCheckFlag().equals("3") || phoneListBean.get(position).getCheckFlag().equals("5")) {
            holder.txtStatus.setText("审核未通过");
        } else {
            holder.txtStatus.setText("未上传");
        }

        Glide.with(mContext)
                .load(fileUrl)
                .apply(options)
                .thumbnail(0.1f)
                .into(holder.ivUpLoadPhone);

        // 图片点击事件
        holder.ivUpLoadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (V_2ContractorDetailsActivity.isCanSelect) {
                    if (phoneListBean.get(position).getIsToBeUpLoad() == 1) {
                        ToastUtil.showShort(mContext, "未上传的照片不能进行审核操作，请先上传照片。");
                    } else {
                        if (phoneListBean.get(position).getCheckFlag().equals("1") || phoneListBean.get(position).getCheckFlag().equals("2")) {
                            ToastUtil.showShort(mContext, "照片正在审核中，不能再次提交审核！");
                        } else if (phoneListBean.get(position).getCheckFlag().equals("4")) {
                            ToastUtil.showShort(mContext, "照片已审核通过，不能再次提交审核！");
                        } else if (phoneListBean.get(position).getCheckFlag().equals("5") || phoneListBean.get(position).getCheckFlag().equals("3")) {
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
                } else {
                    // 图片浏览方式
                    listener.selectWayOrShowPhoto(false, String.valueOf(position), "", phoneListBean.get(position).getIsToBeUpLoad());
                }
            }
        });

        /**
         * 长按事件
         */
        holder.ivUpLoadPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (phoneListBean.get(position).getCheckFlag().equals("1") || phoneListBean.get(position).getCheckFlag().equals("2")) {
                    ToastUtil.showShort(mContext, "照片正在审核中，不能进行删除操作！");
                } else if (phoneListBean.get(position).getCheckFlag().equals("4")) {
                    ToastUtil.showShort(mContext, "照片已审核通过，不能进行删除操作！");
                } else {
                    PromptDialog promptDialog = new PromptDialog(mContext, new ChoiceListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                // 删除照片
                                if (1 == phoneListBean.get(position).getIsToBeUpLoad()) {
                                    DataSupport.deleteAll(ContractorListPhotosBean.class, "photoAddress=?", phoneListBean.get(position).getPhotoAddress());
                                    phoneListBean.remove(position);
                                    V_2ContractorDetailsAdapter.this.notifyDataSetChanged();
                                } else {
                                    deletePhoto(phoneListBean.get(position), position);
                                }
                            }
                        }
                    }, "提示", "是否删除此照片？", "否", "是");
                    promptDialog.show();
                }
                return true;
            }
        });

        // 选择按钮点击事件
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

    /**
     * 删除服务器上图片
     * @param bean
     * @param point
     */
    private void deletePhoto(final ContractorListPhotosBean bean, final int point) {
        LoadingUtils.showLoading(mContext);
        // 参数
        PictureModel model = new PictureModel();
        model.setSelectUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        model.setRootLevelId(levelId);
        List<PictureBean> beanList = new ArrayList<>();
        PictureBean picBean = new PictureBean();
        picBean.setPhotoId(bean.getPhotoId());
        beanList.add(picBean);
        model.setSxZlPhotoList(beanList);

        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());

        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.DELETE_PHOTOS)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();

        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showShort(mContext, mContext.getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, "删除成功！");
                                    phoneListBean.remove(point);
                                    DataSupport.deleteAll(ContractorListPhotosBean.class, "photoId=?", bean.getPhotoId());
                                    V_2ContractorDetailsAdapter.this.notifyDataSetChanged();
                                }
                            });
                        } else {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
                                            ToastUtil.showLong(mContext, "Token过期请重新登录！");
                                            SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                            ScreenManagerUtil.popAllActivityExceptOne();
                                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                            break;
                                        default:
                                            ToastUtil.showLong(mContext, msg);
                                            break;
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
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
