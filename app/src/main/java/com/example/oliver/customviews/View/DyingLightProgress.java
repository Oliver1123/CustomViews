package com.example.oliver.customviews.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import com.example.oliver.customviews.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by oliver on 09.12.15.
 */
public class DyingLightProgress extends View {
    private final int ANIMATION_PART_DURATION = 500;
    public static final int ITEM_TYPE_SQUARE      = 0;
    public static final int ITEM_TYPE_CIRCLE      = 1;
    public static final int ITEM_TYPE_DRAWABLE    = 2;

    private Paint mLinesPaint, mAnimatedPaint;
    private PlaceHolder mCentralPlaceHolder;
    private AnimationManager mAnimationManager;
    private List<PlaceHolder> mItems;

    private int mColor;
    private float mItemWidth, mItemHeight;
    private int mItemType;
    private Bitmap mIcon;

    public DyingLightProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DyingLightProgress,
                0, 0
        );
        try {
            mColor  = a.getColor(R.styleable.DyingLightProgress_itemColor, Color.BLACK);
            mItemWidth = a.getDimension(R.styleable.DyingLightProgress_itemWidth,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
            mItemHeight = a.getDimension(R.styleable.DyingLightProgress_itemHeight,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));

            mItemType = a.getInt(R.styleable.DyingLightProgress_itemType, ITEM_TYPE_SQUARE);
        } finally {
            a.recycle();
        }
    }

    public void setIcon (Resources _res, int _id) {
        setIcon(BitmapFactory.decodeResource(_res, _id));
    }

    public void setIcon(Bitmap icon) {
//        mIcon = Bitmap.createScaledBitmap(icon, (int) mItemWidth, (int) mItemHeight, true);
        mIcon = icon;
        mItemType = ITEM_TYPE_DRAWABLE;
    }

    public void setItemType (int _itemType) {
        mItemType = _itemType;
    }
    private void initAnimation() {
        mCentralPlaceHolder = new PlaceHolder(getWidth() / 2 - mItemWidth * 2, getWidth() / 2 - mItemHeight * 2,
                        mItemWidth * 4, mItemHeight * 4);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofInt(mAnimatedPaint, "alpha", 100, 200);
        alphaAnimation.setDuration(250);
        alphaAnimation.setRepeatCount(5);
        alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);


        mItems = createItems(getWidth(), getHeight(), mItemWidth, mItemHeight);

        List<AnimatorSet> animationTo = new ArrayList<>();
        List<AnimatorSet> animationFrom = new ArrayList<>();

        for (PlaceHolder placeHolder : mItems) {
            animationTo.add(createAnimTraceTo(placeHolder, ANIMATION_PART_DURATION));
            animationFrom.add(createAnimTraceFrom(placeHolder, ANIMATION_PART_DURATION));
        }

        mAnimationManager = new AnimationManager(animationFrom, animationTo, alphaAnimation);
        mAnimationManager.startAnimation();

    }

    private List<PlaceHolder> createItems(float _areaWidth, float _areaHeight, float _itemWidth, float _itemHeight) {

        List<PlaceHolder> result = new ArrayList<>();

        float areaCenterX = _areaWidth / 2;
        float areaCenterY = _areaHeight / 2;
        float itemHalfHeight = _itemHeight / 2;
        float itemHalfWidth = _itemWidth / 2;

        PlaceHolder placeHolder = null;

//                      TopLeft
        placeHolder = new PlaceHolder(0, 0, _itemWidth, _itemHeight);
        placeHolder.setTrackPoints(
                new Pair<>(placeHolder.getLeft(), placeHolder.getTop()),
                new Pair<>(placeHolder.getLeft(), areaCenterY - itemHalfHeight),
                new Pair<>(areaCenterX - itemHalfWidth, areaCenterY - itemHalfHeight)
        );
        result.add(placeHolder);

//                      BottomLeft
        placeHolder = new PlaceHolder(0, getHeight() - _itemHeight, _itemWidth, _itemHeight);
        placeHolder.setTrackPoints(
                new Pair<>(placeHolder.getLeft(), placeHolder.getTop()),
                new Pair<>(areaCenterX - itemHalfWidth, placeHolder.getTop()),
                new Pair<>(areaCenterX - itemHalfWidth, areaCenterY - itemHalfHeight)
        );
        result.add(placeHolder);

//                      BottomRight
        placeHolder = new PlaceHolder(getWidth() - _itemWidth, getHeight() - _itemHeight, _itemWidth, _itemHeight);
        placeHolder.setTrackPoints(
                new Pair<>(placeHolder.getLeft(), placeHolder.getTop()),
                new Pair<>(placeHolder.getLeft(), areaCenterY - itemHalfHeight),
                new Pair<>(areaCenterX - itemHalfWidth, areaCenterY - itemHalfHeight)
        );
        result.add(placeHolder);

//                      TopRight
        placeHolder = new PlaceHolder(getWidth() - _itemWidth, 0, _itemWidth, _itemHeight);
        placeHolder.setTrackPoints(
                new Pair<>(placeHolder.getLeft(), placeHolder.getTop()),
                new Pair<>(areaCenterX - itemHalfWidth, placeHolder.getTop()),
                new Pair<>(areaCenterX - itemHalfWidth, areaCenterY - itemHalfHeight)
        );
        result.add(placeHolder);

        return result;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int _color) {
        mColor = _color;
        invalidate();
        requestLayout();
    }

    private void init() {
        mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinesPaint.setColor(mColor);
        mLinesPaint.setStrokeWidth(2);

        mAnimatedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimatedPaint.setColor(mColor);
        mAnimatedPaint.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        initAnimation();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mAnimationManager.collapsed) {
            PlaceHolder placeHolder = mItems.get(0);
            drawItem(canvas, placeHolder, mAnimatedPaint);
        } else {
            for (int i = 0; i < mItems.size(); i++) {
                PlaceHolder placeHolderFrom = mItems.get(i);
                for (int j = i + 1; j < mItems.size() ; j++) {
                    PlaceHolder placeHolderTo = mItems.get(j);
                    canvas.drawLine(placeHolderFrom.getCenterX(), placeHolderFrom.getCenterY(),
                            placeHolderTo.getCenterX(), placeHolderTo.getCenterY(), mLinesPaint);
                }
                drawItem(canvas, placeHolderFrom, mLinesPaint);
            }

            drawItem(canvas, mCentralPlaceHolder, mAnimatedPaint);
        }
    }

    protected void drawItem(Canvas _canvas, PlaceHolder _holder, Paint _paint) {
        switch (mItemType) {
            case ITEM_TYPE_SQUARE:
                _canvas.drawRect(_holder.getLeft(), _holder.getTop(),
                        _holder.getRight(), _holder.getBottom(), _paint);
                break;
            case ITEM_TYPE_CIRCLE:
                _canvas.drawCircle(_holder.getCenterX(), _holder.getCenterY(), _holder.getWidth() / 2, _paint);
                break;
            case ITEM_TYPE_DRAWABLE:
                _canvas.drawBitmap(mIcon, null,
                        new Rect((int) _holder.getLeft(), (int) _holder.getTop(), (int) _holder.getRight(), (int) _holder.getBottom()), _paint);
                break;
        }
    }

    protected AnimatorSet createAnimTraceTo(PlaceHolder _target,int _partDuration) {
        List<ObjectAnimator> traceX = new ArrayList<>();
        List<ObjectAnimator> traceY = new ArrayList<>();

        AnimatorSet result = new AnimatorSet();

        Pair<Float, Float> pos0 = _target.getTrackPoints().get(0);

        for (int i = 1; i < _target.getTrackPoints().size(); i++) {
            Pair<Float, Float> pos = _target.getTrackPoints().get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(_target, "left", pos0.first, pos.first);
            animatorX.setDuration(_partDuration);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(_target, "top", pos0.second, pos.second);
            animatorY.setDuration(_partDuration);

            traceX.add(animatorX);
            traceY.add(animatorY);
            pos0 = pos;
        }
        result.play(traceX.get(0)).with(traceY.get(0));
        for (int i = 1; i < traceX.size(); i++) {
            result.play(traceX.get(i)).with(traceY.get(i));
            result.play(traceX.get(i)).after(traceX.get(i - 1));
        }
        return result;
    }
    protected AnimatorSet createAnimTraceFrom(PlaceHolder _target, int _partDuration) {
        List<ObjectAnimator> traceX = new ArrayList<>();
        List<ObjectAnimator> traceY = new ArrayList<>();
        List<Pair<Float, Float>> trackPoints = _target.getTrackPoints();

        Pair<Float, Float> posN = trackPoints.get(trackPoints.size() - 1);

        for (int i = trackPoints.size() - 2; i >= 0; i--) {
            Pair<Float, Float> pos = trackPoints.get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(_target, "left", posN.first, pos.first);
            animatorX.setDuration(_partDuration);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(_target, "top", posN.second, pos.second);
            animatorY.setDuration(_partDuration);

            traceX.add(animatorX);
            traceY.add(animatorY);
            posN = pos;
        }

        AnimatorSet result = new AnimatorSet();
        result.play(traceX.get(0)).with(traceY.get(0));
        for (int i = 1; i < traceX.size(); i++) {
            result.play(traceX.get(i)).with(traceY.get(i));
            result.play(traceX.get(i)).after(traceX.get(i - 1));
        }
        return result;
    }

    final class PlaceHolder {
        private float mWidth, mHeight;
        private float mTop, mLeft;
        private List<Pair<Float, Float>> mTrackPoints;

        public PlaceHolder(float _left, float _top, float _width, float _height) {
            mLeft = _left;
            mTop = _top;
            mWidth = _width;
            mHeight = _height;
        }


        public float getWidth() {
            return mWidth;
        }

        public float getHeight() {
            return mHeight;
        }

        public float getTop() {
            return mTop;
        }

        public void setTop (float _top) {
            mTop = _top;
        }

        public float getLeft() {
            return mLeft;
        }

        public void setLeft (float _left) {
            mLeft = _left;
        }

        public float getRight() {
            return mLeft + mWidth;
        }

        public float getBottom() {
            return mTop + mHeight;
        }

        public float getCenterX() {
            return mLeft + mWidth / 2;
        }

        public float getCenterY() {
            return mTop + mHeight / 2;
        }


        public List<Pair<Float, Float>> getTrackPoints() {
            return mTrackPoints;
        }

        public void setTrackPoints(List<Pair<Float, Float>> _trackPoints) {
            mTrackPoints = _trackPoints;
        }

        public void setTrackPoints(Pair<Float, Float>... points) {
            mTrackPoints = Arrays.asList(points);
        }
    }

    private class AnimationManager {
        private List<AnimatorSet> mAnimationFrom, mAnimationTo;
        private ObjectAnimator mPaintAnim;
        private Paint mAnimatedPaint;
        private boolean collapsed;

        public AnimationManager(List<AnimatorSet> _animationFrom, List<AnimatorSet> _animationTo, ObjectAnimator _paintAnim) {
            mAnimationFrom = _animationFrom;
            mAnimationTo = _animationTo;
            mPaintAnim = _paintAnim;
            mAnimatedPaint = (Paint) mPaintAnim.getTarget();

            mPaintAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    endPaintAnimation();
                }
            });
        }


        private void startAnimationTo(int delayBetweenItems) {
            mAnimatedPaint.setAlpha(0);
            for (int i = 0; i < mAnimationTo.size(); i++) {
                AnimatorSet anim = mAnimationTo.get(i);
                anim.setStartDelay(i*delayBetweenItems);
                anim.start();
            }

            mAnimationTo.get(mAnimationTo.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
                    endAnimationTo();
                }
            });
        }

        private void startAnimationFrom(int delayBetweenItems) {
            mAnimatedPaint.setAlpha(0);
            for (int i = 0; i < mAnimationFrom.size(); i++) {
                AnimatorSet anim = mAnimationFrom.get(i);
                anim.setStartDelay(i*delayBetweenItems);
                anim.start();
            }

            mAnimationFrom.get(mAnimationFrom.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    endAnimationFrom();
                }
            });
        }

        private void endAnimationTo(){
            collapsed = true;
            mPaintAnim.start();
        }

        private void endAnimationFrom() {
            collapsed = false;
            mPaintAnim.start();
        }


        private void endPaintAnimation() {
            if (collapsed) {
                collapsed = false;
                startAnimationFrom(250);
            } else {
                startAnimationTo(250);
            }
        }

        public void startAnimation() {
            final Handler h = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    h.postDelayed(this, 10);
                }
            };
            r.run();
            mPaintAnim.start();
        }

    }
}
