package com.biorecorder.edfviewer;

/**
 * Any LINEAR transformation
 */
public interface DigitalFilter {
    double filteredValue(double inputValue);
}
