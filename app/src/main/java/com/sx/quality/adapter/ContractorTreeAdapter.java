package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.ContractorDetailsActivity;
import com.sx.quality.activity.R;
import com.sx.quality.bean.NewContractorListBean;
import com.sx.quality.listener.ContractorListener;
import com.sx.quality.model.ContractorListModel;
import com.sx.quality.tree.Node;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
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
 * 树数据源构造器
 * Created by jack on 2017/10/10.
 */
public class ContractorTreeAdapter extends BaseAdapter {
    private LayoutInflater lif;
    private List<Node> allsCache = new ArrayList<>();
    public List<Node> alls = new ArrayList<>();
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Activity mContext;

    private Node rootNode;
    private List<String> nodeName = new ArrayList<>();
    private ContractorListener listener;

    /**
     * @param mContext
     * @param rootNode
     */
    public ContractorTreeAdapter(Activity mContext, Node rootNode, ContractorListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addNode(rootNode);
    }

    /**
     * 添加节点
     *
     * @param node
     */
    private void addNode(Node node) {
        if (node.getParent() != null) {
            alls.add(node);
            allsCache.add(node);
        }
        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(node.getChildren().get(i));
        }
    }

    /**
     * 控制节点的展开和折叠
     */
    private void filterNode() {
        alls.clear();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (!n.isParentCollapsed() || n.isRoot()) {
                alls.add(n);
            }
        }
    }

    /**
     * 设置展开和折叠状态图标
     *
     * @param expandedIcon  展开时图标
     * @param collapsedIcon 折叠时图标
     */
    public void setExpandedCollapsedIcon(int expandedIcon, int collapsedIcon) {
        this.expandedIcon = expandedIcon;
        this.collapsedIcon = collapsedIcon;
    }

    /**
     * 设置展开级别
     *
     * @param level
     */
    public void setExpandLevel(int level) {
        alls.clear();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (n.getLevel() <= level) {
                // 上层都设置展开状态
                if (n.getLevel() < level) {
                    n.setExpanded(true);
                    // 最后一层都设置折叠状态
                } else {
                    n.setExpanded(false);
                }
                alls.add(n);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 获取节点的根节点
     * @param node
     * @return
     */
    private void getNodeRootNode (Node node) {
        rootNode = node.getParent();
        if (rootNode == null) {
            return;
        }

        if (rootNode.isRoot()) {
            rootNode = node;
        } else {
            nodeName.add(rootNode.getRoleName());
            getNodeRootNode(rootNode);
        }
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(final int position) {
        Node n = alls.get(position);
        if (n != null) {
            // 是否是文件夹（文件夹继续展开---工序进入上传照片界面）
            if (n.getTel().equals("1")) {
                // 是否处于展开状态
                if (n.isExpanded()) {
                    // 如果已经加载 展开已加载向
                    n.setExpanded(!n.isExpanded());
                    filterNode();
                    this.notifyDataSetChanged();
                } else {
                    if (n.isChecked()) {
                        // 如果已经加载 展开已加载向
                        n.setExpanded(!n.isExpanded());
                        filterNode();
                        this.notifyDataSetChanged();
                    } else {
                        // 加载该节点下的工序
                        // 设置根节点的展开状态
                        n.setExpanded(true);
                        listener.returnData(allsCache, alls, position, alls.get(position).getUserId());
                    }
                }
            } else {
                nodeName.clear();
                nodeName.add(n.getRoleName());
                getNodeRootNode(n);
                StringBuffer sb = new StringBuffer();
                int len = nodeName.size() - 1;
                for (int i = len; i >= 0; i--) {
                    String name = nodeName.get(i);
                    if (name.contains("(")) {
                        name = name.substring(0, name.indexOf("("));
                    }
                    if (i != 0) {
                        sb.append(name.trim() + "→");
                    } else {
                        sb.append(name.trim());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("rootNodeName", sb.toString());
                intent.putExtra("nodeId", n.getUserId());
                intent.setClass(mContext, ContractorDetailsActivity.class);
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public int getCount() {
        return alls.size();
    }

    @Override
    public Object getItem(int position) {
        return alls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = this.lif.inflate(R.layout.item_contractor, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // 得到当前节点
        Node n = alls.get(position);

        if (n != null) {
            // 显示文本
            String roleName = n.getRoleName();
            // 去掉节点上该节点下有多少工序，是否已完成
            if (!n.getTel().equals("1")) {
                if (n.hasCheckBox()) {
                    roleName = roleName.substring(0, roleName.indexOf("(")) + "(已完成)";
                } else {
                    roleName = roleName.substring(0, roleName.indexOf("(")) + "(未完成)";
                }
            }

            holder.txtTitle.setText(roleName == null || "null".equals(roleName) ? "" : roleName);
            if (!n.getTel().equals("1")) {
                // 是叶节点 不显示展开和折叠状态图标
                holder.imgViewState.setVisibility(View.GONE);
                holder.imgViewNode.setVisibility(View.VISIBLE);
            } else {
                holder.imgViewState.setVisibility(View.VISIBLE);
                holder.imgViewNode.setVisibility(View.GONE);
            }

            /* 单击时控制子节点展开和折叠,状态图标改变  */
            if (n.isExpanded()) {
                if (expandedIcon != -1) {
                    holder.imgViewState.setImageResource(expandedIcon);
                }
            } else {
                if (collapsedIcon != -1) {
                    holder.imgViewState.setImageResource(collapsedIcon);
                }
            }

            // 控制缩进
            if (n.getLevel() != 1) {
                holder.rlItemTree.setPadding(50 * (n.getLevel() - 1), 3, 3, 3);
            } else {
                holder.rlItemTree.setPadding(0 * (n.getLevel() - 1), 3, 3, 3);
            }
        }
        return view;
    }

    /**
     * 列表项控件集合
     */
    private class ViewHolder {
        // 展开收缩图标
        @ViewInject(R.id.imgViewState)
        private ImageView imgViewState;
        @ViewInject(R.id.imgViewNode)
        private ImageView imgViewNode;

        // 标题
        @ViewInject(R.id.txtTitle)
        private TextView txtTitle;
        @ViewInject(R.id.rlItemTree)
        private RelativeLayout rlItemTree;
    }

}