package com.biorecorder.basechart;

/**
 * Created by galafit on 18/8/17.
 */
public class Margin {
    private int top;
    private int right;
    private int bottom;
    private int left;

    public Margin(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Margin(int margin) {
        this.top = margin;
        this.right = margin;
        this.bottom = margin;
        this.left = margin;
    }

    public int top() {
        return top;
    }

    public int right() {
        return right;
    }

    public int bottom() {
        return bottom;
    }

    public int left() {
        return left;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Margin) {
            Margin  m = (Margin) obj;
            if(m.right == right && m.left == left && m.top == top && m.bottom == bottom) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String str = super.toString() + ", top: " + top
                + ", right: " + right
                + ", bottom: " + bottom
                + ", left: " + left;
        return str;
    }
}
