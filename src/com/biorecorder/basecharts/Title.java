package com.biorecorder.basecharts;

import com.biorecorder.basecharts.graphics.*;

import java.util.ArrayList;

/**
 * Created by hdablin on 17.08.17.
 */
public class Title {
    private TitleConfig config;
    private ArrayList<Text> lines = new ArrayList<Text>();
    private BRectangle bounds = new BRectangle(0, 0, 0, 0);


    public Title(String title, TitleConfig config, BRectangle area, BCanvas canvas) {
        this.config = config;
        formLines(canvas, title, area);
    }


    public BRectangle getBounds() {
        return bounds;
    }

    private void formLines(BCanvas canvas, String title, BRectangle area){
        if(title == null) {
            return;
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
                lines.add(new Text(lineString, x, y + tm.ascent()));

                y += getInterLineSpace() + tm.height();
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

        lines.add(new Text(lineString, x, y + tm.ascent()));

        int height = tm.height() * lines.size()
                + getInterLineSpace() * (lines.size() - 1)
                + margin.top() + margin.bottom();

        bounds = new BRectangle(area.x, area.y, area.width, height);
    }

    public void draw(BCanvas canvas){
        if (lines.size() == 0){
            return;
        }
        canvas.setTextStyle(config.getTextStyle());
        canvas.setColor(config.getTextColor());
        for (Text string : lines) {
            string.draw(canvas);
        }
    }


    private  int getInterLineSpace() {
        return (int)(config.getTextStyle().getSize() * 0.2);
    }

}
