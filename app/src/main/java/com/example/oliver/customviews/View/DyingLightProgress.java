package com.example.oliver.customviews.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.example.oliver.customviews.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by oliver on 09.12.15.
 */
public class DyingLightProgress extends View {

    private int mLinesColor  = Color.BLACK;
    private Paint mLinesPaint, mAnimatedPaint;
    private PlaceHolder mCentralPlaceHolder;
    private AnimationManager mAnimationManager;
    private List<PlaceHolder> mItems;

    public DyingLightProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieMenuView,
                0, 0
        );
        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
//            mLinesColor         = a.getColor(R.styleable.DyingLightProgress_linesColor, Color.BLACK);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
    }

    private void initAnimation() {
        int itemWidth = getWidth() / 10;
        int itemHeight = getHeight() / 10;
        int partDuration = 500;

        mCentralPlaceHolder = new PlaceHolder(getWidth() / 2 - itemWidth * 2, getWidth() / 2 - itemHeight * 2,
                        itemWidth * 4, itemHeight * 4);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofInt(mAnimatedPaint, "alpha", 250, 100);
        alphaAnimation.setDuration(250);
        alphaAnimation.setRepeatCount(5);
        alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);


        mItems = createItems(getWidth(), getHeight(), itemWidth, itemHeight);

        List<AnimatorSet> animationTo = new ArrayList<>();
        List<AnimatorSet> animationFrom = new ArrayList<>();

        for (PlaceHolder placeHolder : mItems) {
            animationTo.add(createAnimTraceTo(placeHolder, partDuration));
            animationFrom.add(createAnimTraceFrom(placeHolder, partDuration));
        }

        mAnimationManager = new AnimationManager(animationFrom, animationTo, alphaAnimation);
        mAnimationManager.startAnimation();

    }

    private List<PlaceHolder> createItems(int _areaWidth, int _areaHeight, int _itemWidth, int _itemHeight) {

        List<PlaceHolder> result = new ArrayList<>();

        int areaCenterX = _areaWidth / 2;
        int areaCenterY = _areaHeight / 2;
        int itemHalfHeight = _itemHeight / 2;
        int itemHalfWidth = _itemWidth / 2;

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

    public int getLinesColor() {
        return mLinesColor;
    }

    public void setLinesColor(int _linesColor) {
        mLinesColor = _linesColor;
        invalidate();
        requestLayout();
    }

    private void init() {
//        Log.d("tag", "init w: " + getWidth() + " h: " + getHeight());
        mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinesPaint.setColor(mLinesColor);
        mLinesPaint.setStrokeWidth(2);

        mAnimatedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimatedPaint.setColor(mLinesColor);
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

    private void drawItem(Canvas _canvas, PlaceHolder _holder, Paint _paint) {
        _canvas.drawRect(_holder.getLeft(), _holder.getTop(),
                _holder.getRight(), _holder.getBottom(), _paint);
//        _canvas.drawCircle(_holder.getCenterX(), _holder.getCenterY(), _holder.getWidth() / 2, _paint);
//        _paint.setStyle(Paint.Style.STROKE);
//        _paint.setStrokeWidth(4);
//        _canvas.drawArc(new RectF(_holder.getLeft(), _holder.getTop(), _holder.getRight(), _holder.getBottom()), 0, 360, true, _paint );
//        _paint.setStrokeWidth(2);
    }

    protected AnimatorSet createAnimTraceTo(PlaceHolder _target,int _partDuration) {
        List<ObjectAnimator> traceX = new ArrayList<>();
        List<ObjectAnimator> traceY = new ArrayList<>();

        AnimatorSet result = new AnimatorSet();

        Pair<Integer, Integer> pos0 = _target.getTrackPoints().get(0);

        for (int i = 1; i < _target.getTrackPoints().size(); i++) {
            Pair<Integer, Integer> pos = _target.getTrackPoints().get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofInt(_target, "left", pos0.first, pos.first);
            animatorX.setDuration(_partDuration);
            ObjectAnimator animatorY = ObjectAnimator.ofInt(_target, "top", pos0.second, pos.second);
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
        List<Pair<Integer, Integer>> trackPoints = _target.getTrackPoints();

        Pair<Integer, Integer> posN = trackPoints.get(trackPoints.size() - 1);

        for (int i = trackPoints.size() - 2; i >= 0; i--) {
            Pair<Integer, Integer> pos = trackPoints.get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofInt(_target, "left", posN.first, pos.first);
            animatorX.setDuration(_partDuration);
            ObjectAnimator animatorY = ObjectAnimator.ofInt(_target, "top", posN.second, pos.second);
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

    private class PlaceHolder {
        private int mWidth, mHeight;
        private int mTop, mLeft;
        private List<Pair<Integer, Integer>> mTrackPoints;

        public PlaceHolder(int _left, int _top, int _width, int _height) {
            mLeft = _left;
            mTop = _top;
            mWidth = _width;
            mHeight = _height;
        }


        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        public int getTop() {
            return mTop;
        }

        public void setTop (int _top) {
            mTop = _top;
        }

        public int getLeft() {
            return mLeft;
        }

        public void setLeft (int _left) {
            mLeft = _left;
        }

        public int getRight() {
            return mLeft + mWidth;
        }

        public int getBottom() {
            return mTop + mHeight;
        }

        public int getCenterX() {
            return mLeft + mWidth / 2;
        }

        public int getCenterY() {
            return mTop + mHeight / 2;
        }


        public List<Pair<Integer, Integer>> getTrackPoints() {
            return mTrackPoints;
        }

        public void setTrackPoints(List<Pair<Integer, Integer>> _trackPoints) {
            mTrackPoints = _trackPoints;
        }

        public void setTrackPoints(Pair<Integer, Integer>... points) {
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
