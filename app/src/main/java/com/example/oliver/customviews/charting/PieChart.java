package com.example.oliver.customviews.charting;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.example.oliver.customviews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view that shows a pie chart and, optionally, a label.
 */
public class PieChart extends ViewGroup {
    private List<Item> mData = new ArrayList<Item>();


    private RectF mPieBounds = new RectF();

    private Paint mPiePaint;
    private Paint mLinesPaint;

    private int mPieRotation;

    private OnSelectedItemChangeListener mSelectedItemChangeListener = null;
    private OnItemCLickListener mItemClickListener= null;

    private PieView mPieView;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private GestureDetector mDetector;
//    private PointerView mPointerView;

    // The angle at which we measure the current item.
//    private int mCurrentItemAngle;

    // the index of the current item.
    private int mSelectedItem = -1;
    private boolean mAutoCenterInSlice;
    private ObjectAnimator mAutoCenterAnimator;

    //////////////////////////
    public static int PIE_STYLE_FULL    = 0;
    public static int PIE_STYLE_HALF    = 1;
    public static int PIE_STYLE_QUARTER = 2;
    private int mLinesColor;
    private int mSegmentsColor;
    private int mSelectedItemColor;
    private float mLinesWidth;
    private int mPieStyle;
    private float mInnerRadius;
    private float mIconWidth;
    private float mIconHeight;

    //////////////////////////
    /**
     * The initial fling velocity is divided by this amount.
     */
    public static final int FLING_VELOCITY_DOWNSCALE = 4;

    public static final int AUTOCENTER_ANIM_DURATION = 250;


    /**
     * Interface definition for a callback to be invoked when the current
     * item changes.
     */
    public interface OnSelectedItemChangeListener {
        void OnSelectedItemChange(PieChart source, int currentItem);
    }

    public interface OnItemCLickListener {
        void OnItemCLick(PieChart source, int currentItem);
    }

    /**
     * Class constructor taking only a context. Use this constructor to create
     * {@link PieChart} objects from your own code.
     *
     * @param context
     */
    public PieChart(Context context) {
        super(context);
        Log.d("tag", "PieChart constructor context");
        init();
    }

