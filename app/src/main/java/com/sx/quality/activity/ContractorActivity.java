package com.sx.quality.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.adapter.ContractorTreeAdapter;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.NewContractorListBean;
import com.sx.quality.listener.ContractorListener;
import com.sx.quality.model.ContractorListModel;
import com.sx.quality.tree.Node;
import com.sx.quality.utils.Constants;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 承包商列表
 */
public class ContractorActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;

    @ViewInject(R.id.lvContractorList)
    private ListView lvContractorList;

    private Context mContext;
    private List<Node> allsCache;
    private List<Node> alls;
    private ContractorTreeAdapter ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(getResources().getDrawable(R.drawable.back_btn));
        txtTitle.setText(R.string.app_title);

        lvContractorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ContractorTreeAdapter) parent.getAdapter()).ExpandOrCollapse(position);
            }
        });

        String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.NODE_ID, "");
        if(JudgeNetworkIsAvailable.isNetworkAvailable(this) && !alreadyLoadNode.contains("&")){
            getData();
        }else{
            List<NewContractorListBean> listBean = DataSupport.where("pid = ?", "").find(NewContractorListBean.class);
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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "");
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.NEW_CONTRACTOR_LIST)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
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
            Gson gson = new Gson();
            String jsonData = response.body().string().toString();
            if (!JsonUtils.isGoodJson(jsonData)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, getString(R.string.json_error));
                    }
                });
            } else {
                final ContractorListModel model = gson.fromJson(jsonData, ContractorListModel.class);
                if (model.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 将数据存储到LitePal数据库（根据nodeId添加或更新）
                            List<NewContractorListBean> listBeen = model.getData();
                            for (NewContractorListBean bean : listBeen) {
                                bean.saveOrUpdate("nodeId=?", bean.getNodeId());
                            }

                            String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.NODE_ID, "");
                            String loadNodeId = alreadyLoadNode + "&,";
                            SpUtil.put(mContext, ConstantsUtil.NODE_ID, loadNodeId);

                            setContractorNode(model.getData());
                            LoadingUtils.hideLoading();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, getString(R.string.get_data_exception));
                        }
                    });
                }
            }
        }
    };

    /**
     *  设置节点,可以通过循环或递归方式添加节点
     */
    private void setContractorNode(List<NewContractorListBean> contractorBean) {
        try {
            int listSize = contractorBean.size();
            // 添加节点
            if (contractorBean != null && listSize > 0) {
                // 创建根节点
                Node root = new Node("山西东二环高速公路", "000000");
                root.setUser(false);

                for (int i = 0; i < listSize; i++) {
                    getNode(contractorBean.get(i), root);
                }

                ta = new ContractorTreeAdapter(this, root, listener);
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
            String nodeName = contractorListBean.getNodeTitle() + " (共" + contractorListBean.getProcessNum() + "道工序/已完成" + contractorListBean.getFinishedNum() + "道)";
            String nodeId = contractorListBean.getNodeId();
            String folderFlag = contractorListBean.getFolderFlag();
            // 创建子节点
            Node n = new Node(nodeName, "1");
            n.setParent(root);
            if (contractorListBean.getFolderFlag().equals("1")) {
                n.setUser(false);
            } else {
                n.setUser(true);
            }
            n.setUserId(nodeId);
            n.setRoleName(nodeName);
            n.setTel(folderFlag);
            n.setExpanded(false);
            n.setChecked(false); // 是否已加载
            n.setCheckBox(contractorListBean.getIsFinish().equals("1"));
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
        public void returnData(List<Node> allsCaches, List<Node> allss, int point, String userId) {
            allsCache = allsCaches;
            alls = allss;
            String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.NODE_ID, "");
            // 没有网络并且没有加载过
            if (JudgeNetworkIsAvailable.isNetworkAvailable(ContractorActivity.this) && !alreadyLoadNode.contains(userId)) {
                loadProcedureByNodeId(point, userId);
            } else {
                List<NewContractorListBean> listBean = DataSupport.where("pid = ?", userId).find(NewContractorListBean.class);
                setNodeInChildren(listBean, point);
            }
        }
    };

    /**
     * 请求数据
     * @param position
     */
    private void loadProcedureByNodeId(final int position, final String nodeId) {
        LoadingUtils.showLoading(mContext);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject obj = new JSONObject();
        try {
            obj.put("nodeId", nodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.NEW_CONTRACTOR_LIST)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
                Gson gson = new Gson();
                String jsonData = response.body().string().toString();
                if (!JsonUtils.isGoodJson(jsonData)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                } else {
                    final ContractorListModel model = gson.fromJson(jsonData, ContractorListModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 将数据存储到LitePal数据库（根据nodeId添加或更新）
                                List<NewContractorListBean> listBeen = model.getData();
                                for (NewContractorListBean bean : listBeen) {
                                    bean.saveOrUpdate("nodeId=?", bean.getNodeId());
                                }

                                String alreadyLoadNode = (String) SpUtil.get(mContext, ConstantsUtil.NODE_ID, "");
                                String loadNodeId = alreadyLoadNode + "" + nodeId + ",";
                                SpUtil.put(mContext, ConstantsUtil.NODE_ID, loadNodeId);

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
                                ToastUtil.showLong(mContext, mContext.getString(R.string.get_data_exception));
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 将数据插入到Node中
     * @param data
     * @param position
     */
    private void setNodeInChildren(List<NewContractorListBean> data, final int position) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            String nodeName = data.get(i).getNodeTitle() + " (共" + data.get(i).getProcessNum() + "道工序/已完成" + data.get(i).getFinishedNum() + "道)";
            String nodeId = data.get(i).getNodeId();
            String folderFlag = data.get(i).getFolderFlag();
            // 创建子节点
            Node n = new Node(nodeName, "1");
            n.setParent(alls.get(position));
            n.setUser(false);
            n.setUserId(nodeId);
            n.setRoleName(nodeName);
            n.setTel(folderFlag);
            n.setExpanded(false);
            n.setCheckBox(data.get(i).getIsFinish().equals("1"));
            n.setChecked(false); // 是否已加载
            nodes.add(n);
        }

        // 添加子节点到指定根节点下面
        alls.addAll(position + 1, nodes);
        // 需要放到此节点下
        Node node = alls.get(position);
        int point = allsCache.indexOf(node);
        allsCache.addAll(point + 1, nodes);

        alls.get(position).setChildren(nodes);
        alls.get(position).setChecked(true);
        allsCache.get(position).setChildren(nodes);
        allsCache.get(position).setChecked(true);

        ta.notifyDataSetChanged();
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View view){
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
