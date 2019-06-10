package com.biorecorder.basechart.axis;

import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 5/9/17.
 */
public class AxisPrefixAndSuffix {
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
    private String prefix;
    private String suffix;

    public AxisPrefixAndSuffix(@Nullable String prefix, @Nullable String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
