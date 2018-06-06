package com.sx.quality.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sx.quality.adapter.BaseAdapter;
import com.sx.quality.adapter.CommonWorkingProcedureAdapter;
import com.sx.quality.adapter.ILoadCallback;
import com.sx.quality.adapter.LoadMoreAdapterWrapper;
import com.sx.quality.adapter.OnLoad;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.utils.ScreenManagerUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用工序
 */
public class CommonWorkingProcedureActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.rvCommonWorkingProcedure)
    private RecyclerView rvCommonWorkingProcedure;

    private CommonWorkingProcedureAdapter mAdapter;
    private Activity mContext;
    private BaseAdapter baseAdapter;

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_working_procedure);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        mContext = this;
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("常用工序");

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 创建被装饰者类实例
        mAdapter = new CommonWorkingProcedureAdapter(mContext);
        mAdapter.updateData();
        // 创建装饰者实例，并传入被装饰者和回调接口
        baseAdapter = new LoadMoreAdapterWrapper(mAdapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                //boolean isHave = pagePosition != 1 && (pagePosition-1) * pageSize > sum;
                //getData(pagePosition, pageSize, callback, isHave);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<WorkingBean> dataSet = new ArrayList();
                        for (int i = 0; i < 10; i++) {
                            WorkingBean bean = new WorkingBean();
                            bean.setProcessName("工序" + (num * 10 + i));
                            dataSet.add(bean);
                        }
                        // 数据的处理最终还是交给被装饰的adapter来处理
                        mAdapter.appendData(dataSet);
                        callback.onSuccess();
                        // 模拟加载到没有更多数据的情况，触发onFailure
                        if (num++ == 3) {
                            callback.onFailure();
                        }
                    }
                }, 2000);
            }
        });
        rvCommonWorkingProcedure.setAdapter(baseAdapter);
        rvCommonWorkingProcedure.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
