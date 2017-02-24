package com.cxk.kang.listviewpullrefresh.Base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author: xiaokang
 * time  : 17/2/20
 * desc  :
 */

public abstract class SimpleViewHolderAdapter<VH extends SimpleViewHolderAdapter.ViewHolder, T> extends BaseAdapter {

    private List<T> mList;
    protected LayoutInflater mInflater;

    public SimpleViewHolderAdapter(Context context){
        mList = new ArrayList<T>();
        this.mInflater = LayoutInflater.from(context);
    }

    public SimpleViewHolderAdapter notifyListData(boolean isClear, Collection<? extends T> list){
        if (null == list)
            return this;

        if (isClear)
            mList.clear();

        mList.addAll(list);
        notifyDataSetChanged();
        return this;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        VH holder;
        if (view == null){
            holder = onCreateViewHolder(viewGroup, position);
            holder.view.setTag(holder);
        } else {
            holder = (VH) view.getTag();
        }

        onBindViewHolder(holder, mList == null ? null : (mList.get(position) == null ? null : mList.get(position)));
        return holder.view;
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, int position);

    public abstract void onBindViewHolder(VH holder, T data);

    static class ViewHolder{
        View view;

        public ViewHolder(View itemView) {
            this.view = itemView;
        }

        public View getConvertView(){
            return view;
        }
    }

}
