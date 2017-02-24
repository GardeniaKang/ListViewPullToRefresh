package com.cxk.kang.listviewpullrefresh.Base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import butterknife.ButterKnife;

/**
 * author: xiaokang
 * time  : 17/2/24
 * desc  : Activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
    }

    protected abstract
    @LayoutRes
    int getLayoutResId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
