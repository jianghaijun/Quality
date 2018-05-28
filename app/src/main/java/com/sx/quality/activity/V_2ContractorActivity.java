package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.V_2ContractorTreeAdapter;
import com.sx.quality.bean.NewContractorListBean;
import com.sx.quality.listener.ContractorListener;
import com.sx.quality.model.ContractorListModel;
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
import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 工序树形图
 */
public class V_2ContractorActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.btnRight)
    private Button btnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;

    @ViewInject(R.id.lvContractorList)
    private ListView lvContractorList;

    private Context mContext;
    private List<Node> allCache;
    private List<Node> all;
    private V_2ContractorTreeAdapter ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_2activity_contractor);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(getResources().getDrawable(R.drawable.back_btn));
        txtTitle.setText(R.string.app_title);

        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("");

        lvContractorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((V_2ContractorTreeAdapter) parent.getAdapter()).CheckIsHave(position);
            }
        });

        //String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.LEVEL_ID, "");
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)/* && !alreadyLoadNode.contains("&")*/) {
            getData();
        } else {
            List<NewContractorListBean> listBean = DataSupport.where("parentId = ? and levelType = ?", "", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""))).find(NewContractorListBean.class);
            setContractorNode(listBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ta != null) {
            ta.notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("parentId", "");
            obj.put("levelType", SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.NEW_CONTRACTOR_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
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
            LoadingUtils.hideLoading();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLong(mContext, getString(R.string.server_exception));
                }
            });
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
                        final ContractorListModel model = gson.fromJson(jsonData, ContractorListModel.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 将数据存储到LitePal数据库（根据LevelId添加或更新）
                                List<NewContractorListBean> listBeen = model.getData();
                                for (NewContractorListBean bean : listBeen) {
                                    bean.setLevelType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
                                    bean.saveOrUpdate("levelId=?", bean.getLevelId());
                                }
                                // 添加已加载的LevelId
                                //SpUtil.put(mContext, ConstantsUtil.LEVEL_ID, SpUtil.get(mContext, ConstantsUtil.LEVEL_ID, "") + "&");
                                // 设置节点
                                setContractorNode(model.getData());
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                switch (code) {
                                    case "3003":
                                    case "3004":
                                        // Token异常重新登录
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
                    LoadingUtils.hideLoading();
                    ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, getString(R.string.json_error));
                    }
                });
            }
        }
    };

    /**
     * 设置节点,可以通过循环或递归方式添加节点
     * @param contractorBean
     */
    private void setContractorNode(List<NewContractorListBean> contractorBean) {
        try {
            // 添加节点
            if (contractorBean != null && contractorBean.size() > 0) {
                int listSize = contractorBean.size();
                // 创建根节点
                Node root = new Node();
                root.setFolderFlag("1");

                for (int i = 0; i < listSize; i++) {
                    getNode(contractorBean.get(i), root);
                }

                ta = new V_2ContractorTreeAdapter(this, root, listener, btnRight);
                /* 设置展开和折叠时图标 */
                ta.setExpandedCollapsedIcon(R.drawable.reduce, R.drawable.plus);
				/* 设置默认展开级别 */
                ta.setExpandLevel(1);
                lvContractorList.setAdapter(ta);
                SetListHeight.setListViewHeight(lvContractorList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 子节点
     * @param contractorListBean
     * @param root
     * @return
     */
    private Node getNode(NewContractorListBean contractorListBean, Node root) {
        try {
            String levelId = contractorListBean.getLevelId();
            String type = (String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "");
            String levelName;
            if (type.equals("1")) {
                levelName = contractorListBean.getLevelName() + " (共" + contractorListBean.getProcessNum() + "道隐患/已完成" + contractorListBean.getFinishedNum() + "道)";
            } else {
                levelName = contractorListBean.getLevelName() + " (共" + contractorListBean.getProcessNum() + "道工序/已完成" + contractorListBean.getFinishedNum() + "道)";
            }
            // 创建子节点
            Node n = new Node();
            n.setParent(root);
            n.setLevelId(levelId);
            n.setLevelName(levelName);
            n.setParentId(contractorListBean.getParentId());
            n.setFolderFlag(contractorListBean.getFolderFlag());
            n.setExpanded(false);
            n.setLoading(false);
            n.setCanClick(contractorListBean.getHaveProcess().equals("1"));
            n.setIsFinish(contractorListBean.getIsFinish());
            root.add(n);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否已加载监听
     */
    private ContractorListener listener = new ContractorListener() {
        @Override
        public void returnData(List<Node> allCaches, List<Node> allNode, int point, String levelId) {
            allCache = allCaches;
            all = allNode;
            //String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.LEVEL_ID, "");
            // 没有网络并且没有加载过
            if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorActivity.this)/* && !alreadyLoadNode.contains("," + levelId + ",")*/) {
                loadProcedureByNodeId(point, levelId);
            } else {
                List<NewContractorListBean> listBean = DataSupport.where("parentId = ?", levelId).find(NewContractorListBean.class);
                setNodeInChildren(listBean, point);
            }
        }
    };

    /**
     * 加载层级下的节点
     * @param position
     * @param parentId
     */
    private void loadProcedureByNodeId(final int position, final String parentId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("parentId", parentId);
            obj.put("levelType", SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.NEW_CONTRACTOR_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showLong(mContext, getString(R.string.server_exception));
                    }
                });
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
                            final ContractorListModel model = gson.fromJson(jsonData, ContractorListModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 将数据存储到LitePal数据库（根据nodeId添加或更新）
                                    List<NewContractorListBean> listBeen = model.getData();
                                    for (NewContractorListBean bean : listBeen) {
                                        bean.setLevelType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
                                        bean.saveOrUpdate("levelId=?", bean.getLevelId());
                                    }

                                    SpUtil.put(mContext, ConstantsUtil.LEVEL_ID, SpUtil.get(mContext, ConstantsUtil.LEVEL_ID, "") + "" + parentId + ",");

                                    // 将数据添加到Node的子节点中
                                    setNodeInChildren(model.getData(), position);
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
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
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 将数据插入到Node中
     *
     * @param data
     * @param position
     */
    private void setNodeInChildren(List<NewContractorListBean> data, final int position) {
        List<Node> nodes = new ArrayList<>();
        for (NewContractorListBean contractor : data) {
            String levelId = contractor.getLevelId();
            String type = (String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "");
            String levelName;
            if (type.equals("1")) {
                levelName = contractor.getLevelName() + " (共" + contractor.getProcessNum() + "道隐患/已完成" + contractor.getFinishedNum() + "道)";
            } else {
                levelName = contractor.getLevelName() + " (共" + contractor.getProcessNum() + "道工序/已完成" + contractor.getFinishedNum() + "道)";
            }
            // 创建子节点
            Node n = new Node();
            n.setParent(all.get(position));
            n.setLevelId(levelId);
            n.setLevelName(levelName);
            n.setParentId(contractor.getParentId());
            n.setFolderFlag(contractor.getFolderFlag());
            n.setExpanded(false);
            n.setLoading(false);
            n.setCanClick(contractor.getHaveProcess().equals("1"));
            n.setIsFinish(contractor.getIsFinish());
            nodes.add(n);
        }

        // 添加子节点到指定根节点下面
        all.addAll(position + 1, nodes);
        // 需要放到此节点下
        Node node = all.get(position);
        int point = allCache.indexOf(node);
        allCache.addAll(point + 1, nodes);

        all.get(position).setChildren(nodes);
        all.get(position).setLoading(true);
        allCache.get(position).setChildren(nodes);
        allCache.get(position).setLoading(true);

        ta.notifyDataSetChanged();
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
