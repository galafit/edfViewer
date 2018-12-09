package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.graphics.BRectangle;

/**
 * Created by galafit on 14/9/18.
 */
public interface InteractiveDrawable {
    public void onResize(BRectangle area);

    public boolean onTap(int x, int y); // onClick
    public boolean onDoubleTap(int x, int y); // onDoubleClick
    public boolean onTapUp(int x, int y); // onRelease
    public boolean onLongPress(int x, int y); // long press or right mouse button


    public boolean onScaleX(BPoint startPoint, double scaleFactor); // onPinchZoom
    public boolean onScaleY(BPoint startPoint, double scaleFactor); // onPinchZoom

     /**
     * Mouse wheel scroll or two fingers up or down movement
     * https://developer.android.com/reference/android/view/GestureDetector.OnGestureListener
     * https://stackoverflow.com/questions/28098737/difference-between-onscroll-and-onfling-of-gesturedetector
     */
    public boolean onScrollX(BPoint startPoint, int dx);
    public boolean onScrollY(BPoint startPoint, int dy);

    /**
     * Костыль для работы co скроллбаром. Пока не придумается
     * лучший вариант
     */
    public boolean onDrag(BPoint startPoint, int dx, int dy);


    public boolean update();

    public void draw(BCanvas canvas);
}
