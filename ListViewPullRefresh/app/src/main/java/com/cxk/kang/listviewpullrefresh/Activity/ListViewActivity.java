package com.cxk.kang.listviewpullrefresh.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cxk.kang.listviewpullrefresh.Adapter.ListViewAdapter;
import com.cxk.kang.listviewpullrefresh.Base.ListViewBaseActivity;
import com.cxk.kang.listviewpullrefresh.R;
import com.cxk.kang.listviewpullrefresh.custview.KPullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnItemClick;

/**
 * author: xiaokang
 * time  : 17/2/24
 * desc  : listView列表Demo
 */

public class ListViewActivity extends ListViewBaseActivity {

    private static final String HEADER = "header";
    private static final String FOOTER = "footer";

    private List<String> mDatas;
    private ListViewAdapter adapter;

    private Handler mHandler = new Handler();

    @Override
    protected int getLayoutResId() {
        return super.getLayoutResId();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatas = new ArrayList<>();
        adapter = new ListViewAdapter(this);
        listView.setAdapter(adapter);
        pullToRefreshView.addFooterView();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 6; i++) {
            mDatas.add("" + i);
        }

        adapter.notifyListData(true, mDatas);
    }

    @OnItemClick(R.id.list_view)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,"你点击了Item.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnFooterRefresh(KPullToRefreshView view) {
        mHandler.postDelayed(new DelayRunnable(FOOTER),2000);
    }

    @Override
    public void OnHeaderRefresh(KPullToRefreshView view) {
        mHandler.postDelayed(new DelayRunnable(HEADER),2000);
    }

    /**
     * 模拟下拉刷新和上拉加载效果
     */
    private class DelayRunnable implements Runnable {
        private String type;

        public DelayRunnable(String type){
            this.type = type;
        }

        @Override
        public void run() {
            if (TextUtils.equals(type, HEADER)){
                pullToRefreshView.onHeaderRefreshComplete(true);
                adapter.notifyListData(true,mDatas);
            } else if (TextUtils.equals(type, FOOTER)){
                pullToRefreshView.onFooterRefreshComplete(true);
                List<String> newData = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    newData.add("" + i);
                }

                adapter.notifyListData(false, newData);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyHandler();
    }

    /**
     * destroy handler.
     */
    private void destroyHandler() {
        if (null != mHandler){
            mHandler = null;
        }
    }
}
