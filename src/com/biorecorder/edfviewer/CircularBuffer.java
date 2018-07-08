package com.biorecorder.edfviewer;

/**
 * Created by galafit on 25/6/18.
 */
public class CircularBuffer {
    int[] buffer;
    int recordLength;
    int capacity; // in records
    int size = 0; // in records
    int start = 0; // in records

    public CircularBuffer(int capacity, int recordLength) {
        this.capacity = capacity;
        this.recordLength = recordLength;
        buffer = new int[capacity * recordLength];
    }

    public void addFirst(int[] data, int startIndex) {
        int position = start + size;
        if(position >= capacity) {
            position -= capacity;
        }
        size++;
        if(size > capacity) {
            size = capacity;
            start++;
        }
        if(start == capacity) {
            start = 0;
        }
        System.arraycopy(data, startIndex, buffer, position * recordLength, recordLength);
    }

    public void addLast(int[] data, int startIndex) {
        int position = start - 1;
        if(position < 0) {
            position += capacity;
        }
        start = position;
        size++;
        if(size > capacity) {
            size = capacity;
        }
        System.arraycopy(data, startIndex, buffer, position * recordLength, recordLength);
    }

    public void clear() {
        size = 0;
        start = 0;
    }

    // in records
    public int getSize() {
        return size;
    }

    public int getSample(int recordNumber, int positionWithinRecord) {
        if(recordNumber < 0 || recordNumber >= size) {
            String errMsg = "Invalid record number: "+ recordNumber + ". It could not be < 0 and > "+size;
            throw new IllegalArgumentException(errMsg);
        }
        int recordPosition = start + recordNumber;
        if(recordPosition >= capacity) {
            recordPosition -= capacity;
        }
        return buffer[recordPosition * recordLength + positionWithinRecord];
    }
}
