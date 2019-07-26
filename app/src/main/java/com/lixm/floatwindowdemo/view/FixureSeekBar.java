package com.lixm.floatwindowdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.lixm.floatwindowdemo.R;


/**
 * @author Lixm
 * @date 2017/9/30
 * @detail 财视学院添加可免费观看时长
 */

public class FixureSeekBar extends AppCompatSeekBar {

    private Context mContext;
    private RectF rectF = new RectF(0, 0, 0, 0);
    private Paint mPaint;
    private int fixurePoi;
    private int fixureColor;

    private int defaultFixureColor = Color.parseColor("#fd8957");
    private int defaultFixurePoi = 0;

    private boolean hasDraw = false;

    public FixureSeekBar(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public FixureSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FixureSeekBar,
                0, 0);

        fixurePoi = array.getInteger(R.styleable.FixureSeekBar_progress_fixure_sb, defaultFixurePoi);
        fixureColor = array.getColor(R.styleable.FixureSeekBar_progress_fixure_color_sb, defaultFixureColor);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(fixureColor);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fixurePoi > 0 && fixurePoi < getMax()) {
            rectF.left = getWidth() * fixurePoi / getMax() + getPaddingLeft();
            rectF.top = getHeight() / 2 - getMinimumHeight() / 2;
            rectF.right = rectF.left + 10;
            rectF.bottom = getHeight() / 2 + getMinimumHeight() / 2;
            canvas.drawRect(rectF, mPaint);
            hasDraw = true;
        }
    }

    public void setFixurePoi(int fixurePoi) {
        this.fixurePoi = fixurePoi;
        invalidate();
    }
}
