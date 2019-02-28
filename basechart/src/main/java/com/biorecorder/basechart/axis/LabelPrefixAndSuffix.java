package com.biorecorder.basechart.axis;

import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 5/9/17.
 */
public class LabelPrefixAndSuffix {
    private String prefix;
    private String suffix;

    public LabelPrefixAndSuffix(@Nullable String prefix, @Nullable String suffix) {
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
