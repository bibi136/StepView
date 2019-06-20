package com.baoyachi.stepview.kotlin

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.baoyachi.stepview.R

class StepView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val defaultStepIndicatorNum = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40.0f, resources.displayMetrics).toInt()
    private val lineHeightPx = 5.px
    private val mCircleRadius: Float
    private val curveRadius: Float

    private var completeIcon: Drawable? = null
    private var mCenterY: Float = 0f

    private var mStepNum = 4
    private var mLinePadding: Float

    private var mCircleCenterPointPositionList = mutableListOf<Float>()
    private var unCompletedPaint: Paint
    private var completedPaint: Paint
    private var unCompletedLineColor = ContextCompat.getColor(getContext(), R.color.uncompleted_color)//定义默认未完成线的颜色  definition
    private var completedLineColor = Color.WHITE//定义默认完成线的颜色      definition completedLineColor
    private var mComplectingPosition: Int = 0//正在进行position   underway position
    private val textPaint: Paint
    private val totalHeight: Int

    private val mPath: Path = Path()
    private val curvePaint: Paint
    private var screenWidth: Int = 0//this screen width

    private val HORIZONTAL_PADDING = 45.px
    private val TEXT_SIZE_SP = 17f
    private var centerY: Float = 0f
    private var currentStep = 0
    private var oldStep: Int = 0
    private var isAnimating: Boolean = false
    private val TEXT_COLOR = 0xFF118ee9.toInt()
    private var curveTopY: Float = 0f
    private val CURVE_COLOR = Color.WHITE
    private val CURVE_FACTOR = 1.75f
    private var tempRect : Rect = Rect()
    private var animatorPaint : Paint


    init {
        unCompletedPaint = Paint().apply {
            isAntiAlias = true
            color = unCompletedLineColor
            style = Paint.Style.STROKE
            strokeWidth = lineHeightPx
        }
        completedPaint = Paint(unCompletedPaint).apply {
            color = completedLineColor
            style = Paint.Style.FILL
        }
        animatorPaint = Paint(completedPaint)
        curvePaint = Paint().apply {
            isAntiAlias = true
            color = CURVE_COLOR
            style = Paint.Style.FILL
        }
        textPaint = Paint().apply {
            color = TEXT_COLOR
            textSize = TEXT_SIZE_SP * resources.displayMetrics.scaledDensity
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        totalHeight = (defaultStepIndicatorNum * 1.8f).toInt()
        mCircleRadius = 0.26f * defaultStepIndicatorNum
        mLinePadding = 0.85f * defaultStepIndicatorNum
        curveRadius = mCircleRadius * CURVE_FACTOR
        completeIcon = ContextCompat.getDrawable(getContext(), R.drawable.complted)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = totalHeight
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec))
        }

        val width = mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            screenWidth = MeasureSpec.getSize(widthMeasureSpec)
            if (width < screenWidth) {
                // Calculate the line padding
                mLinePadding = (screenWidth - HORIZONTAL_PADDING * 2 - mStepNum * mCircleRadius * 2) / (mStepNum - 1)
                setMeasuredDimension(screenWidth, height)
                return
            }
        }
        setMeasuredDimension(width.toInt(), height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterY = height * 0.5f
        mCircleCenterPointPositionList.clear()

        for (i in 0 until mStepNum) {
            val paddingLeft = (screenWidth - mStepNum * mCircleRadius * 2f - (mStepNum - 1) * mLinePadding) / 2
            mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding)
        }
        curveTopY = mCenterY - curveRadius
        centerY = curveTopY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLine(canvas)
        drawStep(canvas)
    }

    private fun drawLine(canvas: Canvas) {
        for (index in 0 until mCircleCenterPointPositionList.size - 1) {
            val currentCenterXPos = mCircleCenterPointPositionList[index]
            val afterXPos = mCircleCenterPointPositionList[index + 1]

//            val paint = if (index <= currentStep) unCompletedPaint
            mPath.reset()
            mPath.moveTo(currentCenterXPos + mCircleRadius, mCenterY)
            mPath.lineTo(afterXPos - mCircleRadius, mCenterY)
            canvas.drawPath(mPath, unCompletedPaint)
        }
    }

    private fun drawStep(canvas: Canvas) {
        for ((index, currentCenterXPos) in mCircleCenterPointPositionList.withIndex()) {
            val left = currentCenterXPos - mCircleRadius
            val top = mCenterY - mCircleRadius
            val right = currentCenterXPos + mCircleRadius
            val bottom = mCenterY + mCircleRadius
            when {
                index < currentStep -> {
                    completeIcon?.setBounds((left + 1.px).toInt(), (top + 1.px).toInt(), (right - 1.px).toInt(), (bottom - 1.px).toInt())
                    canvas.drawCircle(currentCenterXPos, mCenterY, mCircleRadius * 1.3f, completedPaint)
                    completeIcon?.draw(canvas)
                    // Completed
                    if (isAnimating && oldStep == index) {
                        drawCurve(currentCenterXPos, canvas, (totalHeight - (centerY - curveTopY)).toInt())
                    }
                }
                index == currentStep -> {
                    // Current
                    if (isAnimating) {
                        animatorPaint.alpha = ((centerY - curveTopY ) / (totalHeight - curveTopY) * 255).toInt()
                        canvas.drawCircle(currentCenterXPos, mCenterY, mCircleRadius * 1.3f, animatorPaint)
                    }
                    drawCurve(currentCenterXPos, canvas, centerY.toInt())
                    drawStepText(canvas, index, left, top, right, bottom)
                }
                index > currentStep -> {
                    // UnComplete
                    canvas.drawCircle(currentCenterXPos, mCenterY, mCircleRadius * 1.3f, completedPaint)
                    drawStepText(canvas, index, left, top, right, bottom)
                    if (isAnimating && oldStep == index) {
                        drawCurve(currentCenterXPos, canvas, (totalHeight - (centerY - curveTopY)).toInt())
                    }
                }
            }
        }
    }

    private fun drawStepText(canvas: Canvas, step: Int, left: Float, top: Float, right: Float, bottom: Float) {
        canvas.drawText((step + 1).toString(), (left + right) * 0.5f, (bottom + (top + bottom) * 0.5f) / 2f, textPaint)

    }

    private fun drawCurve(currentCenterXPos: Float, canvas: Canvas, topCurvedY: Int) {
        tempRect = Rect((currentCenterXPos - curveRadius * 2).toInt(), (mCenterY - curveRadius).toInt(), (currentCenterXPos + curveRadius * 2).toInt(), totalHeight)

        val maxHeight = tempRect.bottom
        val centerX = tempRect.centerX()

        val p0 = Point(tempRect.left - tempRect.width() / 4, tempRect.bottom)
        val p3 = Point(tempRect.centerX(), topCurvedY)
        val p1 = Point(tempRect.centerX(), tempRect.bottom)
        val p2 = Point((tempRect.left + (1 - ((maxHeight.toFloat() - topCurvedY) / maxHeight + 0.1f)).toDouble() * tempRect.width().toDouble() * 0.4f).toInt(), topCurvedY)
        val p4 = Point(2 * centerX - p2.x, p2.y)
        val p5 = Point(2 * centerX - p1.x, p1.y)
        val p6 = Point(2 * centerX - p0.x, p0.y)

        mPath.reset()
        mPath.moveTo(p0.x.toFloat(), p0.y.toFloat())
        mPath.cubicTo(p1.x.toFloat(), p1.y.toFloat(), p2.x.toFloat(), p2.y.toFloat(), p3.x.toFloat(), p3.y.toFloat())
        mPath.cubicTo(p4.x.toFloat(), p4.y.toFloat(), p5.x.toFloat(), p5.y.toFloat(), p6.x.toFloat(), p6.y.toFloat())
        canvas.drawPath(mPath, curvePaint)
    }

    fun setStep(stepNum: Int) {
        if (stepNum > mStepNum) {
            Log.e("Stepview", "step num is out of size")
            return
        }
        oldStep = currentStep
        currentStep = stepNum
        startAnim()
    }

    fun startAnim() {
        val animator = ValueAnimator.ofFloat(totalHeight.toFloat(), curveTopY)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 300
        animator.addUpdateListener { animation ->
            centerY = animation.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animator.start()
    }

    fun setStepNum(num: Int) {
        mStepNum = num
        requestLayout()
    }

    fun setUnCompletedLineColor(unCompletedLineColor: Int) {
        this.unCompletedLineColor = unCompletedLineColor
        unCompletedPaint.color = unCompletedLineColor
    }

    fun setCompletedLineColor(completedLineColor: Int) {
        this.completedLineColor = completedLineColor
        completedPaint.color = completedLineColor
        animatorPaint.color = completedLineColor
    }

    fun setCompleteIcon(completeIcon: Drawable) {
        this.completeIcon = completeIcon
    }
}

val Int.dp: Float
    get() = this / Resources.getSystem().displayMetrics.density
val Int.px: Float
    get() = this * Resources.getSystem().displayMetrics.density