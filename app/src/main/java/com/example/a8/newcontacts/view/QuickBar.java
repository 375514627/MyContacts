package com.example.a8.newcontacts.view;

/**
 * Created by A8 on 2016/5/10.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义的View，实现ListView A~Z快速索引效果
 *
 * @author Folyd
 */
public class QuickBar extends View {

    private Paint mPaint = new Paint();
    private OnTouchLetterChangeListener listenner;
    // 是否画出背景
    private boolean showBg = false;
    // 选中的项
    private int choose = -1;
    // 准备好的A~Z的字母数组
    public static String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    // 构造方法
    public QuickBar(Context context) {
        super(context);
    }

    public QuickBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取宽和高
        int width = getWidth();
        int height = getHeight();
        // 每个字母的高度
        int singleHeight = height / letters.length;
        if (showBg) {
            // 画出背景
            canvas.drawColor(Color.parseColor("#22000000"));
        }
        // 画字母
        for (int i = 0; i < letters.length; i++) {
            mPaint.setColor(Color.BLACK);
            // 设置字体格式
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(width / 2);
            // 如果这一项被选中，则换一种颜色画
            if (i == choose) {
                mPaint.setColor(Color.parseColor("#F88701"));
                mPaint.setFakeBoldText(true);
            }
            // 要画的字母的x,y坐标
            float posX = width / 2 - mPaint.measureText(letters[i]) / 2;
            float posY = i * singleHeight + singleHeight;
            // 画出字母
            canvas.drawText(letters[i], posX, posY, mPaint);
            // 重新设置画笔
            mPaint.reset();
        }
    }

    /**
     * 处理QuickBar的状态
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        // 算出点击的字母的索引
        final int index = (int) (y / getHeight() * letters.length);
        // 保存上次点击的字母的索引到oldChoose
        final int oldChoose = choose;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showBg = true;
                if (oldChoose != index && listenner != null && index >= 0
                        && index < letters.length) {
                    choose = index;
                    listenner.onPressLetter(letters[index]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != index && listenner != null && index >= 0
                        && index < letters.length) {
                    choose = index;
                    listenner.onMoveLetterChange(letters[index]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                showBg = false;
                choose = -1;
                if (listenner != null) {
                    listenner.onDetachedLetter();
                }
                invalidate();
                break;
        }
        return true;
    }

    public void setOnTouchLetterChangeListener(
            OnTouchLetterChangeListener listener) {
        this.listenner = listener;
    }

    public interface OnTouchLetterChangeListener {

        void onPressLetter(String letter);

        void onMoveLetterChange(String letter);

        void onDetachedLetter();
    }

}
