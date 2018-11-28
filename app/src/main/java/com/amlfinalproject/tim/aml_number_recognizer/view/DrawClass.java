package com.amlfinalproject.tim.aml_number_recognizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

public class DrawClass extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public DrawClass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();

    }

    void init(){
        paintScreen = new Paint();

        //Your Finger
        paintLine = new Paint();
        //Attributes
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(100);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setStyle(Paint.Style.STROKE);


        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        bitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap,0,0,paintScreen);

        for(Integer key: pathMap.keySet()){
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //This is just the event type (Is finger up or down)
        int action = event.getActionMasked();

        //This is the Finger
        int actionIndex = event.getActionIndex();


        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP)  //This finds if the screen is touched
        {

            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));  //Captures the X,Y values and pointerID

        }

        //Check if the persons finger left the screen
        else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));

        } else {
            touchMoved(event);
        }


        invalidate();  //redraw the Screen

        return true;
    }

    public Bitmap getBitmap(){

    return bitmap;

    }
    private void touchMoved(MotionEvent event) {

        for(int i = 0; i < event.getPointerCount(); i++){
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if(pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);


                //Figure out how far the user moved from the last movement/update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // If the distance is significant enough to be considered a movement then we do this
                if(deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE){

                    //move the path to the new location
                    path.quadTo(point.x, point.y, (newX+point.x)/2, (newY+point.y)/2);

                    // Store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }

    }

    private void touchEnded(int pointerId) {

        Path path = pathMap.get(pointerId); // get the corresponding Path
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas

        path.reset();
    }

    public void clear(){
        pathMap.clear();// Removes all the previous paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate(); // cleans the screen
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path;   // Store the path for given touch
        Point point;    // Store the last point in the path


        if(pathMap.containsKey(pointerId)){

            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);

        } else{
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        //move to the coordinates of the touch
        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;

    }
}
