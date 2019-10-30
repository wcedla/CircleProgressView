package com.wcedla.circleprogressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

/**
 * @author wcedla, Email wcedla@live.com, Date on 2019/10/30.
 * PS: The code may be millions of lines,but remember comment first please
 */

public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressView";

    TypedArray array;
    Context context;
    float density;
    float scaledDensity;
    Paint circlePaint;
    Paint ringPaint;
    Paint onlineBackgroundPaint;
    Paint onlineTextPaint;
    Paint onlinePercentPaint;
    RectF circleRectF;
    RectF textBackgroundRectF;
    float currentDegree;
    ValueAnimator drawAnimate;
    float circleRadius;
    float ringWidth;
    float innerCircleRadius;
    String drawText;
    String percentText;
    float textSize;
    float percentSize;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        currentDegree = 0;
        array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0);
        initPaint();
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidth, myHeight;
        myWidth = getMySize((int) Math.ceil(circleRadius * 2 + ringWidth), widthMeasureSpec);
        myHeight = getMySize((int) Math.ceil(circleRadius * 2 + ringWidth), heightMeasureSpec);
        myWidth = myWidth + getPaddingLeft() + getPaddingRight();
        myHeight = myHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(myWidth, myHeight);
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是wrap_content,取值为父view给定最大大小和默认大小的小得那个
                mySize = Math.min(defaultSize, size);
                Log.d(TAG, "wrap时的大小:" + mySize + "," + defaultSize + "," + size);
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它，match_parent和指定具体大小
                mySize = size;
                break;
            }
            default:
                break;
        }
        return mySize;
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setColor(array.getColor(R.styleable.CircleProgressView_CircleColor, Color.parseColor("#E3E2E0")));
        circlePaint.setStyle(Paint.Style.STROKE);
        ringWidth = array.getDimension(R.styleable.CircleProgressView_RingWidth, 20 * density);
        circlePaint.setStrokeWidth(ringWidth);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        ringPaint = new Paint();
        ringPaint.setStrokeWidth(ringWidth);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(array.getColor(R.styleable.CircleProgressView_RingColor, Color.parseColor("#48C7EB")));
        ringPaint.setAntiAlias(true);
        ringPaint.setDither(true);
        circleRadius = array.getDimension(R.styleable.CircleProgressView_CircleRadius, 70 * density);
        innerCircleRadius = circleRadius - (ringWidth / 2);
        onlineBackgroundPaint = new Paint();
        onlineBackgroundPaint.setStrokeWidth(1);
        onlineBackgroundPaint.setColor(array.getColor(R.styleable.CircleProgressView_TextBackground, Color.parseColor("#48C7EB")));
        onlineBackgroundPaint.setStyle(Paint.Style.FILL);
        onlineBackgroundPaint.setAntiAlias(true);
        onlineBackgroundPaint.setDither(true);
        drawText = array.getString(R.styleable.CircleProgressView_DrawText);
        onlineTextPaint = new Paint();
        onlineTextPaint.setStrokeWidth(1);
        textSize = array.getDimension(R.styleable.CircleProgressView_TextSize, 15 * scaledDensity);
        percentSize = array.getDimension(R.styleable.CircleProgressView_PercentSize, 17 * scaledDensity);
        if (textSize * drawText.length() > (0.9 * Math.sqrt(2) * innerCircleRadius)) {
            Log.d(TAG, "绘制文本大小超过背景大小了" + textSize * drawText.length() + "," + (0.9 * Math.sqrt(2) * innerCircleRadius) + "," + innerCircleRadius);
            textSize = (float) ((0.9 * Math.sqrt(2) * innerCircleRadius) / drawText.length() - (2 * scaledDensity));
            percentSize = (float) ((0.9 * Math.sqrt(2) * innerCircleRadius) / drawText.length() + (2 * scaledDensity));
            Log.d(TAG, "绘制文本大小设置为:" + textSize + "," + textSize / scaledDensity);
            Log.d(TAG, "绘制文本百分比大小为:" + percentSize + "," + percentSize / scaledDensity);
        }
        onlineTextPaint.setTextSize(textSize);
        onlineTextPaint.setColor(array.getColor(R.styleable.CircleProgressView_TextColor, Color.parseColor("#FFFFFF")));
        onlineTextPaint.setDither(true);
        onlineTextPaint.setAntiAlias(true);
        onlineTextPaint.setTextAlign(Paint.Align.CENTER);
        onlinePercentPaint = new Paint();
        onlinePercentPaint.setStrokeWidth(1);
        onlinePercentPaint.setTextSize(percentSize);
        onlinePercentPaint.setColor(array.getColor(R.styleable.CircleProgressView_PercentColor, Color.parseColor("#48C7EB")));
        onlinePercentPaint.setDither(true);
        onlinePercentPaint.setAntiAlias(true);
        onlinePercentPaint.setTextAlign(Paint.Align.CENTER);
        percentText = "0.0%";
    }

    public synchronized void setPercent(float newProgress) {
        startAnimate(360 * newProgress);
        percentText = formatPercentText(newProgress);
    }

    private synchronized void startAnimate(float newDegree) {
        if (drawAnimate != null) {
            if (drawAnimate.isRunning()) {
                drawAnimate.cancel();
            }
        }
        drawAnimate = ValueAnimator.ofFloat(currentDegree, newDegree);
        drawAnimate.setDuration(2000);
        drawAnimate.setStartDelay(0);
        drawAnimate.setRepeatCount(0);
        drawAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (float) valueAnimator.getAnimatedValue();
                Log.d(TAG, "变化值:" + currentValue);
                currentDegree = currentValue;
                invalidate();
            }
        });
        drawAnimate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentDegree = (float) ((ValueAnimator) animation).getAnimatedValue();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentDegree = (float) ((ValueAnimator) animation).getAnimatedValue();
            }
        });
        drawAnimate.start();
    }

    private void initRectF() {
        if (circleRectF == null) {
            circleRectF = new RectF((getWidth() / 2f) - circleRadius, (getHeight() / 2f) - circleRadius, (getWidth() / 2f) + circleRadius, (getHeight() / 2f) + circleRadius);
        }
        if (textBackgroundRectF == null) {
            textBackgroundRectF = new RectF(getWidth() / 2f - (float) (9 * Math.sqrt(2) * innerCircleRadius / 20), getHeight() / 2f - (float) (2 * Math.sqrt(2) * innerCircleRadius / 5), getWidth() / 2f + (float) (9 * Math.sqrt(2) * innerCircleRadius / 20), getHeight() / 2f);
        }
    }

    DecimalFormat decimalFormat;

    private String formatPercentText(float progress) {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(".0");
        }
        return decimalFormat.format(progress * 100) + "%";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initRectF();
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, circleRadius, circlePaint);
        canvas.drawRect(textBackgroundRectF, onlineBackgroundPaint);
        Paint.FontMetrics fontMetrics = onlineTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = getHeight() / 2f - (float) (2 * Math.sqrt(2) * innerCircleRadius / 10) + distance;
        canvas.drawText(drawText, (getWidth() / 2f), baseline, onlineTextPaint);
        Paint.FontMetrics percentFontMetrics = onlineTextPaint.getFontMetrics();
        float percentDistance = (percentFontMetrics.bottom - percentFontMetrics.top) / 2 - percentFontMetrics.bottom;
        float percentBaseline = getHeight() / 2f + (float) (5 * Math.sqrt(2) * innerCircleRadius / 20) + percentDistance;
        canvas.drawText(percentText, (getWidth() / 2f), percentBaseline, onlinePercentPaint);
        if (circleRectF != null) {
            canvas.drawArc(circleRectF, -90, currentDegree, false, ringPaint);
        }
    }
}
