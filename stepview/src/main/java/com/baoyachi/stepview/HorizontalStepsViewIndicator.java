package com.baoyachi.stepview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期：16/6/22 14:15
 * <p/>
 * 描述：StepsViewIndicator 指示器
 */
public class HorizontalStepsViewIndicator extends View {
    //定义默认的高度   definition default height
    private int defaultStepIndicatorNum = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    private float mCompletedLineHeight = 5;//完成线的高度     definition completed line height
    private float mCircleRadius;//圆的半径  definition circle radius

    private Drawable mCompleteIcon;//完成的默认图片    definition default completed icon
    private float mCenterY;//该view的Y轴中间位置     definition view centerY position

    private List<StepBean> mStepBeanList;//当前有几部流程    there are currently few step
    private int mStepNum = 0;
    private float mLinePadding;//两条连线之间的间距  definition the spacing between the two circles

    private List<Float> mCircleCenterPointPositionList;//定义所有圆的圆心点位置的集合 definition all of circles center point list
    private Paint mUnCompletedPaint;//未完成Paint  definition mUnCompletedPaint
    private Paint mCompletedPaint;//完成paint      definition mCompletedPaint
    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.uncompleted_color);//定义默认未完成线的颜色  definition
    private int mCompletedLineColor = Color.WHITE;//定义默认完成线的颜色      definition mCompletedLineColor
    private int mComplectingPosition;//正在进行position   underway position
    private Paint textPaint = new Paint();
    int totalHeight = (int) (defaultStepIndicatorNum * 1.8f);

    private Path mPath;
    private int screenWidth;//this screen width

    private static final int HORIZONTAL_PADDING = 45;
    private static final float TEXT_SIZE_SP = 17;
    int centerY;
    int downCenterY;
    int rectTop;
    boolean firstLaunch = true;
    int currentStep = 0;
    int oldStep;
    boolean isAnimating;

    /**
     * get圆的半径  get circle radius
     *
     * @return
     */
    public float getCircleRadius() {
        return mCircleRadius;
    }


    public HorizontalStepsViewIndicator(Context context) {
        this(context, null);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * init
     */
    private void init() {
        mStepBeanList = new ArrayList<>();
        mPath = new Path();

        mCircleCenterPointPositionList = new ArrayList<>();//初始化

        mUnCompletedPaint = new Paint();
        mCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStyle(Paint.Style.STROKE);
        mUnCompletedPaint.setStrokeWidth(dpToPx(mCompletedLineHeight));

        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStyle(Paint.Style.FILL);
        mCompletedPaint.setStrokeWidth(dpToPx(mCompletedLineHeight));

        // Text number page paint
        float scaledSizeInPixels = TEXT_SIZE_SP * getResources().getDisplayMetrics().scaledDensity;
        textPaint.setColor(0xFF118ee9);
        textPaint.setTextSize(scaledSizeInPixels);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        //圆的半径  set mCircleRadius
        mCircleRadius = 0.26f * defaultStepIndicatorNum;
        //线与线之间的间距    set mLinePadding
        mLinePadding = 0.85f * defaultStepIndicatorNum;

        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.complted);//已经完成的icon
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = totalHeight;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        int width = (int) (mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding);
        if (widthMeasure > width) {
            mLinePadding = (widthMeasure - dpToPx(HORIZONTAL_PADDING) * 2 - mStepNum * mCircleRadius * 2) / (mStepNum - 1);
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterY = 0.5f * getHeight();

        mCircleCenterPointPositionList.clear();
        for (int i = 0; i < mStepNum; i++) {
            //先计算全部最左边的padding值（getWidth()-（圆形直径+两圆之间距离）*2）
            float paddingLeft = (screenWidth - mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding) / 2;
            //add to list
            mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mCompletedPaint.setColor(mCompletedLineColor);

        //-----------------------画线-------draw line-----------------------------------------------
        for (int i = 0; i < mCircleCenterPointPositionList.size() - 1; i++) {
            //前一个ComplectedXPosition
            final float preComplectedXPosition = mCircleCenterPointPositionList.get(i);
            //后一个ComplectedXPosition
            final float afterComplectedXPosition = mCircleCenterPointPositionList.get(i + 1);

            if (i <= mComplectingPosition && mStepBeanList.get(0).getState() != StepBean.STEP_UNDO)//判断在完成之前的所有点
            {
                //判断在完成之前的所有点，画完成的线，这里是矩形,很细的矩形，类似线，为了做区分，好看些
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                canvas.drawPath(mPath, mCompletedPaint);
            } else {
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                canvas.drawPath(mPath, mUnCompletedPaint);
            }
        }
        //-----------------------画线-------draw line-----------------------------------------------


        //-----------------------画图标-----draw icon-----------------------------------------------
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            final float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect = new Rect((int) (currentComplectedXPosition - mCircleRadius), (int) (mCenterY - mCircleRadius), (int) (currentComplectedXPosition + mCircleRadius), (int) (mCenterY + mCircleRadius));

            StepBean stepsBean = mStepBeanList.get(i);

            if (stepsBean.getState() == StepBean.STEP_UNDO) {
                mCompletedPaint.setColor(mCompletedLineColor);
                // Ve cai bg tron xung quanh
                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.3f, mCompletedPaint);
                // O giua cua vong tron nay
                canvas.drawText(stepsBean.getName(), rect.centerX(), (rect.bottom + rect.centerY()) / 2f + dpToPx(2), textPaint);
                if (isAnimating && oldStep == i) {
                    mCompletedPaint.setColor(Color.WHITE);
                    float bigRadius = mCircleRadius * 1.75f;
                    Rect rect1 = new Rect((int) (currentComplectedXPosition - bigRadius * 2), (int) (mCenterY - bigRadius), (int) (currentComplectedXPosition + bigRadius * 2), totalHeight);
                    drawCurve(rect1, canvas, downCenterY);

                }
            } else if (stepsBean.getState() == StepBean.STEP_CURRENT) {
                mCompletedPaint.setColor(Color.WHITE);
                float bigRadius = mCircleRadius * 1.75f;
//                canvas.drawRect(rect, mCompletedPaint);
                Rect rect1 = new Rect((int) (currentComplectedXPosition - bigRadius * 2), (int) (mCenterY - bigRadius), (int) (currentComplectedXPosition + bigRadius * 2), totalHeight);

                drawCurve(rect1, canvas, centerY);
                // O giua cua vong tron nay
                canvas.drawText(stepsBean.getName(), rect.centerX(), (rect.bottom + rect.centerY()) / 2f + dpToPx(2), textPaint);
            } else if (stepsBean.getState() == StepBean.STEP_COMPLETED) {
//                mCompleteIcon.setBounds(rect);
                mCompleteIcon.setBounds(rect.left + dpToPx(2), rect.top + dpToPx(2), rect.right - dpToPx(2), rect.bottom - dpToPx(2));
                mCompletedPaint.setColor(mCompletedLineColor);
                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.3f, mCompletedPaint);
//                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius, mCompletedPaint);
                mCompleteIcon.draw(canvas);
                if (isAnimating && oldStep == i) {
                    mCompletedPaint.setColor(Color.WHITE);
                    float bigRadius = mCircleRadius * 1.75f;
                    Rect rect1 = new Rect((int) (currentComplectedXPosition - bigRadius * 2), (int) (mCenterY - bigRadius), (int) (currentComplectedXPosition + bigRadius * 2), totalHeight);
                    drawCurve(rect1, canvas, downCenterY);

                }
            }
        }
        //-----------------------画图标-----draw icon-----------------------------------------------
    }

    private void drawCurve(Rect rect, Canvas canvas, int centerY) {
        rectTop = rect.top;
        if (firstLaunch) {
            this.centerY = rectTop;
            centerY = rectTop;
            firstLaunch = false;
        }
        Log.d("Test", "width = " + rect.width() + " height = " + rect.height());
        int maxHeight = rect.bottom;
        int centerX = rect.centerX();

        Point p0 = new Point(rect.left - rect.width() / 4, rect.bottom);
        Point p3 = new Point(rect.centerX(), centerY);

        Point p1 = new Point((int) rect.centerX(), rect.bottom);
        Point p2 = new Point((int) (rect.left + (1 - (((float) maxHeight - centerY) / maxHeight + 0.1f)) * rect.width() * 0.4), centerY);
        Log.d("Test", "p2x = " + p2.x);


        Point p4 = new Point(2 * centerX - p2.x, p2.y);
        Point p5 = new Point(2 * centerX - p1.x, p1.y);
        Point p6 = new Point(2 * centerX - p0.x, p0.y);

        Path path = new Path();
        Paint curvePaint = new Paint();
        curvePaint.setAntiAlias(true);
        curvePaint.setColor(Color.WHITE);
        curvePaint.setStyle(Paint.Style.FILL);
        path.moveTo(p0.x, p0.y);
        path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        path.cubicTo(p4.x, p4.y, p5.x, p5.y, p6.x, p6.y);
        canvas.drawPath(path, curvePaint);
    }

    /**
     * 得到所有圆点所在的位置
     *
     * @return
     */
    public List<Float> getCircleCenterPointPositionList() {
        return mCircleCenterPointPositionList;
    }

    /**
     * 设置流程步数
     *
     * @param stepsBeanList 流程步数
     */
    public void setStepNum(List<StepBean> stepsBeanList) {
        this.mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();

        if (mStepBeanList.size() > 0) {
            for (int i = 0; i < mStepNum; i++) {
                StepBean stepsBean = mStepBeanList.get(i);
                {
                    if (stepsBean.getState() == StepBean.STEP_COMPLETED) {
                        mComplectingPosition = i;
                    }
                }
            }
        }

        requestLayout();
    }

    /**
     * 设置未完成线的颜色
     *
     * @param unCompletedLineColor
     */
    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    /**
     * 设置已完成线的颜色
     *
     * @param completedLineColor
     */
    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    /**
     * 设置已完成图片
     *
     * @param completeIcon
     */
    public void setCompleteIcon(Drawable completeIcon) {
        this.mCompleteIcon = completeIcon;
    }

    public void setStep(int stepNum) {
        if (stepNum > mStepBeanList.size()) {
            Log.e("Stepview", "step num is out of size");
            return;
        }
        oldStep = currentStep;
        currentStep = stepNum;
        boolean reachCurrentStep = false;
        for (int i = 0; i < mStepBeanList.size(); i++) {
            StepBean stepBean = mStepBeanList.get(i);
            if (i == stepNum) {
                reachCurrentStep = true;
                stepBean.setState(0);
            } else {
                stepBean.setState(reachCurrentStep ? -1 : 1);
            }
        }
        startAnim();
    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(totalHeight, rectTop);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                centerY = (int) animation.getAnimatedValue();
                downCenterY = totalHeight - (centerY - rectTop);
                Log.d("CenterY", "CenterY = " + centerY);
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public int dpToPx(float dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
