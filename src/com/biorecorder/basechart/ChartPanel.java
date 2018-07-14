package com.biorecorder.basechart;

import com.biorecorder.basechart.chart.BColor;
import com.biorecorder.basechart.chart.BRectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdablin on 23.06.17.
 */
public class ChartPanel { //extends JPanel implements KeyListener {
    int scrollPointsPerRotation = 10;
    // во сколько раз растягивается или сжимается ось при автозуме
    private float defaultZoom = 2;
    private int pastX;
    private int pastY;
    private boolean isPressedInsideScroll;
    private List<Integer> xAxisList = new ArrayList<>();
    private List<Integer> yAxisList = new ArrayList<>();
    private List<Integer> yAxisListPreview = new ArrayList<>();
    private ChartWithDataManager chartDataManager;

/*    public ChartPanel(ChartConfig config) {
        BColor bg = config.getChartConfig().getMarginColor();
        setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (chartDataManager.getScrollableChart().chartContains(e.getX(), e.getY())) {
                        if (chartDataManager.getScrollableChart().chartHoverOn(e.getX(), e.getY(), chartDataManager.getScrollableChart().getChartSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                    if (chartDataManager.getScrollableChart().previewContains(e.getX(), e.getY())) {
                        if (chartDataManager.getScrollableChart().previewHoverOn(e.getX(), e.getY(), chartDataManager.getScrollableChart().getPreviewSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                } else {
                    int dy = pastY - e.getY();
                    int dx = e.getX() - pastX;
                    pastX = e.getX();
                    pastY = e.getY();
                    if (isPressedInsideScroll) {
                        if (chartDataManager.getScrollableChart().translateScrolls(dx)) {
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
                    if(chartDataManager.getScrollableChart().chartContains(e.getX(), e.getY())) {
                        autoscaleChartY();
                        autoscaleChartX();
                    }
                    if(chartDataManager.getScrollableChart().previewContains(e.getX(), e.getY())) {
                        autoscalePreviewY();
                    }

                    repaint();
                }
                if (e.getClickCount() == 1) {
                    if (chartDataManager.getScrollableChart().chartContains(e.getX(), e.getY()) && chartDataManager.getScrollableChart().selectChartTrace(e.getX(), e.getY())) {
                        repaint();
                    }
                    if (chartDataManager.getScrollableChart().previewContains(e.getX(), e.getY()) && (chartDataManager.getScrollableChart().selectPreviewTrace(e.getX(), e.getY()) || chartDataManager.getScrollableChart().setScrollsPosition(e.getX(), e.getY()))) {
                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (chartDataManager.getScrollableChart().chartContains(e.getX(), e.getY())) {
                        if (chartDataManager.getScrollableChart().chartHoverOn(e.getX(), e.getY(), chartDataManager.getScrollableChart().getChartSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                    if (chartDataManager.getScrollableChart().previewContains(e.getX(), e.getY())) {
                        if (chartDataManager.getScrollableChart().previewHoverOn(e.getX(), e.getY(), chartDataManager.getScrollableChart().getPreviewSelectedTraceIndex())) {
                            repaint();
                        }
                    }
                } else {
                    pastX = e.getX();
                    pastY = e.getY();
                    if (chartDataManager.getScrollableChart().isPointInsideScroll(e.getX(), e.getY())) {
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
                if (chartDataManager.getScrollableChart().chartContains(e.getX(), e.getY())) {
                    if (chartDataManager.getScrollableChart().chartHoverOff()) {
                        repaint();
                    }
                }
                if (chartDataManager.getScrollableChart().previewContains(e.getX(), e.getY())) {
                    if (chartDataManager.getScrollableChart().previewHoverOff()) {
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
                if (chartDataManager == null) {
                    chartDataManager = new ChartWithDataManager(config, area);
                } else {
                    chartDataManager.getScrollableChart().setArea(area);
                }
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
        int selectedTraceIndex = chartDataManager.getScrollableChart().getChartSelectedTraceIndex();
        xAxisList = new ArrayList<>(1);
        if (selectedTraceIndex >= 0) {
            xAxisList.add(chartDataManager.getScrollableChart().getChartTraceXIndex(selectedTraceIndex));
        } else {
            int xAxisIndex = chartDataManager.getScrollableChart().getChartXIndex(x, y);
            if (xAxisIndex >= 0) {
                xAxisList.add(chartDataManager.getScrollableChart().getChartXIndex(x, y));
            }
        }
    }

    private void updateXAxisList() {
        int selectedTraceIndex = chartDataManager.getScrollableChart().getChartSelectedTraceIndex();
        if (selectedTraceIndex >= 0) {
            xAxisList = new ArrayList<>(1);
            xAxisList.add(chartDataManager.getScrollableChart().getChartTraceXIndex(selectedTraceIndex));
        } else {
            xAxisList = new ArrayList<>(chartDataManager.getScrollableChart().getChartXAxisCounter());
            for (int i = 0; i < chartDataManager.getScrollableChart().getChartXAxisCounter(); i++) {
                xAxisList.add(i);
            }
        }
    }

    private void updateYAxisList(int x, int y) {
        if (chartDataManager.getScrollableChart().isPointInsideChart(x, y)) {
            int chartSelectedTraceIndex = chartDataManager.getScrollableChart().getChartSelectedTraceIndex();
            yAxisList = new ArrayList<>(1);
            if (chartSelectedTraceIndex >= 0) {
                yAxisList.add(chartDataManager.getScrollableChart().getChartTraceYIndex(chartSelectedTraceIndex));
            } else {
                int yAxisIndex = chartDataManager.getScrollableChart().getChartYIndex(x, y);
                if (yAxisIndex >= 0) {
                    yAxisList.add(yAxisIndex);
                }
            }
            yAxisListPreview = new ArrayList<>(0);
        }

        if (chartDataManager.getScrollableChart().previewContains(x, y)) {
            int previewSelectedTraceIndex = chartDataManager.getScrollableChart().getPreviewSelectedTraceIndex();
            yAxisListPreview = new ArrayList<>(1);
            if (previewSelectedTraceIndex >= 0) {
                yAxisListPreview.add(chartDataManager.getScrollableChart().getPreviewTraceYIndex(previewSelectedTraceIndex));
            } else {
                int yAxisIndex = chartDataManager.getScrollableChart().getPreviewYIndex(x, y);
                if (yAxisIndex >= 0)
                    yAxisListPreview.add(yAxisIndex);
            }
            yAxisList = new ArrayList<>(0);
        }
    }

    private void updateYAxisList() {
        int chartSelectedTraceIndex = chartDataManager.getScrollableChart().getChartSelectedTraceIndex();
        if (chartSelectedTraceIndex >= 0) {
            yAxisList = new ArrayList<>(1);
            yAxisList.add(chartDataManager.getScrollableChart().getChartTraceYIndex(chartSelectedTraceIndex));
        } else {
            yAxisList = new ArrayList<>(chartDataManager.getScrollableChart().getChartYAxisCounter());
            for (int i = 0; i < chartDataManager.getScrollableChart().getChartYAxisCounter(); i++) {
                yAxisList.add(i);
            }
        }

        int previewSelectedTraceIndex = chartDataManager.getScrollableChart().getPreviewSelectedTraceIndex();
        if (previewSelectedTraceIndex >= 0) {
            yAxisListPreview = new ArrayList<>(1);
            yAxisListPreview.add(chartDataManager.getScrollableChart().getPreviewTraceYIndex(previewSelectedTraceIndex));
        } else {
            yAxisListPreview = new ArrayList<>(chartDataManager.getScrollableChart().getPreviewYAxisCounter());
            for (int i = 0; i < chartDataManager.getScrollableChart().getPreviewYAxisCounter(); i++) {
                yAxisListPreview.add(i);
            }
        }
    }


    private void translateY(int dy) {
        for (Integer yAxisIndex : yAxisList) {
            chartDataManager.getScrollableChart().translateChartY(yAxisIndex, dy);
        }
        for (Integer yAxisIndex : yAxisListPreview) {
            chartDataManager.getScrollableChart().translatePreviewY(yAxisIndex, dy);
        }
    }

    private void translateX(int dx) {
        for (Integer xAxisIndex : xAxisList) {
            chartDataManager.getScrollableChart().translateChartX(xAxisIndex, dx);
        }
    }

    private void zoomY(int dy) {
        for (Integer yAxisIndex : yAxisList) {
            // scaling relative to the stack
            float zoomFactor = 1 + defaultZoom * dy / chartDataManager.getScrollableChart().getChartYStartEnd(yAxisIndex).length();
            chartDataManager.getScrollableChart().zoomChartY(yAxisIndex, zoomFactor);
        }
        for (Integer yAxisIndex : yAxisListPreview) {
            // scaling relative to the stack
            float zoomFactor = 1 + defaultZoom * dy / chartDataManager.getScrollableChart().getChartYStartEnd(yAxisIndex).length();
            chartDataManager.getScrollableChart().zoomPreviewY(yAxisIndex, zoomFactor);
        }
    }

    private void zoomX(int dx) {
        float zoomFactor = 1 + defaultZoom * dx / 100;
        for (Integer xAxisIndex : xAxisList) {
            chartDataManager.getScrollableChart().zoomChartX(xAxisIndex, zoomFactor);
        }
    }

    private void autoscaleChartX() {
        for (Integer xAxisIndex : xAxisList) {
            chartDataManager.getScrollableChart().autoScaleChartX(xAxisIndex);
        }
    }

    private void autoscaleChartY() {
        for (Integer yAxisIndex : yAxisList) {
            chartDataManager.getScrollableChart().autoScaleChartY(yAxisIndex);
        }
    }

    private void autoscalePreviewY() {
        for (Integer yAxisIndex : yAxisListPreview) {
            chartDataManager.getScrollableChart().autoScalePreviewY(yAxisIndex);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        chartDataManager.getScrollableChart().draw(new SwingCanvas((Graphics2D) g));
    }

    public void update() {
        chartDataManager.update();
        repaint();
    } */
}
