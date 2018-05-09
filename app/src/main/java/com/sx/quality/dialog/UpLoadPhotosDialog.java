package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.R;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.model.LoginModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 照片上传Dialog
 * @author JiangHaiJun
 * @date 2017-9-15
 */
public class UpLoadPhotosDialog extends Dialog{
	private List<ContractorListPhotosBean> upLoadPhotosBeenList;
	private Context mContext;
	private Handler upLoadPhotosHandler;
	private TextView txtNum;
	private ProgressBar proBarUpLoadPhotos;
	private ChoiceListener choiceListener;

	private int upLoadNum = 0;

	public UpLoadPhotosDialog(Context context, List<ContractorListPhotosBean> upLoadPhotosBeenList, ChoiceListener choiceListener) {
		super(context);
		this.mContext = context;
		this.choiceListener = choiceListener;
		this.upLoadPhotosBeenList = upLoadPhotosBeenList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_up_load_photos);

		proBarUpLoadPhotos = (ProgressBar) this.findViewById(R.id.proBarUpLoadPhotos);
		txtNum = (TextView) this.findViewById(R.id.txtNum);

		upLoadPhotosHandler = new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case 100:
						txtNum.setText("已上传：" + msg.what + "%  (" + (upLoadNum+1) + "/" + upLoadPhotosBeenList.size() + ")");
						if (upLoadNum == upLoadPhotosBeenList.size() - 1) {
							// 移除已经上传的照片
							DataSupport.deleteAll(ContractorListPhotosBean.class, "photoAddress=? AND userId = ?", upLoadPhotosBeenList.get(upLoadNum).getPhotoAddress(), (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
							UpLoadPhotosDialog.this.dismiss();
							choiceListener.returnTrueOrFalse(true);
							ToastUtil.showShort(mContext, "文件上传成功！");
						}
						break;
					case -1:
						UpLoadPhotosDialog.this.dismiss();
						choiceListener.returnTrueOrFalse(true);
						ToastUtil.showShort(mContext, "文件上传失败！");
						break;
					case -2:
						UpLoadPhotosDialog.this.dismiss();
						ToastUtil.showShort(mContext, mContext.getString(R.string.json_error));
						break;
					case -3:
						UpLoadPhotosDialog.this.dismiss();
						String val = msg.getData().getString("key");
						ToastUtil.showShort(mContext, val);
						/*// Token异常重新登录
						ToastUtil.showLong(mContext, "Token过期请重新登录！");
						SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
						ScreenManagerUtil.popAllActivityExceptOne();
						mContext.startActivity(new Intent(mContext, LoginActivity.class));*/
						break;
					default:
						txtNum.setText("已上传：" + msg.what + "%  (" + (upLoadNum+1) + "/" + upLoadPhotosBeenList.size() + ")");
						break;
				}
			}
		};

		if (null != upLoadPhotosBeenList && upLoadPhotosBeenList.size() > 0) {
			proBarUpLoadPhotos.setMax(upLoadPhotosBeenList.size() * 100000000);
			UpLoadPhotos();
		}
	}

	/**
	 * 上传文件
	 */
	private void UpLoadPhotos() {
		final ContractorListPhotosBean bean = upLoadPhotosBeenList.get(upLoadNum);
		OkHttpUtils.post()
				.addFile("filesName", bean.getPhotoName(), new File(bean.getPhotoAddress()))
				.addParams("processId", bean.getProcessId())
				.addParams("photoDesc", bean.getPhotoDesc())
				.addParams("longitude", bean.getLongitude())
				.addParams("latitude", bean.getLatitude())
				.addParams("location", bean.getLocation() == null ? "" : bean.getLocation())
				.addParams("photoName", bean.getPhotoName())
				.addParams("photoType", bean.getPhotoType())
				.addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
				.addParams("userId", (String) SpUtil.get(mContext,ConstantsUtil.USER_ID, ""))
				.url(ConstantsUtil.BASE_URL + ConstantsUtil.UP_LOAD_PHOTOS)
				.build()
				.execute(new com.zhy.http.okhttp.callback.Callback() {
					@Override
					public Object parseNetworkResponse(Response response, int id) throws Exception {
						String jsonData = response.body().string().toString();
						if (JsonUtils.isGoodJson(jsonData)) {
							Gson gson = new Gson();
							LoginModel loginModel = gson.fromJson(jsonData, LoginModel.class);
							if (loginModel.isSuccess()) {
								bean.setPhotoId(loginModel.getPictureId());
								// 移除已经上传的照片
								DataSupport.deleteAll(ContractorListPhotosBean.class, "photoAddress=? AND userId = ?", bean.getPhotoAddress(), (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
								upLoadNum++;
								if (upLoadNum < upLoadPhotosBeenList.size()) {
									UpLoadPhotos();
								}
							} else {
								Message jsonErr = new Message();
								jsonErr.what = -3;
								Bundle bundle = new Bundle();
								bundle.putString("key", loginModel.getMessage());
								jsonErr.setData(bundle);
								upLoadPhotosHandler.sendMessage(jsonErr);
							}
						} else {
							Message jsonErr = new Message();
							jsonErr.what = -2;
							upLoadPhotosHandler.sendMessage(jsonErr);
						}
						return null;
					}

					@Override
					public void onError(final Call call,final Exception e, final int id) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								choiceListener.returnTrueOrFalse(false);
								UpLoadPhotosDialog.this.dismiss();
							}
						}).start();
					}

					@Override
					public void onResponse(Object response, int id) {
					}

					@Override
					public void inProgress(float progress, long total, int id) {
						super.inProgress(progress, total, id);
						proBarUpLoadPhotos.setProgress((int) (progress*100000000) + upLoadNum*100000000);
						float result = (float) (proBarUpLoadPhotos.getProgress() + upLoadNum) / (float) proBarUpLoadPhotos.getMax();
						int p = (int) (result * 100);
						Message message = new Message();
						message.what = p;
						upLoadPhotosHandler.sendMessage(message);
					}
				});
	}
}
