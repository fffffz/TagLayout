package com.fffz.taglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TagLayout extends ViewGroup {

    private int mTagPaddingLeft;
    private int mTagPaddingTop;
    private int mTagPaddingRight;
    private int mTagPaddingBottom;
    private int mTagWidth;
    private int mTagHeight;
    private float mTagRadius;
    private int mTagHorizontalGap;
    private int mTagVerticalGap;
    private Drawable mTagBackground;
    private int mTagTextColor;
    private float mTagTextSize;

    private OnItemClickListener mOnItemClickListener;

    public TagLayout(Context context) {
        super(context);
        mTagWidth = LayoutParams.WRAP_CONTENT;
        mTagHeight = LayoutParams.WRAP_CONTENT;
        mTagTextColor = Color.BLACK;
        mTagTextSize = dp2px(context, 12);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagLayout);
        int padding = a.getDimensionPixelSize(R.styleable.TagLayout_tagPadding, 0);
        mTagPaddingLeft = padding;
        mTagPaddingRight = padding;
        mTagPaddingTop = padding;
        mTagPaddingBottom = padding;
        mTagPaddingLeft = a.getDimensionPixelSize(R.styleable.TagLayout_tagPaddingLeft, mTagPaddingLeft);
        mTagPaddingTop = a.getDimensionPixelSize(R.styleable.TagLayout_tagPaddingTop, mTagPaddingTop);
        mTagPaddingRight = a.getDimensionPixelSize(R.styleable.TagLayout_tagPaddingRight, mTagPaddingRight);
        mTagPaddingBottom = a.getDimensionPixelSize(R.styleable.TagLayout_tagPaddingBottom, mTagPaddingBottom);
        mTagHorizontalGap = a.getDimensionPixelOffset(R.styleable.TagLayout_tagHorizontalGap, 0);
        mTagVerticalGap = a.getDimensionPixelOffset(R.styleable.TagLayout_tagVerticalGap, 0);
        mTagWidth = a.getDimensionPixelSize(R.styleable.TagLayout_tagWidth, LayoutParams.WRAP_CONTENT);
        mTagHeight = a.getDimensionPixelSize(R.styleable.TagLayout_tagHeight, LayoutParams.WRAP_CONTENT);
        mTagRadius = a.getDimension(R.styleable.TagLayout_tagRadius, 0);
        mTagBackground = a.getDrawable(R.styleable.TagLayout_tagBackground);
        mTagTextColor = a.getColor(R.styleable.TagLayout_tagTextColor, Color.BLACK);
        mTagTextSize = a.getDimension(R.styleable.TagLayout_tagTextSize, dp2px(context, 12));
    }

    public void setText(String... texts) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setOnClickListener(null);
        }
        removeAllViews();
        if (texts == null) {
            return;
        }
        Context context = getContext();
        for (int i = 0; i < texts.length; i++) {
            TagView tagView = new TagView(context);
            tagView.setPadding(mTagPaddingLeft, mTagPaddingTop, mTagPaddingRight, mTagPaddingBottom);
            LayoutParams lp = new LayoutParams(mTagWidth, mTagHeight);
            tagView.setRadius(mTagRadius);
            tagView.setTextColor(mTagTextColor);
            tagView.setBackgroundDrawable(mTagBackground.getConstantState().newDrawable());
            tagView.setTextSize(mTagTextSize);
            tagView.setText(texts[i]);
            addView(tagView, lp);
            if (mOnItemClickListener != null) {
                tagView.setOnClickListener(new ChildClickListenerImpl(i));
            }
        }
    }

    public float getTagHorizontalGap() {
        return mTagHorizontalGap;
    }

    public void setTagHorizontalGap(int tagHorizontalGap) {
        mTagHorizontalGap = tagHorizontalGap;
    }

    public float getTagVerticalGap() {
        return mTagVerticalGap;
    }

    public void setTagVerticalGap(int tagVerticalGap) {
        mTagVerticalGap = tagVerticalGap;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int right = measuredWidth - getPaddingRight();
        final int childCount = getChildCount();
        int childLeft = getPaddingLeft();
        int lineTop = getPaddingTop();
        int lineBottom = 0;
        int lineIndex = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
            TagLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            childLeft += lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();
            if (childRight > right) {
                lineTop = lineBottom;
                lineBottom = 0;
                childLeft = getPaddingLeft() + lp.leftMargin;
                childRight = childLeft + child.getMeasuredWidth();
                lineIndex++;
            }
            int childTop;
            if (lineIndex == 0) {
                childTop = lineTop + lp.topMargin;
            } else {
                childTop = lineTop + (lp.topMargin > 0 ? lp.topMargin : mTagVerticalGap);
            }
            int childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childBottom);
            childLeft = childRight + (lp.rightMargin > 0 ? lp.rightMargin : mTagHorizontalGap);
            lineBottom = Math.max(childBottom + lp.bottomMargin, lineBottom);
        }
        int measuredHeight;
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            measuredHeight = lineBottom + getPaddingBottom();
        } else {
            measuredHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        setChildOnClickListener();
    }

    private void setChildOnClickListener() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (mOnItemClickListener != null) {
                child.setOnClickListener(new ChildClickListenerImpl(i));
            } else {
                child.setOnClickListener(null);
            }
        }
    }

    private class ChildClickListenerImpl implements OnClickListener {

        private final int mPosition;

        private ChildClickListenerImpl(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener == null) {
                return;
            }
            TagView tagView = (TagView) v;
            mOnItemClickListener.onItemClick(TagLayout.this, tagView, mPosition);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TagLayout parent, TagView view, int position);
    }

}