package com.cxk.kang.listviewpullrefresh.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.cxk.kang.listviewpullrefresh.R;
import com.cxk.kang.listviewpullrefresh.custview.KPullToRefreshView;
import com.cxk.kang.listviewpullrefresh.custview.KPullToRefreshView.OnHeaderRefreshListener;
import com.cxk.kang.listviewpullrefresh.custview.KPullToRefreshView.OnFooterRefreshListener;

/**
 * author: xiaokang
 * time  : 17/2/24
 * desc  : 列表刷新类Activity 基类
 */

public abstract class ListViewBaseActivity extends BaseActivity implements OnHeaderRefreshListener,OnFooterRefreshListener
{

    protected KPullToRefreshView pullToRefreshView;
    protected ListView listView;

    @Override
    protected int getLayoutResId() {
        return R.layout.listview_refresh_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pullToRefreshView = (KPullToRefreshView) findViewById(R.id.pull_to_refresh_view);
        listView = (ListView) findViewById(R.id.list_view);

        pullToRefreshView.setOnHeaderRefreshListener(this);
        pullToRefreshView.setOnFooterRefreshListener(this);
    }
}
