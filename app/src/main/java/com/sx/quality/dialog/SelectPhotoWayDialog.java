package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.listener.ChoiceListener;

/**
 * 相片选择Dialog
 * 
 * @author JiangHaiJun
 */
public class SelectPhotoWayDialog extends Dialog implements View.OnClickListener {
	private ChoiceListener selectListener;
	
	public SelectPhotoWayDialog(Context context, ChoiceListener selectListener) {
		super(context);
		this.selectListener = selectListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_select_photos_way);

        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        TextView txtAlbum = (TextView) findViewById(R.id.txtAlbum);
        TextView txtPhotograph = (TextView) findViewById(R.id.txtPhotograph);

		btnCancel.setOnClickListener(this);
		txtAlbum.setOnClickListener(this);
		txtPhotograph.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			dismiss();
			break;
		// 拍照
		case R.id.txtPhotograph:
			dismiss();
			selectListener.returnTrueOrFalse(true);
			break;
		// 相册
		case R.id.txtAlbum:
			dismiss();
			selectListener.returnTrueOrFalse(false);
			break;
		}
	}
}
