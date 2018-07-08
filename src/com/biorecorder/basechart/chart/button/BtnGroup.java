package com.biorecorder.basechart.chart.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/12/17.
 */
public class BtnGroup {
    private List<BtnModel> items = new ArrayList<BtnModel>();
    private BtnModel selection;


    public void add(BtnModel item) {
       items.add(item);
       item.setGroup(this);
    }

    public void setSelected(BtnModel item, boolean isSelected) {
        if (isSelected && item != null && item != selection) {
            BtnModel oldSelection = selection;
            selection = item;
            if (oldSelection != null) {
                oldSelection.setSelected(false);
            }

        }
        if (!isSelected && item != null && item == selection) {
            selection = null;
        }
    }

    public BtnModel getSelection() {
        return selection;
    }

    public boolean isSelected(BtnModel m) {
        return (m == selection);
    }
}
