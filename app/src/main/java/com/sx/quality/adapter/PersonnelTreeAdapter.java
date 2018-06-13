package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.popwindow.WorkingPopupWindow;
import com.sx.quality.tree.Node;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.SpUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 树数据源构造器
 */
public class PersonnelTreeAdapter extends BaseAdapter {
    private LayoutInflater lif;
    private List<Node> allCache = new ArrayList<>();
    public List<Node> all = new ArrayList<>();
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Activity mContext;

    private Node rootNode;

    /**
     * @param mContext
     * @param rootNode
     */
    public PersonnelTreeAdapter(Activity mContext, Node rootNode) {
        this.mContext = mContext;
        this.lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rootNode = rootNode;
        addNode(rootNode);
    }

    /**
     * 添加节点
     *
     * @param node
     */
    private void addNode(Node node) {
        if (node.getParent() != null) {
            all.add(node);
            allCache.add(node);
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
        all.clear();
        for (int i = 0; i < allCache.size(); i++) {
            Node n = allCache.get(i);
            if (!n.isParentCollapsed() || n.isRoot()) {
                all.add(n);
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
        all.clear();
        for (int i = 0; i < allCache.size(); i++) {
            Node n = allCache.get(i);
            if (n.getLevel() <= level) {
                // 上层都设置展开状态
                if (n.getLevel() < level) {
                    n.setExpanded(true);
                    // 最后一层都设置折叠状态
                } else {
                    n.setExpanded(false);
                }
                all.add(n);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(final int position) {
        Node n = all.get(position);
        if (n != null) {
            // 是否是文件夹（文件夹继续展开---工序进入上传照片界面）0:不是文件夹 1：是文件夹
            if (!n.getFolderFlag().equals("2")) {
                // 是否处于展开状态
                n.setExpanded(!n.isExpanded());
                filterNode();
                this.notifyDataSetChanged();
            } else {
                // 是否处于选中状态
                for (Node node : all) {
                    node.setCanClick(false);
                }
                SpUtil.remove(mContext, ConstantsUtil.SELECT_USER_ID);
                SpUtil.put(mContext, ConstantsUtil.SELECT_USER_ID, n.getLevelId());
                n.setCanClick(!n.isCanClick());
                filterNode();
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * 展开所有选择项的父节点
     * @param node
     */
    public void expandParentNode(Node node){
        if (node.getParent() != null) {
            setChoice(node, rootNode);
            expandParentNode(node.getParent());
        }
    }

    /**
     * 展开所有选择项的父节点
     */
    private void setChoice(Node node, Node rootNode){
        if (node.getLevelId().equals(rootNode.getLevelId())) {
            rootNode.getParent().setExpanded(true);
            filterNode();
        }

        for (int i = 0; i < rootNode.getChildren().size(); i++) {
            setChoice(node, rootNode.getChildren().get(i));
        }
    }

    @Override
    public int getCount() {
        return all.size();
    }

    @Override
    public Object getItem(int position) {
        return all.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = this.lif.inflate(R.layout.item_personnel, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // 得到当前节点
        Node n = all.get(position);
        // 显示文本
        holder.txtTitle.setText(n.getLevelName());
        if (n.getFolderFlag().equals("2")) { // 人员
            // 不显示展开和折叠状态图标--->显示选中未选中图标
            holder.ivFoldState.setVisibility(View.GONE);
            holder.ivSelectState.setVisibility(View.VISIBLE);
        } else {
            holder.ivFoldState.setVisibility(View.VISIBLE);
            holder.ivSelectState.setVisibility(View.GONE);
        }

        /* 单击时控制子节点展开和折叠,状态图标改变  */
        if (n.isExpanded()) {
            holder.ivFoldState.setImageDrawable(ContextCompat.getDrawable(mContext, expandedIcon));
        } else {
            holder.ivFoldState.setImageDrawable(ContextCompat.getDrawable(mContext, collapsedIcon));
        }

        // 是否选中
        if (n.isCanClick()) {
            holder.ivSelectState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_check));
        } else {
            holder.ivSelectState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_un_check));
        }

        // 控制缩进
        if (n.getLevel() != 1) {
            holder.rlItemTree.setPadding(50 * (n.getLevel() - 1), 3, 3, 3);
        } else {
            holder.rlItemTree.setPadding(0 * (n.getLevel() - 1), 3, 3, 3);
        }
        return view;
    }

    /**
     * 列表项控件集合
     */
    private class ViewHolder {
        // 展开收缩图标
        @ViewInject(R.id.ivFoldState)
        private ImageView ivFoldState;
        // 选中图标
        @ViewInject(R.id.ivSelectState)
        private ImageView ivSelectState;
        // 标题
        @ViewInject(R.id.txtTitle)
        private TextView txtTitle;
        // rl
        @ViewInject(R.id.rlItemTree)
        private RelativeLayout rlItemTree;
    }

}