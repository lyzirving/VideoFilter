package com.lyzirving.test.videofilter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author lyzirving
 *         time        2018/10/15
 *         email       lyzirving@sina.com
 *         information
 */

public class VideoViewContainer extends FrameLayout {

    private RectF mRenderRect;
    private ImageView mSticker;
    private float mStartX, mStartY;
    private float mShiftX, mShiftY;
    private float mStickerOriginalLeft, mStickerOriginalTop;
    private int mStickerW, mStickerH;

    public VideoViewContainer(@NonNull Context context) {
        this(context, null);
    }

    public VideoViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildAt(0) instanceof TextureView) {
            ViewGroup.LayoutParams lp = getChildAt(0).getLayoutParams();
            int childW = lp.width;
            int childH = lp.height;
            mRenderRect = new RectF((w - childW) / 2f, (h - childH) / 2f, (w - childW) / 2f + childW,
                    (h - childH) / 2f + childH);
        } else {
            mRenderRect = new RectF(0, 0, w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(mRenderRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!hasSticker()) {
            return super.onTouchEvent(event);
        }
        return dealWithSticker(event);
    }

    private boolean hasSticker() {
        boolean result = false;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView) {
                result = child.getVisibility() == VISIBLE ? true : false;
                break;
            }
        }
        return result;
    }

    private boolean dealWithSticker(MotionEvent e) {
        if (mSticker == null) {
            mSticker = getSticker();
            mStickerW = mSticker.getMeasuredWidth();
            mStickerH = mSticker.getMeasuredHeight();
            mStickerOriginalLeft = mSticker.getLeft();
            mStickerOriginalTop = mSticker.getTop();
        }
        boolean result = true;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = e.getX();
                mStartY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mShiftX += e.getX() - mStartX;
                mShiftY += e.getY() - mStartY;
                mStartX = e.getX();
                mStartY = e.getY();
                limitTranslation();
                mSticker.setTranslationX(mShiftX);
                mSticker.setTranslationY(mShiftY);
                break;
            case MotionEvent.ACTION_UP:
              break;
        }
        return result;
    }

    private void limitTranslation() {
        float currentLeft = mStickerOriginalLeft + mShiftX;
        float currentTop = mStickerOriginalTop + mShiftY;
        float currentRight = currentLeft + mStickerW;
        float currentBottom = currentTop + mStickerH;
        if (currentLeft < mRenderRect.left) {
            mShiftX = mRenderRect.left - mStickerOriginalLeft;
        } else if (currentRight > mRenderRect.right) {
            mShiftX = mRenderRect.right - mStickerOriginalLeft - mStickerW;
        }
        if (currentTop < mRenderRect.top) {
            mShiftY = mRenderRect.top - mStickerOriginalTop;
        } else if (currentBottom > mRenderRect.bottom) {
            mShiftY = mRenderRect.bottom - mShiftY - mStickerH;
        }
    }

    private ImageView getSticker() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView) {
                return (ImageView) child;
            }
        }
        return null;
    }

}
