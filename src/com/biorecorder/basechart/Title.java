package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.*;

import java.util.ArrayList;

/**
 * Created by hdablin on 17.08.17.
 */
public class Title {
    private TitleConfig config;
    private String[] words = new String[0];
    private ArrayList<String> strings;

    public Title(String chartTitle, TitleConfig config) {
        if(chartTitle != null) {
            words = chartTitle.split(" ");
            this.config = config;
        }
    }

    public int getTitleHeight(BCanvas canvas, int areaWidth){
        if(words.length == 0) {
            return 0;
        }

        canvas.setTextStyle(config.getTextStyle());
        formStrings(canvas, areaWidth);
        int strHeight = canvas.getTextMetric(config.getTextStyle()).height();
        return strHeight * strings.size()
                + getInterLineSpace() * (strings.size() - 1)
                + config.getMargin().top() + config.getMargin().bottom();
    }

    private void formStrings(BCanvas canvas, int areaWidth){
        strings = new ArrayList<String>();
        StringBuilder resultantString = new StringBuilder(words[0]);
        TextMetric tm = canvas.getTextMetric(config.getTextStyle());
        for (int i = 1; i < words.length; i++) {
            int strWidth = tm.stringWidth(resultantString + " "+ words[i]);
            if ( strWidth + config.getMargin().left() + config.getMargin().right() > areaWidth ){
                strings.add(resultantString.toString());
                resultantString = new StringBuilder(words[i]);
            } else {
                resultantString.append(" ").append(words[i]);
            }
        }
        strings.add(resultantString.toString());
    }

    public void draw(BCanvas canvas, BRectangle area){
        if(words.length == 0) {
            return;
        }
        canvas.setTextStyle(config.getTextStyle());
        canvas.setColor(config.getTextColor());
        if (strings == null){
            formStrings(canvas, area.width);
        }
        Margin margin = config.getMargin();
        int y = area.y + margin.top();
        TextMetric tm = canvas.getTextMetric(config.getTextStyle());
        for (String string : strings) {
            int x = (area.x + area.width) / 2 - tm.stringWidth(string) / 2;
            if (x < area.x + margin.left()) {
                x = area.x + margin.left();
            }
            canvas.drawString(string,x,y + tm.ascent());
            y += getInterLineSpace() + tm.height();
        }

    }


    private  int getInterLineSpace() {
        return (int)(config.getTextStyle().getSize() * 0.2);
    }

}
