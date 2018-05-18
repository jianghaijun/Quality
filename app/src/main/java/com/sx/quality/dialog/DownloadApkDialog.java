package com.sx.quality.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.R;
import com.sx.quality.listener.DownloadProgressListener;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DownloadUtil;
import com.sx.quality.utils.ProviderUtil;
import com.sx.quality.utils.ToastUtil;

import java.io.File;

/**
 * 下载dialog
 */
public class DownloadApkDialog extends Dialog{
	private Context mContext;
	private Activity mActivity;
	private TextView txtResult;
	private ProgressBar progressBarDownload;
	private Long fileLength;

	public DownloadApkDialog(Context context, Long fileLength) {
		super(context);
		this.fileLength = fileLength;
		this.mActivity = (Activity) context;
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_donload_apk);

		progressBarDownload = (ProgressBar) this.findViewById(R.id.progressBarDownload);
		txtResult = (TextView) this.findViewById(R.id.txtResult);

		progressBarDownload.setMax(100);
		txtResult.setText("已下载" + 0 + "%");

		downloadApk();
	}

	/**
	 * 下载apk
	 */
	private void downloadApk() {
		DownloadUtil.getInstance().download(fileLength, ConstantsUtil.BASE_URL + ConstantsUtil.DOWNLOAD_APK, ConstantsUtil.SAVE_PATH, new DownloadUtil.OnDownloadListener() {
			@Override
			public void onDownloadSuccess(String path) {
				dismiss();
				//安装
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri;
				if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
					uri = Uri.fromFile(new File(ConstantsUtil.SAVE_PATH + "quality.apk"));
				} else {
					/**
					 * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
					 * 并且这样可以解决MIUI系统上拍照返回size为0的情况
					 */
					uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mActivity), new File(ConstantsUtil.SAVE_PATH + "quality.apk"));
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				}
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
                ConstantsUtil.isDownloadApk = false;
				mActivity.startActivity(intent);
			}

			@Override
			public void onDownloading(int progress) {
				progressBarDownload.setProgress(progress);
				txtResult.setText("已下载" + progress + "%");
			}

			@Override
			public void onDownloadFailed() {
                ConstantsUtil.isDownloadApk = false;
				ToastUtil.showShort(mContext, "文件下载失败！");
			}
		});
	}
}
