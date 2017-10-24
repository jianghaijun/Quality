package com.sx.quality.activity;

import android.animation.ObjectAnimator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class ShowPhotosActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    @ViewInject(R.id.viewPagerPhoto)
    private ViewPager viewPagerPhoto;
    @ViewInject(R.id.llIndicator)
    private LinearLayout llIndicator;

    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private RequestOptions options;
    private int pagerPosition;
    private List<String> imgUrlList;
    private List<ImageView> indicatorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        x.view().inject(this);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        imgUrlList = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);

        options = new RequestOptions()
                .placeholder(R.drawable.rotate_pro_loading)
                .error(R.drawable.error);

        PhotosAdapter adapter = new PhotosAdapter();
        viewPagerPhoto.setAdapter(adapter);
        viewPagerPhoto.setCurrentItem(pagerPosition);
        viewPagerPhoto.addOnPageChangeListener(this);

        if (imgUrlList.size() > 1) {
            addIndicator(imgUrlList);
        }
    }

    /**
     * 添加指示器
     */
    private void addIndicator(List<String> urls) {
        int paddingDp = DensityUtil.dip2px(4);
        int pointSize = DensityUtil.dip2px(20);
        for (int i = 0; i < urls.size(); i++) {
            ImageView child = new ImageView(this);
            child.setImageResource(R.drawable.point_grey);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointSize, pointSize);
            child.setLayoutParams(params);
            child.setPadding(paddingDp, 0, paddingDp, 0);
            indicatorList.add(child);
            llIndicator.addView(child);
        }
        setPointLight(pagerPosition);
    }

    /**
     * 设置默认圆点位置
     * @param position
     */
    private void setPointLight(int position) {
        if (indicatorList.size() < 2) {
            return;
        }

        for (ImageView imageView : indicatorList) {
            imageView.setImageResource(R.drawable.point_grey);
        }

        ImageView imageView = indicatorList.get(position);
        imageView.setImageResource(R.drawable.point_white);
    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < indicatorList.size(); i++) {
            if (i == selectItems) {
                indicatorList.get(i).setImageResource(R.drawable.point_white);
            } else {
                indicatorList.get(i).setImageResource(R.drawable.point_grey);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setImageBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 照片adapter
     */
    private class PhotosAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ShowPhotosActivity.this).inflate(R.layout.item_show_photos, null);
            PhotoView imgViewPhoto = (PhotoView) view.findViewById(R.id.imgViewPhoto);

            imgViewPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowPhotosActivity.this.finish();
                }
            });

            ObjectAnimator anim = ObjectAnimator.ofInt(imgViewPhoto, "ImageLevel", 0, 10000);
            anim.setDuration(800);
            anim.setRepeatCount(ObjectAnimator.INFINITE);
            anim.start();

            Glide.with(ShowPhotosActivity.this)
                    .load(imgUrlList.get(position))
                    .apply(options)
                    .thumbnail(0.1f)
                    .into(imgViewPhoto);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return imgUrlList == null ? 0 : imgUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
