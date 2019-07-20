package com.example.a13703.my_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Inter_kou.InnerItemOnclickListener;
import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;

import java.util.List;

/**
 * Created by 13703 on 2019/6/16.
 */

public class MyExtendableListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener{
    private List<String> mGroup;           //组列表
    private List<List<String>> mChild;     //子列表
    private List<List<Integer>> logou;     //图片列表
    private InnerItemOnclickListener mListener;

    public MyExtendableListViewAdapter(List mGroup,List mChild){
        this.mGroup = mGroup;
        this.mChild = mChild;
    }
    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
        this.mListener=listener;
    }
    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mChild.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mGroup.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mChild.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item,viewGroup,false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.parent = (TextView)view.findViewById(R.id.parent_content);
            groupViewHolder.add_list = (ImageView)view.findViewById(R.id.add_list) ;
            view.setTag(R.id.group,i);
            view.setTag(R.id.item,-1);
            view.setTag(groupViewHolder);
        }else{
            groupViewHolder = (GroupViewHolder)view.getTag();
        }
        groupViewHolder.parent.setText(mGroup.get(i));
        groupViewHolder.add_list.setOnClickListener(this);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        if(view==null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.child_item,viewGroup,false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.child = (TextView)view.findViewById(R.id.content);
            view.setTag(R.id.group,i);
            view.setTag(R.id.item,i1);
            view.setTag(childViewHolder);
        }else{
            childViewHolder = (ChildViewHolder)view.getTag();
        }
        childViewHolder.child.setText(mChild.get(i).get(i1));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    @Override
    public void onClick(View view) {
        mListener.itemClick(view);
    }

    static class GroupViewHolder{
        TextView parent;
        ImageView add_list;
    }
    static class ChildViewHolder{
        TextView child;
        ImageView imageView;
    }

}
