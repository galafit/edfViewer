package com.biorecorder.basechart.scales;

/**
 * Created by galafit on 5/9/17.
 */
public interface TickProvider {
    public Tick getUpperTick(double value);
    public Tick getLowerTick(double value);
    public Tick getNextTick();
    public Tick getPreviousTick();
    public void increaseTickStep(int increaseFactor);
}