    /**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a {@link PieChart} from a set of
     * XML attributes.
     *
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     *                from {@link View}.
     */
    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.d("tag", "PieChart constructor context, attrs");
        // attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.PieChart, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieChart,
                0, 0
        );
            Log.d("tag", a.toString());
        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.

            mAutoCenterInSlice  = a.getBoolean(R.styleable.PieChart_autoCenterPointerInSlice, false);
            mLinesWidth         = a.getDimension(R.styleable.PieChart_linesWidth, 5.0f);
            mLinesColor         = a.getColor(R.styleable.PieChart_linesColor, Color.BLACK);
            mSegmentsColor      = a.getColor(R.styleable.PieChart_segmentsColor, Color.YELLOW);
            mSelectedItemColor  = a.getColor(R.styleable.PieChart_selectedItemColor, Color.RED);

            mPieStyle = a.getInteger(R.styleable.PieChart_pieStyle, 0);
            mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0);
            mInnerRadius = a.getDimension(R.styleable.PieChart_innerRadius, 60.0f);
            mIconWidth = a.getDimension(R.styleable.PieChart_iconWidth, 120.0f);
            mIconHeight = a.getDimension(R.styleable.PieChart_iconHeight, 120.0f);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }


    public int getLinesColor() {
        return mLinesColor;
    }

    public void setLinesColor(int linesColor) {
        mLinesColor = linesColor;
        invalidate();
        requestLayout();
    }

    public int getSegmentsColor() {
        return mSegmentsColor;
    }

    public void setSegmentsColor(int segmentsColor) {
        mSegmentsColor = segmentsColor;
        invalidate();
        requestLayout();
    }
    public int getSelectedItemColor() {
        return mSelectedItemColor;
    }

    public void setSelectedItemColor(int selectedItemColor) {
        mSelectedItemColor = selectedItemColor;
        invalidate();
        requestLayout();
    }

    public float getLinesWidth() {
        return mLinesWidth;
    }

    public void setLinesWidth(float linesWidth) {
        mLinesWidth = linesWidth;
        invalidate();
        requestLayout();
    }

    public int getPieStyle() {
        return mPieStyle;
    }

    public void setPieStyle(int pieStyle) {
        if (pieStyle != PIE_STYLE_FULL && pieStyle != PIE_STYLE_HALF && pieStyle != PIE_STYLE_QUARTER) {
            throw new IllegalArgumentException(
                    "PieStyle must be one of PIE_STYLE_FULL, PIE_STYLE_HALF or PIE_STYLE_QUARTER");
        }
        mPieStyle = pieStyle;
        invalidate();
        requestLayout();

    }

    public float getInnerRadius() {
        return mInnerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        if (innerRadius < 0)
            throw new IllegalArgumentException("InnerRadius cannot be negative");
        mInnerRadius = innerRadius;
        invalidate();
        requestLayout();
    }


    public float getIconWidth() {
        return mIconWidth;
    }

    public void setIconWidth(float iconWidth) {
        if (iconWidth < 0)
            throw new IllegalArgumentException("IconWidth cannot be negative");
        mIconWidth = iconWidth;
        invalidate();
        requestLayout();
    }

    public float getIconHeight() {
        return mIconHeight;
    }

    public void setIconHeight(float iconHeight) {
        if (iconHeight < 0)
            throw new IllegalArgumentException("IconHeight cannot be negative");
        mIconHeight = iconHeight;
        invalidate();
        requestLayout();
    }

    /**
     * Returns the current rotation of the pie graphic.
     *
     * @return The current pie rotation, in degrees.
     */
    public int getPieRotation() {
        return mPieRotation;
    }

    /**
     * Set the current rotation of the pie graphic. Setting this value may change
     * the current item.
     *
     * @param rotation The current pie rotation, in degrees.
     */
    public void setPieRotation(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        mPieRotation = rotation;
        mPieView.rotateTo(rotation);

//        calcSelectedItem();
    }

    /**
     * Returns the index of the currently selected data item.
     *
     * @return The zero-based index of the currently selected data item.
     */
    public int getSelectedItem() {
        return mSelectedItem;
    }

    /**
     * Set the selected item. Calling this function will select item
     * and rotate the pie to bring it into view.
     *
     * @param selectedItem The zero-based index of the item to select.
     */
    public void setSelectedItem(int selectedItem) {
        setSelectedItem(selectedItem, true);
    }

    /**
     * Set the selected  item by index. Optionally, scroll the selected item into view. This version
     * is for internal use--the scrollIntoView option is always true for external callers.
     *
     * @param selectedItem    The index of the selected item.
     * @param scrollIntoView True if the pie should rotate until the selected item is centered.
     *                       False otherwise. If this parameter is false, the pie rotation
     *                       will not change.
     */
    private void setSelectedItem(int selectedItem, boolean scrollIntoView) {
        Log.d("tag", "setSelectedItem: " + selectedItem);
        mSelectedItem = selectedItem;
        if (mSelectedItemChangeListener != null) {
            mSelectedItemChangeListener.OnSelectedItemChange(this, selectedItem);
        }
        if (scrollIntoView) {
//            centerOnCurrentItem();
        }
        invalidate();
        requestLayout();
    }


    /**
     * Register a callback to be invoked when the currently selected item changes.
     *
     * @param listener Can be null.
     *                 The current item changed listener to attach to this view.
     */
    public void setOnSelectedItemChangeListener(OnSelectedItemChangeListener listener) {
        mSelectedItemChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when click the item changes.
     *
     * @param listener Can be null.
     *                 The Item Click listener to attach to this view.
     */
    public void setItemClickListener(OnItemCLickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Add a new data item to this view. Adding an item adds a slice to the pie whose
     * size is proportional to the item's value. As new items are added, the size of each
     * existing slice is recalculated so that the proportions remain correct.
     *
     * return The index of the newly added item.
     */
    public int addItem(Bitmap icon) {
        Item it = new Item();
        it.mIcon = Bitmap.createScaledBitmap(icon, (int)mIconWidth, (int)mIconHeight, true);

        mData.add(it);

        onDataChanged();

        return mData.size() - 1;
    }

    public int addItem(int iconID) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), iconID);
        return addItem(icon);
    }
    public int addItem(String iconFileName) {
        Bitmap icon = BitmapFactory.decodeFile(iconFileName);
        return addItem(icon);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpret this event
        boolean result = mDetector.onTouchEvent(event);

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                stopScrolling();
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. PieChart lays out its children in onSizeChanged().
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("tag", "PieChart onDraw");
//        mPieView.invalidate();
    }


    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) (mInnerRadius + getIconWidth() + mLinesWidth) * 2;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) (mInnerRadius + getIconHeight() + mLinesWidth) * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int h = Math.max(minh, MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        float diameter = Math.min(ww, hh);
        mPieBounds = new RectF(
                0.0f,
                0.0f,
                diameter,
                diameter);
        mPieBounds.offsetTo(getPaddingLeft(), getPaddingTop());


        // Lay out the child view that actually draws the pie.
        mPieView.layout((int) mPieBounds.left,
                (int) mPieBounds.top,
                (int) mPieBounds.right,
                (int) mPieBounds.bottom);

        mPieView.setPivot(mPieBounds.width() / 2, mPieBounds.height() / 2);

        onDataChanged();
    }

    /**
     * Do all of the recalculations needed when the data array changes.
     */
    private void onDataChanged() {
        // When the data changes, we have to recalculate
        // all of the angles.
        int currentAngle = 0;
        int itemAngle = Math.round(360.0f / mData.size());
        for (int i = 0; i < mData.size(); i++) {
            Item it = mData.get(i);
            // change item angles
            it.mStartAngle = currentAngle;
            it.mEndAngle = it.mStartAngle + itemAngle;
            currentAngle = it.mEndAngle;
//            Log.d("tag", "it[" + i + "] sA:" + it.mStartAngle + ", eA: " + it.mEndAngle);
            // where put the icon
            it.mCenterAngle = it.mStartAngle  + (it.mEndAngle - it.mStartAngle) / 2;
        }
        // if there are free space in chart
        mData.get(mData.size() - 1).mEndAngle = 360;
//        calcSelectedItem();
        onScrollFinished();
    }

    /**
     * Initialize the control. This code is in a separate method so that it can be
     * called from both constructors.
     */
    private void init() {
        // Force the background to software rendering because otherwise the Blur
        // filter won't work.
        setLayerToSW(this);

        // Set up the paint for the pie slices
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPiePaint.setStrokeWidth(mLinesWidth);
        mPiePaint.setColor(mSegmentsColor);

        // Set up the paint for the lines
        mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setColor(mLinesColor);
        mLinesPaint.setStrokeWidth(mLinesWidth);

        // Set up the paint for the shadow
        // todo paint for shadow

        // Add a child view to draw the pie. Putting this in a child view
        // makes it possible to draw it on a separate hardware layer that rotates
        // independently
        mPieView = new PieView(getContext());
        addView(mPieView);
        mPieView.rotateTo(mPieRotation);

        // Set up an animator to animate the PieRotation property. This is used to
        // correct the pie's orientation after the user lets go of it.
        if (Build.VERSION.SDK_INT >= 11) {
            mAutoCenterAnimator = ObjectAnimator.ofInt(PieChart.this, "PieRotation", 0);

            // Add a listener to hook the onAnimationEnd event so that we can do
            // some cleanup when the pie stops moving.
            mAutoCenterAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    mPieView.decelerate();
                }

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }
            });
        }


        // Create a Scroller to handle the fling gesture.

            mScroller = new Scroller(getContext(), null, true);

        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        if (Build.VERSION.SDK_INT >= 11) {
            mScrollAnimator = ValueAnimator.ofFloat(0, 1);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    tickScrollAnimation();
                }
            });
        }

        // Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(PieChart.this.getContext(), new GestureListener());

        // Turn off long press--this control doesn't use it, and if long press is enabled,
        // you can't scroll for a bit, pause, then scroll some more (the pause is interpreted
        // as a long press, apparently)
        mDetector.setIsLongpressEnabled(false);


        // In edit mode it's nice to have some demo data, so add that here.
        if (this.isInEditMode()) {
            addItem(R.mipmap.ic_launcher);
            addItem(R.mipmap.ic_launcher);
            addItem(R.mipmap.ic_launcher);
        }
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            setPieRotation(mScroller.getCurrY());
        } else {
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator.cancel();
            }
            onScrollFinished();
        }
    }

    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    /**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        mScroller.forceFinished(true);
        if (Build.VERSION.SDK_INT >= 11) {
            mAutoCenterAnimator.cancel();
        }

        onScrollFinished();
    }

    /**
     * Called when the user finishes a scroll action.
     */
    private void onScrollFinished() {
        if (mAutoCenterInSlice) {
//            centerOnCurrentItem();
        } else {
            mPieView.decelerate();
        }
    }

    /**
     * Kicks off an animation that will result in the pointer being centered in the
     * pie slice of the currently selected item.
     */
