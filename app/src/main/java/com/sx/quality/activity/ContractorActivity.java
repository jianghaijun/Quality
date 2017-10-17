package com.sx.quality.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.ContractorTreeAdapter;
import com.sx.quality.bean.ContractorListBean;
import com.sx.quality.model.ContractorListModel;
import com.sx.quality.tree.Node;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.SetListHeight;
import com.sx.quality.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

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
    @ViewInject(R.id.txtRight)
    private Button txtRight;

    @ViewInject(R.id.lvContractorList)
    private ListView lvContractorList;

    private Context mContext;

    private Node root;
    private ContractorTreeAdapter ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor);

        mContext = this;
        x.view().inject(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(getResources().getDrawable(R.drawable.back_btn));
        txtTitle.setText(R.string.app_title);
        /*txtRight.setVisibility(View.VISIBLE);
        txtRight.setText(R.string.level_one);*/

        lvContractorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这句话写在最后面
                ((ContractorTreeAdapter) parent.getAdapter()).ExpandOrCollapse(position);
            }
        });

        if(JudgeNetworkIsAvailable.isNetworkAvailable(this)){
            getData();
        }else{
            ToastUtil.showLong(this, getString(R.string.not_network));
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, "");
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.CONTRACTOR_LIST)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
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
            jsonData = null == jsonData || jsonData.equals("null") || jsonData.equals("") ? "{}" : jsonData;
            final ContractorListModel model = gson.fromJson(jsonData, ContractorListModel.class);

            if (model.isSuccess()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
    };

    /**
     *  设置节点,可以通过循环或递归方式添加节点
     */
    private void setContractorNode(List<ContractorListBean> contractorBean) {
        try {
            int listSize = contractorBean.size();
            if (contractorBean != null && listSize > 0) {
                // 创建根节点
                root = new Node("山西东二环高速公路", "000000");
                root.setUser(false);

                for (int i = 0; i < listSize; i++) {
                    getNode(contractorBean.get(i), root);
                }

                ta = new ContractorTreeAdapter(this, root);
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
    private Node getNode(ContractorListBean contractorListBean, Node root) {
        try {
            String nodeName = contractorListBean.getNodeName();
            String nodeId = contractorListBean.getNodeId();
            // 创建子节点
            Node n = new Node(nodeName, "1");
            n.setParent(root);
            int listBeanSize = contractorListBean.getSxZlNodeList().size();
            if(listBeanSize > 0){
                if(listBeanSize > 0){
                    n.setUser(false);
                }else{
                    n.setUser(true);
                }
            }else{
                n.setUser(true);
            }
            n.setUserId(nodeId);
            n.setRoleName(nodeName);
            n.setExpanded(false);

            root.add(n);

            if(listBeanSize > 0){
                for (int i = 0; i < listBeanSize; i++) {
                    getNode(contractorListBean.getSxZlNodeList().get(i), n);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View view){
        switch (view.getId()) {
            // 返回前一页
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }
}
