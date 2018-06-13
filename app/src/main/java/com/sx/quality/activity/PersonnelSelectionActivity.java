package com.sx.quality.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.PersonnelTreeAdapter;
import com.sx.quality.bean.PersonnelBean;
import com.sx.quality.model.PersonnelListModel;
import com.sx.quality.tree.Node;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SetListHeight;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonnelSelectionActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    // 工序人员List
    @ViewInject(R.id.lvContractorList)
    private ListView lvPersonnelList;
    // 适配器
    private PersonnelTreeAdapter treeAdapter;
    private Activity mContext;
    private Node choiceNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_personnel);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_btn));
        txtTitle.setText("人员选择");

        lvPersonnelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                treeAdapter.ExpandOrCollapse(position);
            }
        });

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            ToastUtil.showShort(mContext, getString(R.string.not_network));
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, "");
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PERSONNEL_LIST)
                .addHeader("token", "b7055c4fe607ed7a0f18657f5dd1c6c16c06ebf2916e2317ce8377d0b9997ead66cd18d64da1d14224b6df3590b79b9e0b1c7aed240dfc23fd4f5e6a08c04fa665d908c4eea8f80a07c84e9be5682c9fe8364fed7826b064ee590527e2a8dadcac2bfa83d1058f217a8b2239721edbad01c3c688ebf77ea619249bfbf3f2b6364ac108f0cfe4feee7f28a5bc22aaead9")
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            uiThread(true, getString(R.string.server_exception));
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
                        Gson gson = new Gson();
                        final PersonnelListModel model = gson.fromJson(jsonData, PersonnelListModel.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 设置节点
                                setPersonnelNode(model.getData());
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        LoadingUtils.hideLoading();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (code) {
                                    case "3003":
                                    case "3004":
                                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
                                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                        ScreenManagerUtil.popAllActivityExceptOne();
                                        startActivity(new Intent(mContext, LoginActivity.class));
                                        break;
                                    default:
                                        ToastUtil.showLong(mContext, msg);
                                        break;
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    uiThread(true, getString(R.string.data_error));
                    e.printStackTrace();
                }
            } else {
                uiThread(true, getString(R.string.json_error));
            }
        }
    };

    /**
     * 设置节点,可以通过循环或递归方式添加节点
     *
     * @param personnelNode
     */
    private void setPersonnelNode(PersonnelBean personnelNode) {
        // 添加节点
        if (personnelNode != null) {
            // 创建根节点
            Node root = new Node();
            root.setFolderFlag("1");

            personnelNode(personnelNode, root);

            treeAdapter = new PersonnelTreeAdapter(this, root);
            /*设置展开和折叠时图标*/
            treeAdapter.setExpandedCollapsedIcon(R.drawable.open, R.drawable.fold);
            /*设置默认展开级别*/
            treeAdapter.setExpandLevel(1);
            lvPersonnelList.setAdapter(treeAdapter);
            if (choiceNode != null) {
                treeAdapter.expandParentNode(choiceNode);
                treeAdapter.notifyDataSetChanged();
            }
            //SetListHeight.setListViewHeight(lvPersonnelList);
        }
    }

    /**
     * 子节点
     *
     * @param personnelNode
     * @param root
     */
    private void personnelNode(PersonnelBean personnelNode, Node root) {
        // 创建子节点
        Node n = new Node();
        n.setParent(root);
        n.setLevelId(personnelNode.getValue());
        n.setLevelName(personnelNode.getLabel());
        n.setParentId(personnelNode.getValuePid());
        n.setFolderFlag(personnelNode.getType());
        String selectUserId = (String) SpUtil.get(mContext, ConstantsUtil.SELECT_USER_ID, "");
        n.setExpanded(false);
        n.setCanClick(selectUserId.equals(personnelNode.getValue()));
        if (selectUserId.equals(personnelNode.getValue())) {
            choiceNode = n;
        }
        root.add(n);

        if (personnelNode.getChildren() != null && personnelNode.getChildren().size() > 0) {
            for (PersonnelBean personnel : personnelNode.getChildren()) {
                personnelNode(personnel, n);
            }
        }
    }

    /**
     * 子线程运行
     *
     * @param isDismiss 是否隐藏加载动画
     * @param msg       提示信息
     */
    private void uiThread(boolean isDismiss, final String msg) {
        if (isDismiss) {
            LoadingUtils.hideLoading();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View view) {
        switch (view.getId()) {
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
