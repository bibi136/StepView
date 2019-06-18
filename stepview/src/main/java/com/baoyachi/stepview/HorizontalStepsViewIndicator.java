package com.baoyachi.stepview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期：16/6/22 14:15
 * <p/>
 * 描述：StepsViewIndicator 指示器
 */
public class HorizontalStepsViewIndicator extends View
{
    //定义默认的高度   definition default height
    private int defaultStepIndicatorNum = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    private float mCompletedLineHeight;//完成线的高度     definition completed line height
    private float mCircleRadius;//圆的半径  definition circle radius

    private Drawable mCompleteIcon;//完成的默认图片    definition default completed icon
    private Drawable mAttentionIcon;//正在进行的默认图片     definition default underway icon
    private Drawable mDefaultIcon;//默认的背景图  definition default unCompleted icon
    private float mCenterY;//该view的Y轴中间位置     definition view centerY position
    private float mLeftY;//左上方的Y位置  definition rectangle LeftY position
    private float mRightY;//右下方的位置  definition rectangle RightY position

    private List<StepBean> mStepBeanList ;//当前有几部流程    there are currently few step
    private int mStepNum = 0;
    private float mLinePadding;//两条连线之间的间距  definition the spacing between the two circles

    private List<Float> mCircleCenterPointPositionList;//定义所有圆的圆心点位置的集合 definition all of circles center point list
    private Paint mUnCompletedPaint;//未完成Paint  definition mUnCompletedPaint
    private Paint mCompletedPaint;//完成paint      definition mCompletedPaint
    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.uncompleted_color);//定义默认未完成线的颜色  definition
    private int mCompletedLineColor = Color.WHITE;//定义默认完成线的颜色      definition mCompletedLineColor
    private PathEffect mEffects;
    private int mComplectingPosition;//正在进行position   underway position
    private Paint textPaint = new Paint();
    private int CURVE_CIRCLE_RADIUS = 10;
    int totalHeight = (int) (defaultStepIndicatorNum * 1.8f);

    private Path mPath;

    private OnDrawIndicatorListener mOnDrawListener;
    private int screenWidth;//this screen width

    /**
     * 设置监听
     *
     * @param onDrawListener
     */
    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener)
    {
        mOnDrawListener = onDrawListener;
    }

    /**
     * get圆的半径  get circle radius
     *
     * @return
     */
    public float getCircleRadius()
    {
        return mCircleRadius;
    }


    public HorizontalStepsViewIndicator(Context context)
    {
        this(context, null);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public HorizontalStepsViewIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * init
     */
    private void init()
    {
        mStepBeanList = new ArrayList<>();
        mPath = new Path();
        mEffects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);

        mCircleCenterPointPositionList = new ArrayList<>();//初始化

        mUnCompletedPaint = new Paint();
        mCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStyle(Paint.Style.STROKE);
        mUnCompletedPaint.setStrokeWidth(10);

        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStyle(Paint.Style.STROKE);
        mCompletedPaint.setStrokeWidth(10);

//        mUnCompletedPaint.setPathEffect(mEffects);
//        mUnCompletedPaint.setStyle(Paint.Style.FILL);
        mCompletedPaint.setStyle(Paint.Style.FILL);

        // Text number page paint
        textPaint.setColor(0xFF118ee9);
        textPaint.setTextSize(45.0f);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        //已经完成线的宽高 set mCompletedLineHeight
        mCompletedLineHeight = 0.05f * defaultStepIndicatorNum;
        //圆的半径  set mCircleRadius
        mCircleRadius = 0.26f * defaultStepIndicatorNum;
        //线与线之间的间距    set mLinePadding
        mLinePadding = 0.85f * defaultStepIndicatorNum;

        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.complted);//已经完成的icon
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.drawable.attention);//正在进行的icon
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.drawable.default_icon);//未完成的icon
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if(MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec))
        {
            screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = totalHeight;
        if(MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec))
        {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        int width = (int) (mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding);
        if (widthMeasure > width) {
            mLinePadding = (widthMeasure - 100 * 2 - mStepNum * mCircleRadius * 2) / (mStepNum - 1);
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取中间的高度,目的是为了让该view绘制的线和圆在该view垂直居中   get view centerY，keep current stepview center vertical
        mCenterY = 0.5f * getHeight();
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;

        mCircleCenterPointPositionList.clear();
        for(int i = 0; i < mStepNum; i++)
        {
            //先计算全部最左边的padding值（getWidth()-（圆形直径+两圆之间距离）*2）
            float paddingLeft = (screenWidth - mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding) / 2;
            //add to list
            mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding);
        }

        /**
         * set listener
         */
        if(mOnDrawListener!=null)
        {
            mOnDrawListener.ondrawIndicator();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mOnDrawListener!=null)
        {
            mOnDrawListener.ondrawIndicator();
        }
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mCompletedPaint.setColor(mCompletedLineColor);

        //-----------------------画线-------draw line-----------------------------------------------
        for(int i = 0; i < mCircleCenterPointPositionList.size() -1; i++)
        {
            //前一个ComplectedXPosition
            final float preComplectedXPosition = mCircleCenterPointPositionList.get(i);
            //后一个ComplectedXPosition
            final float afterComplectedXPosition = mCircleCenterPointPositionList.get(i + 1);

            if(i <= mComplectingPosition&&mStepBeanList.get(0).getState()!=StepBean.STEP_UNDO)//判断在完成之前的所有点
            {
                //判断在完成之前的所有点，画完成的线，这里是矩形,很细的矩形，类似线，为了做区分，好看些
//                canvas.drawRect(preComplectedXPosition + mCircleRadius - 10, mLeftY, afterComplectedXPosition - mCircleRadius + 10, mRightY, mCompletedPaint);
                mPath.moveTo(preComplectedXPosition + mCircleRadius - 10, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius + 10, mCenterY);
                canvas.drawPath(mPath, mCompletedPaint);
            } else
            {
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                canvas.drawPath(mPath, mUnCompletedPaint);
            }
        }
        //-----------------------画线-------draw line-----------------------------------------------


        //-----------------------画图标-----draw icon-----------------------------------------------
        for(int i = 0; i < mCircleCenterPointPositionList.size(); i++)
        {
            final float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect = new Rect((int) (currentComplectedXPosition - mCircleRadius), (int) (mCenterY - mCircleRadius), (int) (currentComplectedXPosition + mCircleRadius), (int) (mCenterY + mCircleRadius));

            StepBean stepsBean = mStepBeanList.get(i);

            if(stepsBean.getState()==StepBean.STEP_UNDO)
            {
                mCompletedPaint.setColor(mCompletedLineColor);
                mDefaultIcon.setBounds(rect);
                // Ve cai bg tron xung quanh
                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.3f, mCompletedPaint);
                // O giua cua vong tron nay
                canvas.drawText(stepsBean.getName(), rect.centerX() - 2, (rect.bottom + rect.centerY()) / 2f + 5, textPaint);
            }else if(stepsBean.getState()==StepBean.STEP_CURRENT)
            {
                mCompletedPaint.setColor(Color.WHITE);
//                canvas.drawRect(rect, mCompletedPaint);
                float bigRadius = mCircleRadius * 1.75f;
                Rect rect1 = new Rect((int) (currentComplectedXPosition - bigRadius * 2), (int) (mCenterY - bigRadius), (int) (currentComplectedXPosition + bigRadius * 2), totalHeight);

                drawCurve(rect1, canvas);
                // O giua cua vong tron nay
                canvas.drawText(stepsBean.getName(), rect.centerX() - 2, (rect.bottom + rect.centerY()) / 2f + 5, textPaint);
            }else if(stepsBean.getState()==StepBean.STEP_COMPLETED)
            {
//                mCompleteIcon.setBounds(rect);
                mCompleteIcon.setBounds(rect.left + 3, rect.top + 3, rect.right - 3, rect.bottom - 3);
                mCompletedPaint.setColor(mCompletedLineColor);
                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.3f, mCompletedPaint);
//                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius, mCompletedPaint);
                mCompleteIcon.draw(canvas);
            }
        }
        //-----------------------画图标-----draw icon-----------------------------------------------
    }

    private void drawCurve(Rect rect, Canvas canvas) {
//        Point mFirstCurveStartPoint = new Point();
//        Point mFirstCurveEndPoint = new Point();
//        Point mSecondCurveStartPoint;
//        Point mSecondCurveEndPoint = new Point();
//        Point mFirstCurveControlPoint1 = new Point();
//        Point mFirstCurveControlPoint2 = new Point();
//        Point mSecondCurveControlPoint1 = new Point();
//        Point mSecondCurveControlPoint2 = new Point();
//
//        // the coordinates (x,y) of the start point before curve
//        mFirstCurveStartPoint.set(rect.left - (CURVE_CIRCLE_RADIUS * 2) - (CURVE_CIRCLE_RADIUS / 3), rect.top);
//        // the coordinates (x,y) of the end point after curve
//        mFirstCurveEndPoint.set(rect.centerX(), CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4));
//        // same thing for the second curve
//        mSecondCurveStartPoint = mFirstCurveEndPoint;
//        mSecondCurveEndPoint.set((rect.right / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3), rect.top);
//
//        // the coordinates (x,y)  of the 1st control point on a cubic curve
//        mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4), mFirstCurveStartPoint.y);
//        // the coordinates (x,y)  of the 2nd control point on a cubic curve
//        mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2) + CURVE_CIRCLE_RADIUS, mFirstCurveEndPoint.y);
//
//        mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2) - CURVE_CIRCLE_RADIUS, mSecondCurveStartPoint.y);
//        mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4)), mSecondCurveEndPoint.y);
//
//        mPath.reset();
////        mPath.moveTo(rect.left - 40, rect.bottom + 40);
//        mPath.moveTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y);
//        mPath.lineTo(mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);
//        mPath.lineTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y);
//
////        mPath.cubicTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y,
////                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
////                mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);
//
////        mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
////                mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y,
////                mSecondCurveEndPoint.x, mSecondCurveEndPoint.y);
//
////        mPath.lineTo(rect.width(), 0);
//        mPath.close();
        Log.d("Test", "width = " + rect.width() + " height = " + rect.height());
        Point p0 = new Point((int) (rect.left - rect.width() / 4f), rect.bottom);
        Point p1 = new Point((rect.centerX() + 20), rect.bottom);
        Point p2 = new Point(rect.left - 3, rect.top + 15);
        Point p3 = new Point(rect.centerX(), rect.top + 3);
        Point p4 = new Point(rect.right + 3, rect.top + 15);
        Point p5 = new Point((rect.centerX() - 20), rect.bottom);
        Point p6 = new Point((int) (rect.right + rect.width() / 4f), rect.bottom);

        Path path = new Path();
        Paint curvePaint = new Paint();
        curvePaint.setAntiAlias(true);
        curvePaint.setColor(Color.WHITE);
        curvePaint.setStyle(Paint.Style.FILL);
        curvePaint.setStrokeWidth(3);
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
    public List<Float> getCircleCenterPointPositionList()
    {
        return mCircleCenterPointPositionList;
    }

    /**
     * 设置流程步数
     *
     * @param stepsBeanList 流程步数
     */
    public void setStepNum(List<StepBean> stepsBeanList)
    {
        this.mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();

        if(mStepBeanList!=null&&mStepBeanList.size()>0)
        {
            for(int i = 0;i<mStepNum;i++)
            {
                StepBean stepsBean = mStepBeanList.get(i);
                {
                    if(stepsBean.getState()==StepBean.STEP_COMPLETED)
                    {
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
    public void setUnCompletedLineColor(int unCompletedLineColor)
    {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    /**
     * 设置已完成线的颜色
     *
     * @param completedLineColor
     */
    public void setCompletedLineColor(int completedLineColor)
    {
        this.mCompletedLineColor = completedLineColor;
    }

    /**
     * 设置默认图片
     *
     * @param defaultIcon
     */
    public void setDefaultIcon(Drawable defaultIcon)
    {
        this.mDefaultIcon = defaultIcon;
    }

    /**
     * 设置已完成图片
     *
     * @param completeIcon
     */
    public void setCompleteIcon(Drawable completeIcon)
    {
        this.mCompleteIcon = completeIcon;
    }

    /**
     * 设置正在进行中的图片
     *
     * @param attentionIcon
     */
    public void setAttentionIcon(Drawable attentionIcon)
    {
        this.mAttentionIcon = attentionIcon;
    }


    /**
     * 设置对view监听
     */
    public interface OnDrawIndicatorListener
    {
        void ondrawIndicator();
    }
}
