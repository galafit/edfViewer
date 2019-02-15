package com.biorecorder.basechart;

import java.util.ArrayList;

/**
 * Created by hdablin on 02.08.17.
 */
public class TooltipInfo {
    private TooltipItem header;
    private ArrayList<TooltipItem> items = new ArrayList<TooltipItem>();

    public TooltipItem getHeader() {
        return header;
    }

    public void setHeader(TooltipItem header) {
        this.header = header;
    }

    public int getAmountOfItems(){
        return items.size();
    }

    public TooltipItem getItem(int index){
        return items.get(index);
    }

    public void addItems(TooltipItem... items){
        for (TooltipItem item : items) {
            this.items.add(item);
        }
    }
}
