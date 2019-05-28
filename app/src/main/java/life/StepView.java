package life;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StepView extends View {
    private Paint mPaint;
    private Paint mStrokePaint;
    private Path mArrowPath; // 箭头路径

    private int cR = 10; // 圆点半径
    private int arrowR = 20; // 箭头半径

    private float mCurX = 200;
    private float mCurY = 200;
    private int mOrient;
    private Bitmap mBitmap;
//    private double wifisiglevel=0.0;
    private ArrayList<Double> siglevellist=new ArrayList<>();
    private List<PointF> mPointList = new ArrayList<>();
    private ArrayList<String> wifidata=new ArrayList<>();
    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mStrokePaint = new Paint(mPaint);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(5);

        // 初始化箭头路径
        mArrowPath = new Path();
        mArrowPath.arcTo(new RectF(-arrowR, -arrowR, arrowR, arrowR), 0, -180);
        mArrowPath.lineTo(0, -3 * arrowR);
        mArrowPath.close();

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), null); // 将mBitmap绘到canLock

        for (int i=0;i<mPointList.size();i++) {
            PointF p=mPointList.get(i);
            Double d=siglevellist.get(i);
            if(d<-80.0){
                mPaint.setColor(Color.rgb(0,255,255	));//cyan, (0,20)
            }else if(d<-60.0){
                mPaint.setColor(Color.rgb(0,255,127		));//green, (20, 40)
            }else if(d<-40.0){
                mPaint.setColor(Color.rgb(255 ,255,0));//yellow, (40,60)
            }else if(d<-20.0){
                mPaint.setColor(Color.rgb(255,0,0));//red, (60,80)
            }else if(d<-0.0){
                mPaint.setColor(Color.rgb(139,26,26));//brickred, (80,100)
            }

            canvas.drawCircle(p.x, p.y, cR, mPaint);
            canvas.drawText(""+siglevellist.get(i),p.x+10,p.y,mPaint);
//            i++;
        }
        canvas.save(); // 保存画布
        canvas.translate(mCurX, mCurY); // 平移画布
        canvas.rotate(mOrient); // 转动画布
        canvas.drawPath(mArrowPath, mPaint);
        canvas.drawArc(new RectF(-arrowR * 0.8f, -arrowR * 0.8f, arrowR * 0.8f, arrowR * 0.8f),
                0, 360, false, mStrokePaint);
        canvas.restore(); // 恢复画布
    }

    /**
     * 当屏幕被触摸时调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurX = event.getX();
        mCurY = event.getY();
        invalidate();
        return true;
    }

    /**
     * 自动增加点
     */
    public void autoAddPoint(float stepLen, List<ScanResult> results) {
        mCurX += (float) (stepLen * Math.sin(Math.toRadians(mOrient)));
        mCurY += -(float) (stepLen * Math.cos(Math.toRadians(mOrient)));
        mPointList.add(new PointF(mCurX, mCurY));
        if(results.size()==0)wifidata.add("");
        ScanResult sc=null;
        System.out.println("scan size:"+results.size());
        for(ScanResult scanResult:results){
            System.out.println("scan result:"+scanResult.SSID+", "+scanResult.level);
            if(scanResult.SSID.equals("rabbi1")){
                sc=scanResult;
                System.out.println("found");
                break;
            }

        }
        System.out.println();System.out.println();
        System.out.println();

        String result;
        if (sc==null){
//            wifidata.add(result);
            siglevellist.add(-100.0);

        }else{
//            wifidata.add(result);
            siglevellist.add((double)sc.level);

        }

        invalidate();
    }

    public void autoDrawArrow(int orient) {
        mOrient = orient;
        invalidate();
    }
}