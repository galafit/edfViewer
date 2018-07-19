package com.biorecorder.basechart;

/**
 * Created by galafit on 20/10/17.
 */
public interface GestureListener {
    public void onClick(int x, int y); // onTap
    public void onDoubleClick(int x, int y); // onDoubleTap
    public void onPinchZoom(double xZoomFactor, double yZoomFactor);
    public void onRelease(int x, int y); // onUp
    public void onPress(int x, int y, boolean isLong); // long press or right mouse button

    /**
     * Mouse drag or one finger movement (Pan/Drag gesture)
     * Can be Modified with special key pressed (shift, control, alt, meta...)
     */
    public void onDrag(int x, int y, boolean isModified); // onPan

    /**
     * Mouse wheel scroll or two fingers up or down movement (Apple gesture https://support.apple.com/en-us/HT204895).
     * Can be Modified with special key pressed (shift, control, alt, meta...)
     */
    public void onScroll(int translation, boolean isModified);
}
