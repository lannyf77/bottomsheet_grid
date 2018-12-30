package com.test.bottomsheet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by linma9 on 12/28/2018
 */
public class GridView extends View {

    public GridView(Context context) {
        super(context);
        init();
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    Paint paint = new Paint();
    Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2f);

        gridPaint.setColor(0xFFe4facd);  //(Color.GREEN);
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setStrokeWidth(0.8f);

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(40f);

        labelPaint.setColor(0x88eeeeee);
        labelPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTextSize(60f);

        mHandler.post(mRunnable);

        initPoints();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawPoint(canvas);
    }

    int gridHeight = 60;
    int megForOneGrid = 15;  //how many meg one grid height is for
    private void drawPoint(Canvas canvas) {
        int last_x = 0;
        double last_y = 0;

        int x_offset = 0;
        int baseHight = getHeight();
        for (int i=0; i<point.length && x_offset<getWidth(); i++) {

            x_offset = last_x;
            double p = point[i];

            Log.i("+++", "+++, drawPoint(),i:"+i+", p:"+p+", x_offset:"+x_offset+
                    ", getWidth():"+getWidth()+", getHeight():"+getHeight());

            int x = (i==0) ? x_offset : last_x;
            double y = (i==0) ? baseHight-p : last_y;

            x_offset += gridHeight;
            double p2 = point[i++];

            int x2 = x_offset;
            double y2 = baseHight-p2;

            canvas.drawLine(x, (float)y, x2, (float)y2, paint);

            last_x = x2;
            last_y = y2;
        }
    }

    private void drawGrid(Canvas canvas) {

        int height_offset = 0;
        int w = getWidth();
        int h = getHeight();

        height_offset = h;
        for (int i=0; height_offset>=0; i++) {
            canvas.drawLine(0, height_offset, w, height_offset, gridPaint);

            if (i != 0 && (i % 2 != 0)) {
                String label = ((i * megForOneGrid)) + "MB";
                canvas.drawText(label, 10, height_offset+20, textPaint);
            }

            height_offset -= gridHeight;

            Log.w("+++", "+++, drawGrid(),i:"+i+", height_offset:"+height_offset);
        }
        canvas.drawText("Memory Usage", w/2, h/2, labelPaint);
    }

    double point[] = new double[120];
    private void initPoints() {
        Arrays.fill(point, -1);
        collectMemoryInfo();
    }

    private int nextRunTime = 1000; // 1 sec
    private boolean continueToRun = true;

    Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            collectMemoryInfo();
            if(continueToRun == true){
                mHandler.postDelayed(mRunnable, 250);
            }
        }
    };

    public static long getUsedMemorySize() {//for this app

        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedSize;

    }

    public static int dipsToPixels(Context context, int dipValue) {
        DisplayMetrics thisDisplay = context.getResources().getDisplayMetrics();
        Float dipSize = Float.valueOf(dipValue * thisDisplay.density);
        return dipSize.intValue();
    }

    private long getAppMemory() {
        Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        long res = memInfo.getTotalPrivateDirty();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            res += memInfo.getTotalPrivateClean();

        return res;  // kb
    }

    public void collectMemoryInfo() {

//        long height = getHeight();
//        long calcedHeight = dipsToPixels(getContext(), 150);
//
//        height = (height>100) ? height : calcedHeight;

        long memUse = getAppMemory(); //kb

        // (y_height / usage_MB) = (one_grid_height / meg_per_one_grid)
        double heightForTheMemoryUsage = (memUse * gridHeight) / (1024.0 * megForOneGrid);

        for (int i=0; i<point.length-1; i++) {// shift
            point[i] = point[i+1];
        }
        point[point.length-1] = heightForTheMemoryUsage;  //memUse;

        for (int i=point.length-2; i>=0; i--) {// shift for first time fill with memory usage
            if (point[i] < 0) {
                point[i] = heightForTheMemoryUsage;
            } else {
                break;
            }
        }

        Log.w("+++", "+++ collectMemoryInfo(), mem:"+memUse+", usedMemInKB:"+getUsedMemorySize()+", heightForTheMemoryUsage:"+heightForTheMemoryUsage);

    }

    private Timer timer = null;
    private TimerTask task = null;

    public void startTimer() {
        stopTimer();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // Only the original thread that created a view hierarchy can touch its views.
                // run on the UI thread.
                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        };
        timer.schedule(task, nextRunTime, nextRunTime);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
