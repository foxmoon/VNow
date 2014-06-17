package com.nyist.vnow.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nyist.vnow.R;

public class ViEPullToRefreshListView extends ListView implements OnScrollListener {
    private static final String TAG = "ViEPullToRefreshListView";
    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;
    // ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
    private final static int RATIO = 3;
    private LayoutInflater mInflater;
    private LinearLayout mHeadView;
    private TextView mTxtTip;
    private TextView mTxtLastUpdate;
    private ImageView mImgArrow;
    private ProgressBar mProgressBar;
    private RotateAnimation mAnimation;
    private RotateAnimation mReverseAnimation;
    // ���ڱ�֤startY��ֵ��һ�������touch�¼���ֻ����¼һ��
    private boolean isRecored;
    private int mHeadContentWidth;
    private int mHeadContentHeight;
    private int mStartY;
    private int mFirstItemIndex;
    private int mCurState;
    private boolean isBack;
    private OnRefreshListener mRefreshListener;
    private boolean isRefreshAble;

    public ViEPullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public ViEPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setCacheColorHint(context.getResources().getColor(R.color.transparent));
        mInflater = LayoutInflater.from(context);
        mHeadView = (LinearLayout) mInflater.inflate(R.layout.avc_pull_to_refresh_head, null);
        mImgArrow = (ImageView) mHeadView
                .findViewById(R.id.head_arrowImageView);
        mImgArrow.setMinimumWidth(50);
        mImgArrow.setMinimumHeight(50);
        mProgressBar = (ProgressBar) mHeadView
                .findViewById(R.id.head_progressBar);
        mTxtTip = (TextView) mHeadView.findViewById(R.id.head_tipsTextView);
        mTxtLastUpdate = (TextView) mHeadView
                .findViewById(R.id.head_lastUpdatedTextView);
        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadContentWidth = mHeadView.getMeasuredWidth();
        mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
        mHeadView.invalidate();
        addHeaderView(mHeadView, null, false);
        setOnScrollListener(this);
        mAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(250);
        mAnimation.setFillAfter(true);
        mReverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseAnimation.setInterpolator(new LinearInterpolator());
        mReverseAnimation.setDuration(200);
        mReverseAnimation.setFillAfter(true);
        mCurState = DONE;
        isRefreshAble = false;
    }

    public void setRefreshAble(boolean isAble) {
        this.isRefreshAble = isAble;
    }

    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
            int arg3) {
        mFirstItemIndex = firstVisiableItem;
    }

    public void onScrollStateChanged(AbsListView arg0, int arg1) {}

    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshAble) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mFirstItemIndex == 0 && !isRecored) {
                        isRecored = true;
                        mStartY = (int) event.getY();
                        // Log.v(TAG, "��downʱ���¼��ǰλ�á�");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mCurState != REFRESHING && mCurState != LOADING) {
                        if (mCurState == DONE) {
                            // ʲô������
                        }
                        if (mCurState == PULL_To_REFRESH) {
                            mCurState = DONE;
                            changeHeaderViewByState();
                            // Log.v(TAG, "������ˢ��״̬����done״̬");
                        }
                        if (mCurState == RELEASE_To_REFRESH) {
                            mCurState = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            // Log.v(TAG, "���ɿ�ˢ��״̬����done״̬");
                        }
                    }
                    isRecored = false;
                    isBack = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    if (!isRecored && mFirstItemIndex == 0) {
                        // Log.v(TAG, "��moveʱ���¼��λ��");
                        isRecored = true;
                        mStartY = tempY;
                    }
                    if (mCurState != REFRESHING && isRecored
                            && mCurState != LOADING) {
                        // ��֤������padding�Ĺ���У���ǰ��λ��һֱ����head����������б?����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���
                        // ��������ȥˢ����
                        if (mCurState == RELEASE_To_REFRESH) {
                            setSelection(0);
                            // �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
                            if (((tempY - mStartY) / RATIO < mHeadContentHeight)
                                    && (tempY - mStartY) > 0) {
                                mCurState = PULL_To_REFRESH;
                                changeHeaderViewByState();
                                // Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
                            }
                            // һ�����Ƶ�����
                            else if (tempY - mStartY <= 0) {
                                mCurState = DONE;
                                changeHeaderViewByState();
                                // Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");
                            }
                            // �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�
                            else {
                                // ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
                            }
                        }
                        // ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
                        if (mCurState == PULL_To_REFRESH) {
                            setSelection(0);
                            // ���������Խ���RELEASE_TO_REFRESH��״̬
                            if ((tempY - mStartY) / RATIO >= mHeadContentHeight) {
                                mCurState = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                                // Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
                            }
                            // ���Ƶ�����
                            else if (tempY - mStartY <= 0) {
                                mCurState = DONE;
                                changeHeaderViewByState();
                                // Log.v(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");
                            }
                        }
                        // done״̬��
                        if (mCurState == DONE) {
                            if (tempY - mStartY > 0) {
                                mCurState = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                        }
                        // ����headView��size
                        if (mCurState == PULL_To_REFRESH) {
                            mHeadView.setPadding(0, -1 * mHeadContentHeight
                                    + (tempY - mStartY) / RATIO, 0, 0);
                        }
                        // ����headView��paddingTop
                        if (mCurState == RELEASE_To_REFRESH) {
                            mHeadView.setPadding(0, (tempY - mStartY) / RATIO
                                    - mHeadContentHeight, 0, 0);
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    // ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
    private void changeHeaderViewByState() {
        switch (mCurState) {
            case RELEASE_To_REFRESH:
                mImgArrow.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mTxtTip.setVisibility(View.VISIBLE);
                mTxtLastUpdate.setVisibility(View.VISIBLE);
                mImgArrow.clearAnimation();
                mImgArrow.startAnimation(mAnimation);
                mTxtTip.setText(getContext().getString(R.string.str_release_refresh));
                // Log.v(TAG, "��ǰ״̬���ɿ�ˢ��");
                break;
            case PULL_To_REFRESH:
                mProgressBar.setVisibility(View.GONE);
                mTxtTip.setVisibility(View.VISIBLE);
                mTxtLastUpdate.setVisibility(View.VISIBLE);
                mImgArrow.clearAnimation();
                mImgArrow.setVisibility(View.VISIBLE);
                // ����RELEASE_To_REFRESH״̬ת������
                if (isBack) {
                    isBack = false;
                    mImgArrow.clearAnimation();
                    mImgArrow.startAnimation(mReverseAnimation);
                    mTxtTip.setText(getContext().getString(R.string.str_pull_refresh));
                }
                else {
                    mTxtTip.setText(getContext().getString(R.string.str_pull_refresh));
                }
                // Log.v(TAG, "��ǰ״̬������ˢ��");
                break;
            case REFRESHING:
                mHeadView.setPadding(0, 0, 0, 0);
                mProgressBar.setVisibility(View.VISIBLE);
                mImgArrow.clearAnimation();
                mImgArrow.setVisibility(View.GONE);
                mTxtTip.setText(getContext().getString(R.string.str_refreshing));
                mTxtLastUpdate.setVisibility(View.VISIBLE);
                // Log.v(TAG, "��ǰ״̬,����ˢ��...");
                break;
            case DONE:
                mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
                mProgressBar.setVisibility(View.GONE);
                mImgArrow.clearAnimation();
                mImgArrow.setImageResource(R.drawable.arrow);
                mTxtTip.setText(getContext().getString(R.string.str_pull_refresh));
                mTxtLastUpdate.setVisibility(View.VISIBLE);
                // Log.v(TAG, "��ǰ״̬��done");
                break;
        }
    }

    public void setonRefreshListener(OnRefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
        isRefreshAble = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void onRefreshComplete() {
        mCurState = DONE;
        mTxtLastUpdate.setText(getContext().getString(R.string.str_recently_update) + new Date().toLocaleString());
        changeHeaderViewByState();
    }

    private void onRefresh() {
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    // �˷���ֱ���հ��������ϵ�һ������ˢ�µ�demo���˴��ǡ����ơ�headView��width�Լ�height
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        }
        else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
        mTxtLastUpdate.setText(getContext().getString(R.string.str_recently_update) + new Date().toLocaleString());
        super.setAdapter(adapter);
    }
}
