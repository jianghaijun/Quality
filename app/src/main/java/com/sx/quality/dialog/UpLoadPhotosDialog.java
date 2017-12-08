package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.R;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.model.LoginModel;
import com.sx.quality.utils.ConstantsUtil;
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
						// 移除已经上传的照片
						DataSupport.deleteAll(ContractorListPhotosBean.class, "pictureAddress=? AND userId = ?", upLoadPhotosBeenList.get(upLoadNum).getPictureAddress(), (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
						if (upLoadNum == upLoadPhotosBeenList.size() - 1) {
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
		OkHttpUtils.post()
				.addFile("filesName", upLoadPhotosBeenList.get(upLoadNum).getPictureName(), new File(upLoadPhotosBeenList.get(upLoadNum).getPictureAddress()))
				.addParams("nodeId", upLoadPhotosBeenList.get(upLoadNum).getNodeId())
				.addParams("pictureDesc", upLoadPhotosBeenList.get(upLoadNum).getPictureDesc())
				.addParams("pictureName", upLoadPhotosBeenList.get(upLoadNum).getPictureName())
				.addParams("pictureType", upLoadPhotosBeenList.get(upLoadNum).getPictureType())
				.addParams("userId", (String) SpUtil.get(mContext,ConstantsUtil.USER_ID, ""))
				.url(ConstantsUtil.BASE_URL + ConstantsUtil.UP_LOAD_PHOTOS)
				.build()
				.execute(new com.zhy.http.okhttp.callback.Callback() {
					@Override
					public Object parseNetworkResponse(Response response, int id) throws Exception {
						Gson gson = new Gson();
						String jsonData = response.body().string().toString();
						LoginModel loginModel = gson.fromJson(jsonData, LoginModel.class);
						upLoadPhotosBeenList.get(upLoadNum).setPictureId(loginModel.getPictureId());
						upLoadNum++;
						if (upLoadNum < upLoadPhotosBeenList.size()) {
							UpLoadPhotos();
						}
						return null;
					}

					@Override
					public void onError(Call call, Exception e, int id) {
						new Thread(new Runnable() {
							@Override
							public void run() {
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
