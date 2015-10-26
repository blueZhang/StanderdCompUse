package com.bluezhang.standerdcompuse.widgecs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author: blueZhang
 * DATE:2015/10/26
 * Time: 16:48
 * email:bluezhang521@163.com
 */
public class FullVedioView extends VideoView {
    public FullVedioView(Context context) {
        super(context);
    }

    public FullVedioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //重写onMeasure
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize,hSize);
    }
}
