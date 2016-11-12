package com.kachidoki.myselfview2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
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

    //------自由移动
    private int mLastPointerCount;

    private float mLastX,mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight,isCheckTopAndBottom;

    //-----双击放大
    private GestureDetector mGestrueDetector;

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestrueDetector = new ScaleGestureDetector(context,this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestrueDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                float x = e.getX();
                float y = e.getY();

                if (getScale()<mMidScale){
                    mScaleMatrix.postScale(mMidScale/getScale(),mMidScale/getScale(),x,y);
                    setImageMatrix(mScaleMatrix);
                }else {
                    mScaleMatrix.postScale(mInitScale/getScale(),mInitScale/getScale(),x,y);
                    setImageMatrix(mScaleMatrix);
                }
                return true;
            }
        });
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
        if (mGestrueDetector.onTouchEvent(event)) {return true;}
        mScaleGestrueDetector.onTouchEvent(event);


        float x = 0;
        float y = 0;
        int pointCount = event.getPointerCount();
        for (int i=0;i<pointCount;i++){
            x+=event.getX(i);
            y+=event.getY(i);
        }
        x/=pointCount;
        y/=pointCount;
        if (mLastPointerCount!=pointCount){
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointCount;
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag){
                    isCanDrag = isMove(dx,dy);
                }
                if (isCanDrag){
                   RectF rectf = getMatrixRectF();
                    if (getDrawable()!=null){
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        //如果宽度小于控件宽度，不允许横向移动
                        if (rectf.width()<getWidth()){
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectf.height()<getHeight()){
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx,dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount  = 0;
                break;
        }
        return true;
    }
    //移动时进行边界检查
    private void checkBorderWhenTranslate() {
        RectF rectf = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();

        if (rectf.top>0&&isCheckTopAndBottom){
            deltaY = -rectf.top;
        }
        if (rectf.bottom<height&&isCheckTopAndBottom){
            deltaY = height - rectf.bottom;
        }
        if (rectf.left>0&&isCheckLeftAndRight){
            deltaX = -rectf.left;
        }
        if (rectf.right<width&&isCheckLeftAndRight){
            deltaX = width - rectf.right;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);
    }

    private boolean isMove(float dx, float dy) {
        return Math.sqrt(dx*dx+dy*dy)>mTouchSlop;
    }
}
