package com.biorecorder.basecharts.axis;

/**
 * Created by galafit on 5/9/17.
 */
public class TickFormatInfo {
    private String prefix;
    private String suffix;
    /**
     * At the moment not used
     tera 	T 	1,000,000,000,000 	10x12
     giga 	G 	1,000,000,000 	10x9
     mega 	M 	1,000,000 	10x6
     kilo 	k 	1,000 	10x3

     milli 	m 	0.001 	10x¯3
     micro 	µ 	0.000001 	10x¯6
     nano 	n 	0.000000001 	10x¯9
     */
    private boolean enableExponentShortcut = true;

    public TickFormatInfo() {
    }

    public TickFormatInfo(TickFormatInfo labelFormatInfo) {
        prefix = labelFormatInfo.prefix;
        suffix = labelFormatInfo.suffix;
        enableExponentShortcut = labelFormatInfo.enableExponentShortcut;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
