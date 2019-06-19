package com.baoyachi.stepview.demo.fragment;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.baoyachi.stepview.demo.R;

/**
 * 日期：16/6/24 20:00
 * <p>
 * 描述：
 */
public class DrawCanvasFragment extends Fragment {
    View mView;
    int centerY = 50; // rect.top -> maxHeight
    RectView rectView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rectView =  new RectView(container.getContext());
        return rectView;
    }

    @Override
    public void onResume() {
        super.onResume();
        rectView.startAnim();
    }

    public class RectView extends View {

        public RectView(Context context) {
            super(context);
        }

        public void startAnim() {
            ValueAnimator animator = ValueAnimator.ofInt(400, 50);
//            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(3000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    centerY = (int) animation.getAnimatedValue();
                    Log.d("CenterY", "CenterY = " + centerY );
                    invalidate();
                }
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);
            setBackgroundResource(R.drawable.default_bg);//设置背景色

            //--------------------------绘制矩形-----------------------------------------------------
//            Paint paint = new Paint();// 定义画笔
//            paint.setStyle(Paint.Style.FILL);//设置实心
//            paint.setAntiAlias(true);// 消除锯齿
//            paint.setColor(Color.WHITE);//设置画笔颜色
//            paint.setStrokeWidth(40);// 设置paint的外框宽度
//            canvas.drawRect(200, 200, 800, 220, paint);//绘制矩形
//            //--------------------------绘制矩形-----------------------------------------------------
//
//
//            //--------------------------绘制圆-----------------------------------------------------
//            canvas.drawCircle(350, 350, 100, paint);
//            //--------------------------绘制圆-----------------------------------------------------
//
//            //--------------------------绘制虚线-----------------------------------------------------
//
//            Paint pathPaint = new Paint();
//            pathPaint.setAntiAlias(true);
//            pathPaint.setColor(Color.WHITE);
//            pathPaint.setStyle(Paint.Style.STROKE);
//            pathPaint.setStrokeWidth(2);
//            DashPathEffect mEffects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
//            Path path = new Path();
//            path.moveTo(200, 600);
//            path.lineTo(800, 600);
//            pathPaint.setPathEffect(mEffects);
//            canvas.drawPath(path, pathPaint);
//            path.close();
            //--------------------------绘制虚线-----------------------------------------------------

            Rect rect = new Rect(200, 50, 600, 400);
            int maxHeight = rect.bottom;
            int centerX = rect.centerX();

            Point p0 = new Point(rect.left - rect.width() / 3, rect.bottom);
            Point p3 = new Point(rect.centerX(), centerY);

            Point p1 = new Point((int) rect.centerX(), rect.bottom);
            Point p2 = new Point((int) (rect.left + (1 - (((float)maxHeight - centerY)/ maxHeight + 0.1f)) * rect.width() * 0.4), centerY);
            Log.d("Test", "p2x = " + p2.x);


            Point p4 = new Point(2 * centerX - p2.x, p2.y);
            Point p5 = new Point(2 * centerX - p1.x, p1.y);
            Point p6 = new Point(2 * centerX - p0.x, p0.y);

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

            path.reset();
            curvePaint.setColor(Color.GRAY);
            curvePaint.setStyle(Paint.Style.STROKE);
            curvePaint.setStrokeWidth(1);

            path.moveTo(p0.x, p0.y);
            path.lineTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            path.lineTo(p3.x, p3.y);
            path.lineTo(p4.x, p4.y);
            path.lineTo(p5.x, p5.y);
            path.lineTo(p6.x, p6.y);

            canvas.drawPath(path, curvePaint);
            canvas.drawRect(rect, curvePaint);
        }
    }
}