//    private void centerOnCurrentItem() {
//        Item current = mData.get(getSelectedItem());
//        int targetAngle = current.mStartAngle + (current.mEndAngle - current.mStartAngle) / 2;
//        targetAngle -= mCurrentItemAngle;
//        if (targetAngle < 90 && mPieRotation > 180) targetAngle += 360;
//
//        if (Build.VERSION.SDK_INT >= 11) {
//            // Fancy animated version
//            mAutoCenterAnimator.setIntValues(targetAngle);
//            mAutoCenterAnimator.setDuration(AUTOCENTER_ANIM_DURATION).start();
//        } else {
//            // Dull non-animated version
////            mPieView.rotateTo(targetAngle);
//        }
//    }

    /**
     * Internal child class that draws the pie chart onto a separate hardware layer
     * when necessary.
     */
    private class PieView extends View {
        // Used for SDK < 11
        private PointF mPivot = new PointF();

        /**
         * Construct a PieView
         *
         * @param context
         */
        public PieView(Context context) {
            super(context);
        }

        /**
         * Enable hardware acceleration (consumes memory)
         */
        public void accelerate() {
            setLayerToHW(this);
        }

        /**
         * Disable hardware acceleration (releases memory)
         */
        public void decelerate() {
            setLayerToSW(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d("tag", "PieView onDraw");
            mPiePaint.setColor(mSegmentsColor);

            mLinesPaint.setStyle(Paint.Style.STROKE);
            mLinesPaint.setColor(mLinesColor);
            mLinesPaint.setStrokeWidth(mLinesWidth);

            for (int i = 0; i < mData.size(); i++) {
                // draw slice
                Item it = mData.get(i);
                canvas.drawArc(mPieBounds,
                        180 + it.mStartAngle,
                        it.mEndAngle - it.mStartAngle,
                        true, mPiePaint);

                // draw icon
                it.mMatrix.reset();
                it.mMatrix.setRotate(it.mCenterAngle, mPieBounds.centerX(), mPieBounds.centerY());
                it.mMatrix.preTranslate((mPieBounds.centerX() - mInnerRadius) / 2 - it.mIcon.getWidth() / 2,
                        mPieBounds.centerY() - it.mIcon.getHeight() / 2);

                Bitmap rotatedIcon  = rotateBitmap(it.mIcon, -90);
                canvas.drawBitmap(rotatedIcon, it.mMatrix, mPiePaint);


                // draw lines around slice
                canvas.drawArc(mPieBounds,
                        180 + it.mStartAngle,
                        it.mEndAngle - it.mStartAngle,
                        true, mLinesPaint);
            }
            Log.d("tag", "selectedItem: " + getSelectedItem());
            // draw selected Item if exist
            if (getSelectedItem() != -1) {
                Item it = mData.get(getSelectedItem());
                mPiePaint.setColor(mSelectedItemColor);
                canvas.drawArc(mPieBounds,
                        180 + it.mStartAngle,
                        it.mEndAngle - it.mStartAngle,
                        true, mPiePaint);
                // draw icon
                it.mMatrix.reset();
                it.mMatrix.setRotate(it.mCenterAngle, mPieBounds.centerX(), mPieBounds.centerY());
                it.mMatrix.preTranslate((mPieBounds.centerX() - mInnerRadius) / 2 - it.mIcon.getWidth() / 2,
                        mPieBounds.centerY() - it.mIcon.getHeight() / 2);

                Bitmap rotatedIcon  = rotateBitmap(it.mIcon, -90);
                canvas.drawBitmap(rotatedIcon, it.mMatrix, mPiePaint);
            }

            // draw circle in Center
            mLinesPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mCenterBounds, 0, 360, false, mLinesPaint);


            mLinesPaint.setStyle(Paint.Style.STROKE);
            mLinesPaint.setColor(mSelectedItemColor);
            mLinesPaint.setStrokeWidth(4 * mLinesWidth);
            canvas.drawArc(mCenterBounds, 0, 360, false, mLinesPaint);

//             draw outer border
            mLinesPaint.setColor(mLinesColor);
            mLinesPaint.setStrokeWidth(2 * mLinesWidth);
            canvas.drawArc(mPieBounds, 0, 360, false, mLinesPaint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            int linesWidth = (int) mLinesWidth;
            mPieBounds = new RectF(linesWidth, linesWidth, w - linesWidth, h - linesWidth);
            int centerX = w / 2;
            int centerY = h / 2;
            mCenterBounds = new RectF(centerX - mInnerRadius, centerY - mInnerRadius,
                                        centerX + mInnerRadius, centerY + mInnerRadius);

        }

        RectF mPieBounds, mCenterBounds;


        public void rotateTo(float pieRotation) {
            if (Build.VERSION.SDK_INT >= 11) {
                setRotation(pieRotation);
            } else {
                invalidate();
            }
        }

        public void setPivot(float x, float y) {
            mPivot.x = x;
            mPivot.y = y;
            if (Build.VERSION.SDK_INT >= 11) {
                setPivotX(x);
                setPivotY(y);
            } else {
                invalidate();
            }
        }
    }


    /**
     * Maintains the state for a data item.
     */
    private class Item {
        public Bitmap mIcon;

        // computed values
        public int mStartAngle;
        public int mEndAngle;
        public int mCenterAngle;
        public Matrix mMatrix = new Matrix();

    }

    /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom gesture
     * processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Set the pie rotation directly.
            float scrollTheta = vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.getX() - mPieBounds.centerX(),
                    e2.getY() - mPieBounds.centerY());
            setPieRotation(getPieRotation() - (int) scrollTheta / FLING_VELOCITY_DOWNSCALE);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Set up the Scroller for a fling
            float scrollTheta = vectorToScalarScroll(
                    velocityX,
                    velocityY,
                    e2.getX() - mPieBounds.centerX(),
                    e2.getY() - mPieBounds.centerY());
            mScroller.fling(
                    0,
                    (int) getPieRotation(),
                    0,
                    (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE);

            // Start the animator and tell it to animate for the expected duration of the fling.
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator.setDuration(mScroller.getDuration());
                mScrollAnimator.start();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            mPieView.accelerate();
            if (isAnimationRunning()) {
                stopScrolling();
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float tapX = e.getX();
            float tapY = e.getY();
            // Todo find in which slice was click and call onItemClick
            Log.d("tag", "onSingleTapUp (" + tapX + ", " + tapY + ")");
            Log.d("tag", "onSingleTapUp mRotation: " + mPieRotation);
            float centerX = mPieBounds.centerX();
            float centerY = mPieBounds.centerY();
            Log.d("tag", "onSingleTapUp Center(" + centerX+ ", " + centerY+ ")");

            double angle = angleBetweenVectors(centerX, centerY, tapX, tapY, centerX, centerY, centerX + 100, centerY);

            if (tapY > centerY)
                angle = 360 - angle;
            Log.d("tag", "onSingleTapUp angle: " + angle);
            int selectedSlice = findSelectedSlice(mPieRotation, (int) Math.round(angle));
            setSelectedItem(selectedSlice, true);
            if (mItemClickListener != null)
                mItemClickListener.OnItemCLick(PieChart.this, selectedSlice);

            return true;
        }



    }

    private int findSelectedSlice(int pieRotation, int angle) {
        int sliceAngle = (360 + 180 - (pieRotation + angle) % 360) % 360;
        Log.d("tag", "findSelectedSlice sliceAngle: " + sliceAngle);
        for (int i = 0; i < mData.size(); i++) {
            Item it = mData.get(i);
            Log.d("tag", "it[" + i+ "] startAngle: " + it.mStartAngle + ", endAngle: " + it.mEndAngle);
            if (it.mStartAngle < sliceAngle && sliceAngle <= it.mEndAngle) {
                return i;
            }
        }
        return -1;
    }

    private boolean isAnimationRunning() {
        return !mScroller.isFinished() || (Build.VERSION.SDK_INT >= 11 && mAutoCenterAnimator.isRunning());
    }

    /**
     * Helper method for translating (x,y) scroll vectors into scalar rotation of the pie.
     *
     * @param dx The x component of the current scroll vector.
     * @param dy The y component of the current scroll vector.
     * @param x  The x position of the current touch, relative to the pie center.
     * @param y  The y position of the current touch, relative to the pie center.
     * @return The scalar representing the change in angular position for this scroll.
     */
    private static float vectorToScalarScroll(float dx, float dy, float x, float y) {
        // get the length of the vector
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        // decide if the scalar should be negative or positive by finding
        // the dot product of the vector perpendicular to (x,y). 
        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static double vectorLength(float startX, float startY, float endX, float endY) {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    private static double vectorsMultiplication(float vectorAx, float vectorAy, float vectorBx, float vectorBy) {
        return vectorAx * vectorBx + vectorAy * vectorBy;
    }

    private static double angleBetweenVectors(float vectorAstartX, float vectorAstartY,
                                              float vectorAendX, float vectorAendY,
                                              float vectorBstartX, float vectorBstartY,
                                              float vectorBendX, float vectorBendY) {
        double vectorALength = vectorLength(vectorAstartX, vectorAstartY, vectorAendX, vectorAendY);
        double vectorBLength = vectorLength(vectorBstartX, vectorBstartY, vectorBendX, vectorBendY);

        double vectorsMul = vectorsMultiplication(vectorAendX - vectorAstartX, vectorAendY - vectorAstartY,
                                                  vectorBendX - vectorBstartX, vectorBendY - vectorBstartY);
        double angleCos = 0;
        if (vectorALength != 0 && vectorBLength != 0) {
            angleCos = vectorsMul / (vectorALength * vectorBLength);
        }
        return Math.toDegrees(Math.acos(angleCos));
    }
}
