package com.biorecorder.basechart;

import java.util.ArrayList;

/**
 * Created by hdablin on 17.08.17.
 */
public class Title {
    private TextStyle textStyle;
    private BColor color;
    private String[] words = new String[0];
    private ArrayList<String> strings;

    public Title(String chartTitle, TextStyle textStyle, BColor color) {
        if(chartTitle != null) {
            words = chartTitle.split(" ");
            this.textStyle = textStyle;
            this.color = color;
        }
    }

    public int getTitleHeight(BCanvas canvas, int areaWidth){
        if(words.length == 0) {
            return 0;
        }
        canvas.setTextStyle(textStyle);
        formStrings(canvas, areaWidth);
        int strHeight = canvas.getTextMetric(textStyle).height();
        return strHeight * strings.size()
                + getInterLineSpace() * (strings.size() - 1)
                + getMargin().top() + getMargin().bottom();
    }

    private void formStrings(BCanvas canvas, int areaWidth){
        strings = new ArrayList<String>();
        StringBuilder resultantString = new StringBuilder(words[0]);
        TextMetric tm = canvas.getTextMetric(textStyle);
        for (int i = 1; i < words.length; i++) {
            int strWidth = tm.stringWidth(resultantString + " "+ words[i]);
            if ( strWidth + getMargin().left() + getMargin().right() > areaWidth ){
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
        canvas.setTextStyle(textStyle);
        canvas.setColor(color);
        if (strings == null){
            formStrings(canvas, area.width);
        }
        int y = area.y + getMargin().top();
        TextMetric tm = canvas.getTextMetric(textStyle);
        for (String string : strings) {
            int x = (area.x + area.width) / 2 - tm.stringWidth(string) / 2;
            if (x < area.x + getMargin().left()) {
                x = area.x + getMargin().left();
            }
            canvas.drawString(string,x,y + tm.ascent());
            y += getInterLineSpace() + tm.height();
        }

    }


    private  int getInterLineSpace() {
        return (int)(textStyle.getSize() * 0.2);
    }

    private Margin getMargin(){
       /* return new Margin((int)(textStyle.getSize() * 0),
                (int)(textStyle.getSize() * 0.5),
                (int)(textStyle.getSize() * 0.5),
                (int)(textStyle.getSize() * 0.5));*/
        return new Margin(0, (int)(textStyle.getSize() * 0.5), 0, (int)(textStyle.getSize() * 0.5));
    }
}
