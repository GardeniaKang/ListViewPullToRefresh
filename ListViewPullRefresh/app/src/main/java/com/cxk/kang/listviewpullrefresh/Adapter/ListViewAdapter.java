package com.cxk.kang.listviewpullrefresh.Adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxk.kang.listviewpullrefresh.Base.ListViewBaseAdapter;
import com.cxk.kang.listviewpullrefresh.R;

/**
 * author: xiaokang
 * time  : 17/2/24
 * desc  : 列表适配器
 */

public class ListViewAdapter extends ListViewBaseAdapter<String> {

    public ListViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutRes(ViewGroup parent, int position) {
        return R.layout.view_list_item;
    }

    @Override
    public void onBindViewHolder(ListViewViewHolder holder, String data) {
        holder.getView(TextView.class,R.id.title_category_tv).setText("[标题" + data + "]");
    }
}
