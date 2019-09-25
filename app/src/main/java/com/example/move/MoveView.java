package com.example.move;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MoveView extends View {

    int[] colors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK};
    float offsetY = 100f;
    float mTouchX = 0f;
    float offsetX = 0f;
    float offsetTouchX = 0f;
    float centerX = 0f;
    float centerY = 0f;

    public MoveView(Context context) {
        super(context);
        init();
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        centerX = canvas.getWidth() / 2;
        float r = (int) (width / 5f / 2);
        float minR = 0.8f * r;
        float maxR = 1.1f * r;

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);

        for (int i = -1; i < 6; i++) {

            float offsetR = 0f;

            int index = 0;
            //计算实际位置的颜色位置
            if (offsetX <= 0) {
                /**
                 * 计算公式
                 * 代表当前位置是在原始位置的右边
                 * 滑动距离除以直径取整，获得当前位置在原始位置的右边多少个
                 * 绘画的当前位置加上右移动的个数，对总颜色数取余
                 * 得出右移N个后应该显示的颜色位置
                 */
                index = (i + 1 + (int) (Math.abs(offsetX) / 2 / r)) % colors.length;
            } else {
                /**
                 * 计算公式
                 * 代表当前位置是在原始位置的左边
                 * 滑动距离除以直径取整，获得当前位置在原始位置的左边多少个
                 * 绘画的当前位置减去左移动的个数，对总颜色数取余
                 * 如果得到的位置是负数，用总颜色数量减去取得余数
                 * 得出左移N个后应该显示的颜色位置
                 */
                index = (i + 1 - (int) (offsetX / 2 / r)) % colors.length;
                if(index <0){
                    index = colors.length + index;
                }
            }

            mPaint.setColor(colors[index]);

            /**
             * 循环移动。采用当偏移量大于一个圆的位置，使圆的颜色按照滑动方向依次替换达到整体滑动效果
             * 无线循环滑动 实际中就画了7个实物，用颜色的变化给与无限滑动的效果
             */

            float _offsetX = offsetX % (2f * r);

            float cx = r + (i * 2 * r) + _offsetX;

            float cy = r + offsetY;

            /**
             * 实现当圆移动到中心点的时候放大、移除中心的时候缩小.
             * 计算当前这个圆的圆心X轴坐标距离布局中心点的绝对值
             * 绝对值大于圆的直径，该圆已进入放大缩小的范围
             *
             * 计算滑动时半径大小
             * 如果圆心的X轴坐标正好在布局的中心点上是圆最大的时候，半径为MAX
             * 圆心距离中心点的距离在，最大圆直径的比例。离中心越近比例越高
             * minR * (1 + (1 - absR / (2 * maxR)))
             */

            float absR = Math.abs(centerX - cx);

            if (Float.compare(absR, maxR * 2) <= 0) {
                if (Float.compare(centerX, cx) == 0) {
                    offsetR = maxR;
                } else {
                    offsetR = minR * (1 + (1 - absR / (2 * maxR)));
                }
            } else {
                offsetR = minR;
            }

            canvas.drawCircle(cx, cy, offsetR, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = event.getX();
                offsetTouchX = offsetX;
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = event.getX();
                offsetX = (curX - mTouchX) + offsetTouchX;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }
}
