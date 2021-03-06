package kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.provider.ResourceProvider;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.provider.ScreenInfoProvider;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.provider.interfaces.IResourceProvider;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.provider.interfaces.IScreenInfoProvider;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.ui.interfaces.IScrollViewListener;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.ui.presentation.IStickyScrollPresentation;
import kro.kr.rhya_network.online_attendance.lib.com_github_amarjain07_StickyScrollView_1_0_2.ui.presenter.StickyScrollPresenter;

public class StickyScrollView extends NestedScrollView implements IStickyScrollPresentation {
    private IScrollViewListener scrollViewListener;

    private View stickyFooterView;
    private View stickyHeaderView;

    private static final String SCROLL_STATE = "scroll_state";
    private static final String SUPER_STATE = "super_state";

    private final StickyScrollPresenter mStickyScrollPresenter;

    public StickyScrollView(Context context) {
        this(context, null);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        IScreenInfoProvider screenInfoProvider = new ScreenInfoProvider(context);
        IResourceProvider resourceProvider = new ResourceProvider(context, attrs, R.styleable.StickyScrollView);
        mStickyScrollPresenter = new StickyScrollPresenter(this, screenInfoProvider, resourceProvider);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mStickyScrollPresenter.onGlobalLayoutChange(R.styleable.StickyScrollView_stickyHeader, R.styleable.StickyScrollView_stickyFooter);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (stickyFooterView != null && !changed) {
            mStickyScrollPresenter.recomputeFooterLocation(getRelativeTop(stickyFooterView));
        }

        if (stickyHeaderView != null) {
            mStickyScrollPresenter.recomputeHeaderLocation(stickyHeaderView.getTop());
        }
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getTop();
        } else {
            return myView.getTop() + getRelativeTop((View) myView.getParent());
        }
    }

    @Override
    public void initHeaderView(int id) {
        stickyHeaderView = findViewById(id);
        mStickyScrollPresenter.initStickyHeader(stickyHeaderView.getTop());
    }

    @Override
    public void initFooterView(int id) {
        stickyFooterView = findViewById(id);
        mStickyScrollPresenter.initStickyFooter(stickyFooterView.getMeasuredHeight(), getRelativeTop(stickyFooterView));
    }

    @Override
    public void freeHeader() {
        if (stickyHeaderView != null) {
            stickyHeaderView.setTranslationY(0);
            PropertySetter.setTranslationZ(stickyHeaderView, 0);
        }
    }

    @Override
    public void freeFooter() {
        if (stickyFooterView != null) {
            stickyFooterView.setTranslationY(0);
        }
    }

    @Override
    public void stickHeader(int translationY) {
        if (stickyHeaderView != null) {
            stickyHeaderView.setTranslationY(translationY);
            PropertySetter.setTranslationZ(stickyHeaderView, 1);
        }
    }

    @Override
    public void stickFooter(int translationY) {
        if (stickyFooterView != null) {
            stickyFooterView.setTranslationY(translationY);
        }
    }

    @Override
    public int getCurrentScrollYPos() {
        return getScrollY();
    }

    @Override
    protected void onScrollChanged(int mScrollX, int mScrollY, int oldX, int oldY) {
        super.onScrollChanged(mScrollX, mScrollY, oldX, oldY);
        mStickyScrollPresenter.onScroll(mScrollY);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(mScrollX, mScrollY, oldX, oldY);
        }
    }

    public IScrollViewListener getScrollViewListener() {
        return scrollViewListener;
    }

    public void setScrollViewListener(IScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public boolean isFooterSticky() {
        return mStickyScrollPresenter.isFooterSticky();
    }

    public boolean isHeaderSticky() {
        return mStickyScrollPresenter.isHeaderSticky();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollStopped(clampedY);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState());
        bundle.putBoolean(SCROLL_STATE, mStickyScrollPresenter.mScrolled);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mStickyScrollPresenter.mScrolled = bundle.getBoolean(SCROLL_STATE);
            state = bundle.getParcelable(SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}