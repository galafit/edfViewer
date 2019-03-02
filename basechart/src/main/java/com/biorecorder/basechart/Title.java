package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by hdablin on 17.08.17.
 */
public class Title {
    private String title;
    private TitleConfig config;
    private ArrayList<BText> lines = new ArrayList<BText>();
    private BRectangle area;
    private BRectangle bounds;

    public Title(TitleConfig config) {
        this.config = config;
    }

    private void setDirty() {
        lines.clear();
        bounds = null;
    }

    private boolean isDirty() {
        return bounds == null;
    }

    public BRectangle getBounds(BCanvas canvas) {
        if(isDirty()) {
            formLines(canvas);
        }
        return bounds;
    }

    public boolean isNullOrBlank() {
        return StringUtils.isNullOrBlank(title);
    }

    public void setTitle(String title) {
        this.title = title;
        setDirty();
    }

    public void setConfig(TitleConfig config) {
        this.config = config;
        setDirty();
    }


    public void setArea(BRectangle area) {
        this.area = area;
        setDirty();
    }

    private void formLines(BCanvas canvas){
        if(title == null || StringUtils.isNullOrBlank(title)) {
            bounds = new BRectangle(0, 0, 0, 0);
            return;
        }
        if(area == null) {
            area = canvas.getBounds();
        }
        String[] words = title.split(" ");
        StringBuilder stringBuilder = new StringBuilder(words[0]);
        TextMetric tm = canvas.getTextMetric(config.getTextStyle());
        Insets margin = config.getMargin();
        int y = area.y + margin.top();
        int x;
        for (int i = 1; i < words.length; i++) {
            int strWidth = tm.stringWidth(stringBuilder + " "+ words[i]);
            if ( strWidth + margin.left() + margin.right() > area.width ){
                String lineString = stringBuilder.toString();
                x = (area.x + area.width) / 2 - tm.stringWidth(lineString) / 2;
                if (x < area.x + margin.left()) {
                    x = area.x + margin.left();
                }
                lines.add(new BText(lineString, x, y + tm.ascent()));

                y += config.getInterLineSpace() + tm.height();
                stringBuilder = new StringBuilder(words[i]);
            } else {
                stringBuilder.append(" ").append(words[i]);
            }
        }

        // last line
        String lineString = stringBuilder.toString();
        x = (area.x + area.width) / 2 - tm.stringWidth(lineString) / 2;
        if (x < area.x + margin.left()) {
            x = area.x + margin.left();
        }

        lines.add(new BText(lineString, x, y + tm.ascent()));

        int height = tm.height() * lines.size()
                + config.getInterLineSpace() * (lines.size() - 1)
                + margin.top() + margin.bottom();

        bounds = new BRectangle(area.x, area.y, area.width, height);
    }

    public void draw(BCanvas canvas){
        if(isDirty()) {
            formLines(canvas);
        }
        if (lines.size() == 0){
            return;
        }
        canvas.setTextStyle(config.getTextStyle());
        canvas.setColor(config.getTextColor());
        for (BText string : lines) {
            string.draw(canvas);
        }
    }

}
