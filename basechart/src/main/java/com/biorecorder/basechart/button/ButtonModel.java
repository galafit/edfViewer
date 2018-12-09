package com.biorecorder.basechart.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Implemented similar to swing ButtonModel (and  ButtonGroup).
 * Permit to select only one or none
 * button in the button group.
 */
public class ButtonModel {
    private List<StateListener> selectionListeners = new ArrayList<StateListener>();
    private ButtonGroup group;

    public void setGroup(ButtonGroup group) {
        this.group = group;
    }

    public void addListener(StateListener listener) {
       selectionListeners.add(listener);
    }

    public void setSelected(boolean isSelected) {
        if (group != null) {
            // use the group model instead
            boolean oldSelection = isSelected();
            if (oldSelection != isSelected) {
                group.setSelected(this, isSelected);
            }
            if (oldSelection != isSelected()) {
                for (StateListener listener : selectionListeners) {
                    listener.stateChanged(isSelected);
                }
            }
        }
    }

    public boolean isSelected() {
        if(group != null) {
            return group.isSelected(this);
        }
        return false;
    }
}
