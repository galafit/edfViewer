package com.biorecorder.edfviewer;


import java.util.List;

class ChannelData {
    private int[] buffer;
    private List<DigitalFilter> filters;

    public ChannelData(int bufferSize) {
        buffer = new int[bufferSize];
    }
}
