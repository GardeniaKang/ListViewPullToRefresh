package com.cxk.kang.listviewpullrefresh.custview;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxk.kang.listviewpullrefresh.R;

/**
 * author: xiaokang
 * time  : 17/2/15
 * desc  : 自定义刷新，适合于ListView和GridView.
 */

public class KPullToRefreshView extends LinearLayout {

    // pull state
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;
    // refresh state
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REGRESH = 3;
    private static final int REFRESHING = 4;

    private static final String HEADER = "header";
    private static final String FOOTER = "footer";

    /**
     * list or grid
     */
    private AdapterView<?> mAdapterView;

    /**
     * ScrollView
     */
    private ScrollView mScrollView;

    /**
     * the last Y
     */
    private int mLastMotionY;

    /**
     * header view
     */
    private View mHeaderView;

    /**
     * footer view
     */
    private View mFooterView;

    /**
     * header view height
     */
    private int mHeaderViewHeight = 0;

    /**
     * footer view height
     */
    private int mFooterViewHeight = 0;

    /**
     * header view image
     */
    private ImageView mHeaderImageView;

    /**
     * footer view image
     */
    private ImageView mFooterImageView;

    /**
     * header view title text
     */
    private TextView mHeaderTextView;

    /**
     * footer view title text
     */
    private TextView mFooterTextView;

    /**
     * header progress bar
     */
    private ProgressBar mHeaderProgressBar;

    /**
     * footer progress bar
     */
    private ProgressBar mFooterProgressBar;

    /**
     * layout inflater
     */
    private LayoutInflater mInflater;

    /**
     * header view current state
     */
    private int mHeaderState = 0;

    /**
     * footer view current state
     */
    private int mFooterState = 0;

    /**
     * pull state
     */
    private int mPullState = 0;

    /**
     * if the distance is less than or equal to 5 pixels on the Y axis,passing an event to a child view
     */
    private final int moveY = 5;

    /**
     * animation rotation s-resize
     */
    private RotateAnimation mFlipAnimation;

    /**
     * animation reverse rotation
     */
    private RotateAnimation mReverseFlipAnimation;

    /**
     * header refresh listener
     */
    private OnHeaderRefreshListener mOnHeaderRefreshListener;

    /**
     * footer refresh listener
     */
    private OnFooterRefreshListener mOnFooterRefreshListener;

    /**
     * handler for success.
     */
    private Handler mHandler;

    public KPullToRefreshView(Context context) {
        super(context);
        initView();
    }

    public KPullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public KPullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * initialize view
     */
    private void initView() {

        mInflater = LayoutInflater.from(getContext());

        // clockwise rotate 180 degrees
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);

