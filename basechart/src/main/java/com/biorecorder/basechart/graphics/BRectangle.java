package com.biorecorder.basechart.graphics;

/**
 * Created by galafit on 29/12/17.
 */
public class BRectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public BRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(float x, float y) {
        if(x < this.x || y < this.y || x > this.x + width || y > this.y + height) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "x: " + x + "  y: " + y + "  width: " + width + "  height: " + height;
    }
}
