package com.baoyachi.stepview.demo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyachi.stepview.demo.R;

/**
 * 日期：16/6/24 20:00
 * <p>
 * 描述：
 */
public class DrawCanvasFragment extends Fragment
{
    View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new RectView(container.getContext());
    }
    public class RectView extends View
    {

        public RectView(Context context)
        {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {

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

            Point p0 = new Point(rect.left - rect.width() / 3 , rect.bottom);
            Point p1 = new Point((int) (rect.centerX() * 1.1f), rect.bottom);
            Point p2 = new Point(rect.left, rect.top);
            Point p3 = new Point(rect.centerX(), rect.top);
            Point p4 = new Point(rect.right, rect.top);
            Point p5 = new Point((int) (rect.centerX() * 0.9f), rect.bottom);
            Point p6 = new Point(rect.right + rect.width() / 3, rect.bottom);

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
