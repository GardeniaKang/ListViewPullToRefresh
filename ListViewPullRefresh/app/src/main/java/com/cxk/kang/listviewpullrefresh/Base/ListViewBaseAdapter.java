package com.cxk.kang.listviewpullrefresh.Base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: xiaokang
 * time  : 17/2/20
 * desc  : ListView适配器的基类
 */

public abstract class ListViewBaseAdapter<T> extends SimpleViewHolderAdapter<ListViewBaseAdapter.ListViewViewHolder, T> {

    public ListViewBaseAdapter(Context context) {
        super(context);
    }

    @LayoutRes
    public int getItemLayoutRes(ViewGroup parent, int position){return 0;}

    public View getItemLayout(ViewGroup parent, int position){
        return new View(parent.getContext());
    }

    @Override
    public ListViewViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        if (getItemLayoutRes(parent,position) > 0){
            return new ListViewViewHolder(mInflater.inflate(getItemLayoutRes(parent,position),null));
        }
        return new ListViewViewHolder(getItemLayout(parent,position));
    }

    public static class ListViewViewHolder extends SimpleViewHolderAdapter.ViewHolder{

        private final SparseArray<View> mViews;

        public ListViewViewHolder(View itemView) {
            super(itemView);
            this.mViews = new SparseArray<View>();
        }

        public <V extends View> V getView(int id){
            if (mViews.get(id) == null){
                View view = getConvertView().findViewById(id);
                mViews.put(id, view);
            }
            return (V) mViews.get(id);
        }

        public <V extends View> V getView(Class<V> clazz, int id){
            if (mViews.get(id) == null){
                View view = getConvertView().findViewById(id);
                mViews.put(id, view);
            }
            return clazz.cast(mViews.get(id));
        }
    }
}
