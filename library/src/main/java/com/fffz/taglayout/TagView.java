package com.fffz.taglayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class TagView extends View {

    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics mFontMetrics;
    private RectF mBackgroundRect = new RectF();
    private RectF mTextRect = new RectF();
    private float mRadius;
    private int mTextColor;
    private float mTextSize;
    private String mText;
    private PorterDuffXfermode mSrcInMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private int mWidth, mHeight;

    public TagView(TagView tagView) {
        super(tagView.getContext());
        mText = tagView.mText;
        mTextSize = tagView.mTextSize;
        mTextColor = tagView.mTextColor;
        mRadius = tagView.mRadius;
        setBackgroundDrawable(tagView.getBackground());
        setupPaint();
    }

    public TagView(Context context) {
        super(context);
        mTextColor = Color.WHITE;
        mTextSize = dp2px(context, 12);
        setupPaint();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagView);
        mText = a.getString(R.styleable.TagView_text);
        mTextSize = a.getDimension(R.styleable.TagView_textSize, dp2px(context, 12));
        mTextColor = a.getColor(R.styleable.TagView_textColor, Color.RED);
        mRadius = a.getDimension(R.styleable.TagView_radius, 0);
        a.recycle();
        setupPaint();
    }

    private void setupPaint() {
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    private static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setWidth(int width) {
        mWidth = width;
        setRight(width);
    }

    public void setHeight(int height) {
        mHeight = height;
        setBottom(height);
    }

    public float getRadius() {
        return mRadius;
    }

    public TagView setRadius(float radius) {
        mRadius = radius;
        return this;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public TagView setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        return this;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public TagView setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        return this;
    }

    public String getText() {
        return mText;
    }

    public TagView setText(String text) {
        mText = text;
        requestLayout();
        return this;
    }

    private void measureText() {
        mTextRect.left = 0;
        mTextRect.top = 0;
        if (TextUtils.isEmpty(mText)) {
            mTextRect.right = 0;
            mTextRect.bottom = 0;
        } else {
            mTextRect.right = mTextPaint.measureText(mText);
            mFontMetrics = mTextPaint.getFontMetrics();
            mTextRect.bottom = mFontMetrics.descent - mFontMetrics.ascent;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureText();
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mBackgroundRect.right = getMeasuredWidth();
        mBackgroundRect.bottom = getMeasuredHeight();
    }

    private int measureWidth(int widthMeasureSpec) {
        int width = mWidth;
        if (width <= 0) {
            width = getLayoutParams().width;
        }
        if (width <= 0 && width == ViewGroup.LayoutParams.MATCH_PARENT) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (width <= 0) {
            width = (int) mTextRect.width() + getPaddingLeft() + getPaddingRight();
            Drawable background = getBackground();
            if (background != null) {
                Drawable drawable = background.getCurrent();
                if (drawable instanceof BitmapDrawable) {
                    width = Math.max(width, drawable.getIntrinsicWidth());
                }
            }
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = mHeight;
        if (height <= 0) {
            height = getLayoutParams().height;
        }
        if (height <= 0 && height == ViewGroup.LayoutParams.MATCH_PARENT) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        if (height <= 0) {
            height = (int) mTextRect.height() + getPaddingTop() + getPaddingBottom();
            Drawable background = getBackground();
            if (background != null) {
                Drawable drawable = background.getCurrent();
                if (drawable instanceof BitmapDrawable) {
                    height = Math.max(height, background.getIntrinsicHeight());
                }
            }
        }
        return height;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 255, Canvas.ALL_SAVE_FLAG);
        Drawable background = getBackground().getCurrent();
        if (background instanceof BitmapDrawable) {
            drawShape(canvas);
            drawBitmap(canvas, (BitmapDrawable) background);
        } else if (background instanceof ColorDrawable) {
            mBackgroundPaint.setColor(((ColorDrawable) background).getColor());
            drawShape(canvas);
        }
        drawText(canvas);
    }

    private void drawShape(Canvas canvas) {
        if (mRadius > 0) {
            canvas.drawRoundRect(mBackgroundRect, mRadius, mRadius, mBackgroundPaint);
        } else {
            canvas.drawRect(mBackgroundRect, mBackgroundPaint);
        }
    }

    private void drawBitmap(Canvas canvas, BitmapDrawable background) {
        mBackgroundPaint.setXfermode(mSrcInMode);
        canvas.drawBitmap(background.getBitmap(), null, mBackgroundRect, mBackgroundPaint);
        mBackgroundPaint.setXfermode(null);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        canvas.drawText(mText, (getMeasuredWidth() - mTextRect.width()) / 2,
                (getMeasuredHeight() - mFontMetrics.top - mFontMetrics.bottom) / 2, mTextPaint);
    }

}