package com.sx.quality.activity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 展示图片界面
 * Created by jack on 2017/10/10.
 */
public class ShowPhotoActivity extends BaseActivity {
    @ViewInject(R.id.imgViewPhoto)
    private PhotoView imgViewPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        x.view().inject(this);

        String url = getIntent().getStringExtra("photoUrl");
        Glide.with(this).load(url).into(imgViewPhoto);
    }

    @Event({R.id.imgViewPhoto})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgViewPhoto:
                this.finish();
                break;
        }
    }

}
