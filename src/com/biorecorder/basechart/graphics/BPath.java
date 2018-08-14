package com.biorecorder.basechart.graphics;
import java.util.Arrays;


/**
 * Created by galafit on 30/12/17.
 */
public class BPath {
    private static final byte SEG_MOVETO = (byte) BPathIterator.SEG_MOVETO;
    private static final byte SEG_LINETO = (byte) BPathIterator.SEG_LINETO;
    private static final byte SEG_QUADTO = (byte) BPathIterator.SEG_QUADTO;
    private static final byte SEG_CUBICTO = (byte) BPathIterator.SEG_CUBICTO;
    private static final byte SEG_CLOSE = (byte) BPathIterator.SEG_CLOSE;

    private static final int INIT_SIZE = 20;
    private static final int EXPAND_MAX = 500;


    private int numTypes;
    private int numCoords;
    private byte[] pointTypes;
    private float[] pointCoords;

    public BPath() {
        this(INIT_SIZE);
    }

    public BPath(int initialCapacity) {
        pointTypes = new byte[initialCapacity];
        pointCoords = new float[2 * initialCapacity];
    }

    public void moveTo(float x, float y) {
        if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
            pointCoords[numCoords-2] = x;
            pointCoords[numCoords-1] = y;
        } else {
            needRoom(false, 2);
            pointTypes[numTypes++] = SEG_MOVETO;
            pointCoords[numCoords++] = x;
            pointCoords[numCoords++] = y;
        }
    }

    public void lineTo(float x, float y) {
        needRoom(true, 2);
        pointTypes[numTypes++] = SEG_LINETO;
        pointCoords[numCoords++] = x;
        pointCoords[numCoords++] = y;
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        needRoom(true, 4);
        pointTypes[numTypes++] = SEG_QUADTO;
        pointCoords[numCoords++] = x1;
        pointCoords[numCoords++] = y1;
        pointCoords[numCoords++] = x2;
        pointCoords[numCoords++] = y2;
    }

    public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        needRoom(true, 6);
        pointTypes[numTypes++] = SEG_CUBICTO;
        pointCoords[numCoords++] = x1;
        pointCoords[numCoords++] = y1;
        pointCoords[numCoords++] = x2;
        pointCoords[numCoords++] = y2;
        pointCoords[numCoords++] = x3;
        pointCoords[numCoords++] = y3;
    }

    public void close() {
        if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
            needRoom(true, 0);
            pointTypes[numTypes++] = SEG_CLOSE;
        }
    }

    private void  needRoom(boolean needMove, int newCoords) {
        if (needMove && numTypes == 0) {
            throw new RuntimeException("missing initial moveto "+
                    "in path definition");
        }
        int size = pointTypes.length;
        if (numTypes >= size) {
            int grow = size;
            if (grow > EXPAND_MAX) {
                grow = EXPAND_MAX;
            }
            pointTypes = Arrays.copyOf(pointTypes, size+grow);
        }
        size = pointCoords.length;
        if (numCoords + newCoords > size) {
            int grow = size;
            if (grow > EXPAND_MAX * 2) {
                grow = EXPAND_MAX * 2;
            }
            if (grow < newCoords) {
                grow = newCoords;
            }
            pointCoords = Arrays.copyOf(pointCoords, size+grow);
        }
    }

    public BPathIterator getPathIterator() {
        return new BPathIterator() {
            private int typeIdx;
            private int pointIdx;
            @Override
            public boolean hasNext() {
                return !(typeIdx >= numTypes);
            }

            @Override
            public int next(float[] coords) {
                byte type = pointTypes[typeIdx];
                int numCoords = 0;
                switch (type) {
                    case BPath.SEG_MOVETO:
                    case BPath.SEG_LINETO:
                        numCoords = 2;
                        break;
                    case BPath.SEG_CUBICTO:
                        numCoords = 6;
                        break;
                    case BPath.SEG_QUADTO:
                        numCoords = 4;
                        break;
                }
                if (numCoords > 0) {
                    System.arraycopy(pointCoords, pointIdx, coords, 0, numCoords);
                }
                typeIdx++;
                pointIdx += numCoords;
                return type;
            }
        };
    }
}
