package com.sx.quality.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    @ViewInject(R.id.imgViewThumbPhoto)
    private ImageView imgViewThumbPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        x.view().inject(this);

        String thumbUrl = getIntent().getStringExtra("thumbUrl");
        Glide.with(this).load(thumbUrl).into(imgViewThumbPhoto);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);

        ObjectAnimator anim = ObjectAnimator.ofInt(imgViewPhoto, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        String photoUrl = getIntent().getStringExtra("photoUrl");
        Glide.with(this).load(photoUrl).apply(options).into(imgViewPhoto);
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
