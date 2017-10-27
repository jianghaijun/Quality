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

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.ContractorDetailsActivity;
import com.sx.quality.activity.R;
import com.sx.quality.tree.Node;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 树数据源构造器
 * Created by jack on 2017/10/10.
 */
public class ContractorTreeAdapter extends BaseAdapter {
    private LayoutInflater lif;
    private List<Node> allsCache = new ArrayList<Node>();
    public List<Node> alls = new ArrayList<Node>();
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Activity mContext;

    private List<Node> listNode = new ArrayList<>();
    private Node parentNode, rootNode;
    private List<String> nodeName = new ArrayList<>();

    /**
     * @param mContext
     * @param rootNode
     */
    public ContractorTreeAdapter(Activity mContext, Node rootNode) {
        this.mContext = mContext;
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
     * 获得选中节点
     *
     * @return
     */
    public List<Node> getSeletedNodes() {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            if (n.isChecked()) {
                nodes.add(n);
            }
        }
        return nodes;
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
     * 展开节点下所有子节点
     *
     * @param node
     */
    private void showNodeUnderAllNodes(Node node) {
        listNode.clear();
        listNode.add(node);
        getNodeUnderAllNodes(node);
        alls.clear();
        for (int i = 0; i < allsCache.size(); i++) {
            Node n = allsCache.get(i);
            for (int j = 0; j < listNode.size(); j++) {
                if (n.getUserId().equals(listNode.get(j).getUserId())) {
                    n.setExpanded(true);
                }
            }

            if (!n.isParentCollapsed() || n.isRoot()) {
                alls.add(n);
            }
        }
    }

    /**
     * 获取节点下所有子节点
     *
     * @param node
     * @return
     */
    private void getNodeUnderAllNodes(Node node) {
        if (!node.isLeaf()) {
            List<Node> nodeList = node.getChildren();
            if (nodeList.size() != 0) {
                for (Node n : nodeList) {
                    listNode.add(n);
                    getNodeUnderAllNodes(n);
                }
            }
        }
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
     * 获取节点的父节点
     * @param node
     * @return
     */
    private void getNodeParentNode (Node node) {
        parentNode = node.getParent();
        if (rootNode == null || parentNode == null || parentNode.isRoot()) {
            return;
        }

        if (parentNode.getUserId().equals(rootNode.getUserId())) {
            parentNode = node;
        } else {
            getNodeParentNode(parentNode);
        }
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(int position) {
        Node n = alls.get(position);
        if (n != null) {
            /* 是否叶节点,即没有子节点的节点 */
            if (!n.isLeaf()) {
                // 如果展开状态则折叠---如果折叠状态则展开全部子节点
                if (n.isExpanded()) {
                    n.setExpanded(!n.isExpanded());
                    filterNode();
                } else {
                    showNodeUnderAllNodes(n);
                }
                this.notifyDataSetChanged();
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
                        sb.append(name + "→");
                    } else {
                        sb.append(name);
                    }
                }

                //getNodeParentNode(n);

                Intent intent = new Intent();
                intent.putExtra("rootNodeName", sb.toString());
                /*String parentNodeId = parentNode.getUserId() == null ? "" : parentNode.getUserId();
                if (parentNodeId.equals(n.getUserId())) {
                    intent.putExtra("parentNodeName", "");
                } else {
                    intent.putExtra("parentNodeName", null == parentNode.getRoleName() ? "" : parentNode.getRoleName());
                }*/
                intent.putExtra("nodeId", n.getUserId());
                /*intent.putExtra("nodeName", n.getParent().getRoleName());*/

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
            if (n.isLeaf()) {
                roleName = roleName.substring(0, roleName.indexOf("("));
            }

            holder.txtTitle.setText(roleName == null || "null".equals(roleName) ? "" : roleName);
            if (n.isLeaf() && n.isUser()) {
                /**
                 * 是叶节点 不显示展开和折叠状态图标
                 */
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