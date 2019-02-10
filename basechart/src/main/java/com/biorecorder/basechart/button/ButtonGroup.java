package com.biorecorder.basechart.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/12/17.
 */
public class ButtonGroup {
    private List<ButtonModel> items = new ArrayList<ButtonModel>();
    private ButtonModel selection;


    public void add(ButtonModel item) {
       items.add(item);
       item.setGroup(this);
    }

    public void remove(ButtonModel item) {
        if(selection == item) {
            selection = null;
        }
        items.remove(item);
    }

    public void setSelected(ButtonModel item, boolean isSelected) {
        if (isSelected && item != null && item != selection) {
            ButtonModel oldSelection = selection;
            selection = item;
            if (oldSelection != null) {
                oldSelection.setSelected(false);
            }

        }
        if (!isSelected && item != null && item == selection) {
            selection = null;
        }
    }

    public ButtonModel getSelection() {
        return selection;
    }

    public boolean isSelected(ButtonModel m) {
        return (m == selection);
    }
}