        // counterclockwise rotate 180 degrees
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        addHeaderView();
    }

    /**
     * header View
     */
    private void addHeaderView() {
        mHeaderView = mInflater.inflate(R.layout.refresh_header_view,this,false);

        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.kpull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.kpull_to_refresh_text);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.kpull_to_refresh_progress);

        measureView(mHeaderView);

        mHeaderViewHeight = mHeaderView.getMeasuredHeight() + 10;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                mHeaderViewHeight);
        params.topMargin = - mHeaderViewHeight;
        addView(mHeaderView,params);
    }

    /**
     * footer View
     */
    public void addFooterView() {
        if (null == mFooterView && getChildCount() < 3){
            mFooterView = mInflater.inflate(R.layout.refresh_footer_view,this,false);

            mFooterImageView = (ImageView) mFooterView.findViewById(R.id.kpull_to_load_image);
            mFooterTextView = (TextView) mFooterView.findViewById(R.id.kpull_to_load_text);
            mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.kpull_to_load_progress);

            measureView(mFooterView);

            mFooterViewHeight = mFooterView.getMeasuredHeight();
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    mFooterViewHeight);
            addView(mFooterView,params);
        }
    }

    /**
     * remove footer view
     */
    public void removeFooterView(){
        if (null == mFooterView){
            return;
        }

        removeView(mFooterView);
        mFooterView = null;
    }

    private void measureView(View child){
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (null == params){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,0,params.width);
        int lpHeight = params.height;
        int childHeightSpec;

        if (lpHeight > 0){
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec,childHeightSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initContentAdapterView();
    }

    /**
     * initialize AdapterView like ListView or GridView, and so on...
     * or
     * initialize ScrollView like RecycleView or others.
     */
    private void initContentAdapterView(){
        int count = getChildCount();
        if (count < 2){
            throw new IllegalArgumentException("this layout must contain 3 child views, and AdapterView or ScrollView must in the second position");
        }

        View view;
        for (int i = 0; i < count; i++) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>){
                mAdapterView = (AdapterView<?>) view;
            }

            if (view instanceof ScrollView){
                mScrollView = (ScrollView) view;
            }
        }

        if (mAdapterView == null && mScrollView == null){
            throw new IllegalArgumentException("this layout must contain a AdapterView or ScrollView");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getRawY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                if (Math.abs(deltaY) > moveY){
                    if (isRefreshViewScroll(deltaY)){
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    /**
     * is KPullToRefreshView moving on
     * @param deltaY
     * @return
     */
    private boolean isRefreshViewScroll(int deltaY){
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING){
            return false;
        }

        // 1. AdapterView
        if (null != mAdapterView){
            if (deltaY > 0){// the child view was moving to the top position.
                View child = mAdapterView.getChildAt(0);
                if (child == null){
                    return false;
                }

                if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0){
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

                int top = child.getTop();
                int padding = mAdapterView.getPaddingTop();
                if (mAdapterView.getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8){
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
            } else if (deltaY < 0){
                View lastChild = mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
                if (lastChild == null){
                    return false;
                }

                if (lastChild.getBottom() <= getHeight()
                        && mAdapterView.getLastVisiblePosition() == mAdapterView.getCount() - 1){
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }

        // 2. ScrollView
        if (null != mScrollView){
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0 && mScrollView.getScaleY() == 0){
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScaleY()){
                mPullState = PULL_UP_STATE;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaY = y - mLastMotionY;
                if (Math.abs(deltaY) > moveY){
                    if (mPullState == PULL_DOWN_STATE){// prepare to refresh
                        headerPrepareToRefresh(deltaY);
                    } else if (mPullState == PULL_UP_STATE){// prepare to load
                        footerPrepareToRefresh(deltaY);
                    }
                    mLastMotionY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE){
                    if (topMargin >= 0){
                        headerRefreshing();
                    } else {
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                } else if (mPullState == PULL_UP_STATE){
                    if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight){
                        footerRefreshing();
                    } else {
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * footer view start refresh.
     */
    private void footerRefreshing() {
        if (mFooterView != null){
            mFooterState = REFRESHING;
            int topMargin = mHeaderViewHeight + mFooterViewHeight;
            setHeaderTopMargin(-topMargin);
            mFooterImageView.setVisibility(GONE);
            mFooterImageView.clearAnimation();
            mFooterImageView.setImageDrawable(null);
            mFooterProgressBar.setVisibility(VISIBLE);
            mFooterTextView.setText("正在加载");
            if (mOnFooterRefreshListener != null){
                mOnFooterRefreshListener.OnFooterRefresh(this);
            }
        }
    }

    /**
     * set header view top margin.
     * @param topMargin
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    /**
     * header view start refresh.
     */
    private void headerRefreshing() {
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mHeaderImageView.setVisibility(GONE);
        mHeaderImageView.clearAnimation();
        mHeaderImageView.setImageDrawable(null);
        mHeaderProgressBar.setVisibility(VISIBLE);
        mHeaderTextView.setText("正在刷新");
        if (mOnHeaderRefreshListener != null){
            mOnHeaderRefreshListener.OnHeaderRefresh(this);
        }
    }

    /**
     * get header top_margin
     * @return
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * header, prepare to refresh, moving, do not deliver.
     * @param deltaY
     */
    private void headerPrepareToRefresh(int deltaY){
        int newTopMargin = changeingHeaderViewTopMargin(deltaY);

        // if topMargin >= 0, then change the content text of header view.
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REGRESH){
            mHeaderTextView.setText("释放刷新");
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderState = RELEASE_TO_REGRESH;
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight){
            mHeaderTextView.setText("下拉刷新");
            mHeaderImageView.clearAnimation();
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer, prepare to refresh, moving, do not deliver.
     * @param deltaY
     */
    private void footerPrepareToRefresh(int deltaY){

        if (null != mFooterView){
            int newTopMargin = changeingHeaderViewTopMargin(deltaY);

            // if topMargin >= 0, then change the content text of header view.
            if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
                    && mFooterState != RELEASE_TO_REGRESH){
                mFooterTextView.setText("松开加载");
                mFooterImageView.clearAnimation();
                mFooterImageView.startAnimation(mFlipAnimation);
                mFooterState = RELEASE_TO_REGRESH;
            } else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight) ){
                mFooterTextView.setText("上拉加载");
                mFooterImageView.clearAnimation();
                mFooterState = PULL_TO_REFRESH;
            }
        }
    }

    /**
     * change top margin of header view.
     *
     * @param deltaY
     * @return
     */
    private int changeingHeaderViewTopMargin(int deltaY){
        if (null != mHeaderView){
            LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
            float newTopMargin = params.topMargin + deltaY * 0.3f;

            // 1. about pull_up
            if (deltaY > 0 && mPullState == PULL_UP_STATE
                    && Math.abs(params.topMargin) <= mHeaderViewHeight){
                return params.topMargin;
            }

            // 2. about pull_down
            if (deltaY < 0 && mPullState == PULL_DOWN_STATE
                    && Math.abs(params.topMargin) >= mHeaderViewHeight){
                return params.topMargin;
            }

            params.topMargin = (int) newTopMargin;
            mHeaderView.setLayoutParams(params);
            invalidate();
            return params.topMargin;
        }
        return 0;
    }

    /**
     * header refresh complete.
     * @param isSuccess
     */
    public void onHeaderRefreshComplete(boolean isSuccess){

        mHeaderProgressBar.setVisibility(GONE);
        mHeaderImageView.setVisibility(VISIBLE);
        mHeaderImageView.setImageResource(R.drawable.icon_yes);
        mHeaderTextView.setText("刷新成功");

        mHandler = new Handler();
        mHandler.postDelayed(new PostDelayRunnable(HEADER),1500);
    }

    /**
     * footer refresh complete.
     * @param isSuccess
     */
    public void onFooterRefreshComplete(boolean isSuccess){
        mFooterProgressBar.setVisibility(GONE);
        mFooterImageView.setVisibility(VISIBLE);
        mFooterImageView.setImageResource(R.drawable.icon_yes);
        mFooterTextView.setText("加载成功");

        mHandler = new Handler();
        mHandler.postDelayed(new PostDelayRunnable(FOOTER),1500);
    }

    /**
     * initialize header view.
     */
    private void headerRefresh() {
        setHeaderTopMargin(-mHeaderViewHeight);
        mHeaderImageView.setImageResource(R.drawable.icon_refresh_arrow);
        mHeaderTextView.setText("下拉刷新");
        mHeaderState = PULL_TO_REFRESH;

        destroyHandler();
    }

    /**
     * initialize footer view.
     */
    private void footerRefresh() {
        setHeaderTopMargin(-mHeaderViewHeight);
        mFooterImageView.setImageResource(R.drawable.icon_refresh_arrow_up);
        mFooterTextView.setText("上拉加载");
        mFooterState = PULL_TO_REFRESH;

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

    private class PostDelayRunnable implements Runnable {

        private String type;

        public PostDelayRunnable(String type){
            this.type = type;
        }

        @Override
        public void run() {
            if (TextUtils.equals(type, HEADER)){
                headerRefresh();
            } else if (TextUtils.equals(type, FOOTER)){
                footerRefresh();
            }
        }
    }

    /**
     * set headerRefreshListener
     * @param headerRefreshListener
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener){
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    /**
     * set footerRefreshListener
     * @param footerRefreshListener
     */
    public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener){
        mOnFooterRefreshListener = footerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        void OnHeaderRefresh(KPullToRefreshView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header view should be refreshed.
     */
    public interface OnFooterRefreshListener {
        void OnFooterRefresh(KPullToRefreshView view);
    }
}
