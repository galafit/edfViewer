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
    public boolean equals(Object obj) {
        if (obj instanceof BRectangle) {
            BRectangle r = (BRectangle)obj;
            return ((x == r.x) &&
                    (y == r.y) &&
                    (width == r.width) &&
                    (height == r.height));
        }
        return super.equals(obj);

    }

    /**
     * https://www.mkyong.com/java/java-how-to-overrides-equals-and-hashcode/
     * https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "x: " + x + "  y: " + y + "  width: " + width + "  height: " + height;
    }
}
