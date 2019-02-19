package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.*;

import java.util.ArrayList;

/**
 * Created by hdablin on 02.08.17.
 */
public class Tooltip {
    private TooltipConfig tooltipConfig;
    private int x, y;
    private int y_offset = 2;
    private String separator = "  ";
    private TooltipItem header;
    private ArrayList<TooltipItem> items = new ArrayList<TooltipItem>();

    public Tooltip(TooltipConfig tooltipConfig, int x, int y) {
        this.tooltipConfig = tooltipConfig;
        this.x = x;
        this.y = y;
    }

    public void setHeader(TooltipItem header) {
        this.header = header;
    }

    public void addItems(TooltipItem... items){
        for (TooltipItem item : items) {
            this.items.add(item);
        }
    }


    public void draw(BCanvas canvas, BRectangle area) {
        canvas.setTextStyle(tooltipConfig.getTextStyle());
        BDimension tooltipDimension  = getTextSize(canvas);
        int tooltipAreaX = x - tooltipDimension.width / 2;
        int tooltipAreaY = y - tooltipDimension.height - y_offset;
        if (tooltipAreaX + tooltipDimension.width > area.x + area.width){
            tooltipAreaX = area.x + area.width - tooltipDimension.width;
        }
        if (tooltipAreaX < area.x){
            tooltipAreaX = area.x;
        }
        if (tooltipAreaY < area.y){
            tooltipAreaY = area.y;
        }
        if (tooltipAreaY + tooltipDimension.height > area.y + area.height ){
            tooltipAreaY = area.y + area.height - tooltipDimension.height;
        }

        BRectangle tooltipArea = new BRectangle(tooltipAreaX, tooltipAreaY, tooltipDimension.width, tooltipDimension.height);
        canvas.setColor(tooltipConfig.getBackgroundColor());
        canvas.fillRect(tooltipArea.x, tooltipArea.y, tooltipArea.width, tooltipArea.height);
        canvas.setColor(tooltipConfig.getBorderColor());
        canvas.setStroke(new BStroke(tooltipConfig.getBorderWidth()));
        canvas.drawRect(tooltipArea.x, tooltipArea.y, tooltipArea.width, tooltipArea.height);
        drawTooltipInfo(canvas, tooltipArea);
    }


    /**
     * https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
     */
    private void drawTooltipInfo(BCanvas canvas, BRectangle area) {
        Insets margin = tooltipConfig.getMargin();
        int stringHeght = canvas.getTextMetric(tooltipConfig.getTextStyle()).height();
        int lineSpace = getInterLineSpace();
        int x = area.x + margin.left();
        int y = area.y + margin.top();
        if (header != null) {
            drawItem(canvas, x, y, header);
            y += (lineSpace + stringHeght);
        }

        for (int i = 0; i < items.size(); i++) {
            drawItem(canvas, x, y, items.get(i));
            //g2.drawRect(x - margin.left(), y, area.width, stringHeght);
            y += (lineSpace + stringHeght);
        }
    }


    private void drawItem(BCanvas canvas, int x, int y, TooltipItem infoItem) {
        TextMetric tm = canvas.getTextMetric(tooltipConfig.getTextStyle());
        int string_y = y + tm.ascent();
        if (infoItem.getMarkColor() != null) {
            canvas.setColor(infoItem.getMarkColor());
            int colorMarkerSize = getColorMarkerSize();
            canvas.fillRect(x, y + (tm.height() - colorMarkerSize) / 2 + 1, colorMarkerSize, colorMarkerSize);
            x = x + colorMarkerSize + getColorMarkerPadding();
        }
        if (infoItem.getLabel() != null) {
            canvas.setColor(tooltipConfig.getColor());
            canvas.setTextStyle(tooltipConfig.getTextStyle());
            String labelString = infoItem.getLabel() + separator;
            canvas.drawString(labelString, x, string_y);
            x = x + tm.stringWidth(labelString);
        }
        if (infoItem.getValue() != null) {
            canvas.setColor(tooltipConfig.getColor());
            // font for value is always BOLD!
            TextStyle ts = tooltipConfig.getTextStyle();
            TextStyle boldTextStyle = new TextStyle(ts.getFontName(), TextStyle.BOLD, ts.getSize());
            canvas.setTextStyle(boldTextStyle);
            canvas.drawString(infoItem.getValue(), x, string_y);
        }
    }


    private int getColorMarkerSize() {
        return (int) (tooltipConfig.getTextStyle().getSize() * 0.8);
    }

    private int getColorMarkerPadding() {
        return (int) (tooltipConfig.getTextStyle().getSize() * 0.5);
    }

    private int getItemWidth(BCanvas canvas, TooltipItem infoItem) {
        String string = "";
        if (infoItem.getValue() != null) {
            string = infoItem.getValue();
        }
        if (infoItem.getLabel() != null) {
            string = infoItem.getLabel() + separator + string;
        }
        int itemWidth = canvas.getTextMetric(tooltipConfig.getTextStyle()).stringWidth(string);
        if (infoItem.getMarkColor() != null) {
            itemWidth = itemWidth + getColorMarkerPadding() + getColorMarkerSize();
        }

        return itemWidth;
    }

    private BDimension getTextSize(BCanvas canvas) {
        int textWidth = 0;

        for (int i = 0; i < items.size(); i++) {
            textWidth = Math.max(textWidth, getItemWidth(canvas, items.get(i)));
        }
        if (header != null) {
            textWidth = Math.max(textWidth, getItemWidth(canvas, header));
        }
        Insets margin = tooltipConfig.getMargin();
        textWidth += margin.left() + margin.right();
        int strHeight = canvas.getTextMetric(tooltipConfig.getTextStyle()).height();
        int textHeight = margin.top() + margin.bottom() + items.size() * strHeight;
        textHeight += getInterLineSpace() * (items.size() - 1);
        if (header != null) {
            textHeight += strHeight + getInterLineSpace();
        }
        return new BDimension(textWidth, textHeight);
    }

    private int getInterLineSpace() {
        return (int) (tooltipConfig.getTextStyle().getSize() * 0.2);
    }
}
