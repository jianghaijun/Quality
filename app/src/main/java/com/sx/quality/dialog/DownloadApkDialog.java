package com.sx.quality.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.listener.DownloadProgressListener;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DownloadFileTaskUtil;
import com.sx.quality.utils.ToastUtil;

import java.io.File;

/**
 * 下载dialog
 */
public class DownloadApkDialog extends Dialog{
	private Activity mContext;
	private TextView txtResult;
	private ProgressBar progressBarDownload;
	private Handler downloadHandler;
	private Long fileLength;

	public DownloadApkDialog(Context context, Long fileLength) {
		super(context);
		this.mContext = (Activity) context;
		this.fileLength = fileLength;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_donload_apk);

		progressBarDownload = (ProgressBar) this.findViewById(R.id.progressBarDownload);
		txtResult = (TextView) this.findViewById(R.id.txtResult);

		progressBarDownload.setMax(Integer.parseInt(String.valueOf(fileLength)));
		txtResult.setText(0 + "%");

		downloadHandler = new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case 100:
						txtResult.setText("已下载：" + msg.what + "%");
						dismiss();
						//安装
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(new File(ConstantsUtil.SAVE_PATH + "sx_quality.apk")), "application/vnd.android.package-archive");
						mContext.startActivity(intent);
						break;
					default:
						txtResult.setText("已下载：" + msg.what + "%");
						break;
				}
			}
		};

		downloadApk();
	}

	/**
	 * 下载apk
	 */
	private void downloadApk() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DownloadFileTaskUtil.downloadFileDoGet(ConstantsUtil.BASE_URL + ConstantsUtil.DOWNLOAD_APK, ConstantsUtil.SAVE_PATH + "sx_quality.apk", listener);
				} catch (Exception e) {
					e.printStackTrace();
					dismiss();
					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ToastUtil.showShort(mContext, "文件下载失败！");
						}
					});
				}
			}
		}).start();
	}

	/**
	 * APK下载监听
	 */
	private DownloadProgressListener listener = new DownloadProgressListener() {
		@Override
		public void downloadSize(Long progressSize) {
			progressBarDownload.setProgress(Integer.parseInt(String.valueOf(progressSize)));
			float result = (float) progressBarDownload.getProgress() / (float) progressBarDownload.getMax();
			int p = (int) (result * 100);
			Message message = new Message();
			message.what = p;
			downloadHandler.sendMessage(message);
		}
	};
}
