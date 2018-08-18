package com.biorecorder.basechart.swing;

import com.biorecorder.basechart.ScrollableChart;
import com.biorecorder.basechart.config.ChartConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.swing.SwingCanvas;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdablin on 23.06.17.
 */
public class ChartPanel extends JPanel implements KeyListener {
    int scrollPointsPerRotation = 10;
    // во сколько раз растягивается или сжимается ось при автозуме
    private float defaultZoom = 2;
    private int pastX;
    private int pastY;
    private boolean isPressedInsideScroll;
    private List<Integer> xAxisList = new ArrayList<>();
    private List<Integer> yAxisList = new ArrayList<>();
    private List<Integer> yAxisListPreview = new ArrayList<>();
    private final ScrollableChart chart;

    public ChartPanel(ScrollableChart chart1) {
        this.chart = chart1;

        BRectangle startArea = new BRectangle(0, 0, 200, 200);
        chart.setArea(startArea);

        BColor bg = chart.getChartConfig().getMarginColor();
        setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (chart.chartContains(e.getX(), e.getY())) {
                        if (chart.chartHoverOn(e.getX(), e.getY(), chart.getChartSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                    if (chart.previewContains(e.getX(), e.getY())) {
                        if (chart.previewHoverOn(e.getX(), e.getY(), chart.getPreviewSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                } else {
                    int dy = pastY - e.getY();
                    int dx = e.getX() - pastX;
                    pastX = e.getX();
                    pastY = e.getY();
                    if (isPressedInsideScroll) {
                        if (chart.translateScrolls(dx)) {
                            repaint();
                        }
                    } else {
                        if (e.isAltDown()
                                || e.isControlDown()
                                // || e.isShiftDown()
                                || e.isMetaDown()) { // zoomChartY
                            zoomY(dy);
                            repaint();
                        } else { // tranlate X and Y
                            // translateX(dx);
                            translateY(dy);
                            repaint();
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateXAxisList();
                    updateYAxisList();
                    if(chart.chartContains(e.getX(), e.getY())) {
                        autoscaleChartY();
                        autoscaleChartX();
                    }
                    if(chart.isPreviewEnabled() && chart.previewContains(e.getX(), e.getY())) {
                        autoscalePreviewY();
                    }

                    repaint();
                }
                if (e.getClickCount() == 1) {
                    if (chart.chartContains(e.getX(), e.getY()) && chart.selectChartTrace(e.getX(), e.getY())) {
                        repaint();
                    }
                    if (chart.isPreviewEnabled() && chart.previewContains(e.getX(), e.getY()) && (chart.selectPreviewTrace(e.getX(), e.getY()) || chart.setScrollsPosition(e.getX(), e.getY()))) {
                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (chart.chartContains(e.getX(), e.getY())) {
                        if (chart.chartHoverOn(e.getX(), e.getY(), chart.getChartSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                    if (chart.isPreviewEnabled() && chart.previewContains(e.getX(), e.getY())) {
                        if (chart.previewHoverOn(e.getX(), e.getY(), chart.getPreviewSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                } else {
                    pastX = e.getX();
                    pastY = e.getY();
                    if (chart.isPreviewEnabled() && chart.isPointInsideScroll(e.getX(), e.getY())) {
                        isPressedInsideScroll = true;
                    } else {
                        isPressedInsideScroll = false;
                        updateXAxisList(e.getX(), e.getY());
                        updateYAxisList(e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (chart.chartContains(e.getX(), e.getY())) {
                    if (chart.chartHoverOff()) {
                        repaint();
                    }
                }
                if (chart.isPreviewEnabled() && chart.previewContains(e.getX(), e.getY())) {
                    if (chart.previewHoverOff()) {
                        repaint();
                    }
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.consume(); // avoid the event to be triggered twice
                updateXAxisList(e.getX(), e.getY());
                if (e.isAltDown()
                        || e.isControlDown()
                        //    || e.isShiftDown() // JAVA BUG on MAC!!!!
                        || e.isMetaDown()) { // zoomChartX
                    zoomX(e.getWheelRotation());
                    repaint();
                } else { // translateScrolls X
                    translateX(e.getWheelRotation() * scrollPointsPerRotation);
                    repaint();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = getBounds();
                BRectangle area = new BRectangle(0, 0, bounds.width, bounds.height);
                chart.setArea(area);
                repaint();
            }
        });
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        updateXAxisList();
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            translateX(scrollPointsPerRotation);
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            translateX(-scrollPointsPerRotation);
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void updateXAxisList(int x, int y) {
        int selectedTraceIndex = chart.getChartSelectedTraceIndex();
        xAxisList = new ArrayList<>(1);
        if (selectedTraceIndex >= 0) {
            xAxisList.add(chart.getChartTraceXIndex(selectedTraceIndex));
        } else {
            int xAxisIndex = chart.getChartXIndex(x, y);
            if (xAxisIndex >= 0) {
                xAxisList.add(chart.getChartXIndex(x, y));
            }
        }
    }

    private void updateXAxisList() {
        int selectedTraceIndex = chart.getChartSelectedTraceIndex();
        if (selectedTraceIndex >= 0) {
            xAxisList = new ArrayList<>(1);
            xAxisList.add(chart.getChartTraceXIndex(selectedTraceIndex));
        } else {
            xAxisList = new ArrayList<>(chart.getChartXAxisCounter());
            for (int i = 0; i < chart.getChartXAxisCounter(); i++) {
                xAxisList.add(i);
            }
        }
    }

    private void updateYAxisList(int x, int y) {
        if (chart.isPointInsideChart(x, y)) {
            int chartSelectedTraceIndex = chart.getChartSelectedTraceIndex();
            yAxisList = new ArrayList<>(1);
            if (chartSelectedTraceIndex >= 0) {
                yAxisList.add(chart.getChartTraceYIndex(chartSelectedTraceIndex));
            } else {
                int yAxisIndex = chart.getChartYIndex(x, y);
                if (yAxisIndex >= 0) {
                    yAxisList.add(yAxisIndex);
                }
            }
            yAxisListPreview = new ArrayList<>(0);
        }

        if (chart.previewContains(x, y)) {
            int previewSelectedTraceIndex = chart.getPreviewSelectedTraceIndex();
            yAxisListPreview = new ArrayList<>(1);
            if (previewSelectedTraceIndex >= 0) {
                yAxisListPreview.add(chart.getPreviewTraceYIndex(previewSelectedTraceIndex));
            } else {
                int yAxisIndex = chart.getPreviewYIndex(x, y);
                if (yAxisIndex >= 0)
                    yAxisListPreview.add(yAxisIndex);
            }
            yAxisList = new ArrayList<>(0);
        }
    }

    private void updateYAxisList() {
        int chartSelectedTraceIndex = chart.getChartSelectedTraceIndex();
        if (chartSelectedTraceIndex >= 0) {
            yAxisList = new ArrayList<>(1);
            yAxisList.add(chart.getChartTraceYIndex(chartSelectedTraceIndex));
        } else {
            yAxisList = new ArrayList<>(chart.getChartYAxisCounter());
            for (int i = 0; i < chart.getChartYAxisCounter(); i++) {
                yAxisList.add(i);
            }
        }

        int previewSelectedTraceIndex = chart.getPreviewSelectedTraceIndex();
        if (previewSelectedTraceIndex >= 0) {
            yAxisListPreview = new ArrayList<>(1);
            yAxisListPreview.add(chart.getPreviewTraceYIndex(previewSelectedTraceIndex));
        } else {
            yAxisListPreview = new ArrayList<>(chart.getPreviewYAxisCounter());
            for (int i = 0; i < chart.getPreviewYAxisCounter(); i++) {
                yAxisListPreview.add(i);
            }
        }
    }


    private void translateY(int dy) {
        for (Integer yAxisIndex : yAxisList) {
            chart.translateChartY(yAxisIndex, dy);
        }
        for (Integer yAxisIndex : yAxisListPreview) {
            chart.translatePreviewY(yAxisIndex, dy);
        }
    }

    private void translateX(int dx) {
        for (Integer xAxisIndex : xAxisList) {
            chart.translateChartX(xAxisIndex, dx);
        }
    }

    private void zoomY(int dy) {
        for (Integer yAxisIndex : yAxisList) {
            // scaling relative to the stack
            float zoomFactor = 1 + defaultZoom * dy / chart.getChartYStartEnd(yAxisIndex).length();
            chart.zoomChartY(yAxisIndex, zoomFactor);
        }
        for (Integer yAxisIndex : yAxisListPreview) {
            // scaling relative to the stack
            float zoomFactor = 1 + defaultZoom * dy / chart.getChartYStartEnd(yAxisIndex).length();
            chart.zoomPreviewY(yAxisIndex, zoomFactor);
        }
    }

    private void zoomX(int dx) {
        float zoomFactor = 1 + defaultZoom * dx / 100;
        for (Integer xAxisIndex : xAxisList) {
            chart.zoomChartX(xAxisIndex, zoomFactor);
        }
    }

    private void autoscaleChartX() {
        for (Integer xAxisIndex : xAxisList) {
            chart.autoScaleChartX(xAxisIndex);
        }
    }

    private void autoscaleChartY() {
        for (Integer yAxisIndex : yAxisList) {
            chart.autoScaleChartY(yAxisIndex);
        }
    }

    private void autoscalePreviewY() {
        for (Integer yAxisIndex : yAxisListPreview) {
            chart.autoScalePreviewY(yAxisIndex);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        chart.draw(new SwingCanvas((Graphics2D) g));
    }

    public void update() {
        chart.update();
        repaint();
    } 
}
