package com.kachidoki.myselfview2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by mayiwei on 16/11/9.
 */
public class MyImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener{
    private boolean isInit;
    //初始化缩放的值
    private float mInitScale;
    //双击放大值
    private float mMidScale;
    //放大的最大值
    private float mMaxScale;

    private Matrix mScaleMatrix;
    //捕获用户多指触控比例
    private ScaleGestureDetector mScaleGestrueDetector;



    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestrueDetector = new ScaleGestureDetector(context,this);
        setOnTouchListener(this);
    }

    public MyImageView(Context context) {
        this(context,null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    //获取加载完成的图片
    @Override
    public void onGlobalLayout() {
        if (!isInit){
            int width = getWidth();
            int height = getHeight();

             Drawable d = getDrawable();
            if (d== null){
                return;
            }
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;
            if(dw>width && dh<height){
                scale = width*1.0f/dw;

            }
            if (dh>height && dw<width){
                scale = height*1.0f/dh;
            }
            if (dw>width && dh>height){
                scale = Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            if (dw<width && dh<height){
                scale = Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            mInitScale = scale;
            mMaxScale = mInitScale*4;
            mMidScale = mMidScale*2;
            //移动图片至中心

            int dx = getWidth()/2-dw/2;
            int dy = getHeight()/2-dh/2;

            mScaleMatrix.postTranslate(dx,dy);
            mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
            setImageMatrix(mScaleMatrix);

            isInit = true;
        }
    }

    public float getScale(){
        float[] value = new float[9];
        mScaleMatrix.getValues(value);
        return value[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //缩放区间为initScale max...
        float scale = getScale();
        float scaleFator = detector.getScaleFactor();

        if (getDrawable()==null) return true;

        if ((scale<mMaxScale&&scaleFator>1.0f)||(scale>mInitScale&&scaleFator<1.0f)){
            if(scale*scaleFator<mInitScale){
                scaleFator = mInitScale/scale;
            }
            if (scale*scaleFator>mMaxScale){
                scale = mMaxScale/scale;
            }


            mScaleMatrix.postScale(scaleFator,scaleFator,detector.getFocusX(),detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    //获得图片放大缩小以后的宽和高
    private RectF getMatrixRectF(){
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable d = getDrawable();
        if (d!=null){
            rectF.set(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    //控制缩放时位置的控制
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();
        //缩放时防止白边出现
        if (rect.width()>=width){
            if (rect.left>0){
                deltaX = -rect.left;
            }
            if (rect.right<width){
                deltaX = width-rect.right;
            }
        }

        if (rect.height()>=height){
            if (rect.top>0){
                deltaY = -rect.top;
            }
            if (rect.bottom<height){
                deltaY = height - rect.bottom;
            }
        }
        //小于控件居中
        if (rect.width()<width){
            deltaX = width/2f-rect.right+rect.width()/2;
        }
        if (rect.height()<height){
            deltaY = height/2f - rect.bottom+rect.height()/2;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestrueDetector.onTouchEvent(event);
        return true;
    }
}
