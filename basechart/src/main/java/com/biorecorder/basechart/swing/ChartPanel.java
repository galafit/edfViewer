package com.biorecorder.basechart.swing;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.graphics.BRectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartPanel extends JPanel implements KeyListener {
    InteractiveDrawable chart;

    int scrollPointsPerRotation = 10;
    // во сколько раз растягивается или сжимается ось при автозуме
    private float defaultZoom = 2;
    private BPoint pressPoint;
    private int pastX;
    private int pastY;
    private boolean isXDirection;
    private boolean isYDirection;


    public ChartPanel(Chart chart1) {
        this.chart = new InteractiveChart(chart1);
        init();
    }

    public ChartPanel(NavigableChart chart1) {
        this.chart = new InteractiveNavigableChart(chart1);
        init();
    }

    private void init() {
        BRectangle startArea = new BRectangle(0, 0, 200, 200);
        chart.onResize(startArea);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if(chart.onLongPress(e.getX(), e.getY())) {
                        repaint();
                    }
                } else {
                    int dy = pastY - e.getY();
                    int dx = pastX - e.getX();

                    pastX = e.getX();
                    pastY = e.getY();
                    if(!isXDirection && !isYDirection) {
                        if(Math.abs(dy) >= Math.abs(dx)) {
                            isYDirection = true;
                        } else {
                            isXDirection = true;
                        }
                    }

                    if (e.isAltDown()
                            || e.isControlDown()
                            // || e.isShiftDown()
                            || e.isMetaDown()) { // zoom

                        if(chart.onScaleY(pressPoint, distanceToScaleFactor(dy))) {
                            repaint();
                        }
                    } else { // scroll
                        if(isYDirection) {
                            if(chart.onScrollY(pressPoint, dy)) {
                                repaint();
                            }
                        } else {
                            if(chart.onScrollX(pressPoint, dx)) {
                                repaint();
                            }
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if(chart.onDoubleTap(e.getX(), e.getY())) {
                        repaint();
                    }
                }
                if (e.getClickCount() == 1) {
                    if(chart.onTap(e.getX(), e.getY())) {
                        repaint();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if(chart.onLongPress(e.getX(), e.getY())) {
                        repaint();
                    }
                } else {
                    pastX = e.getX();
                    pastY = e.getY();
                    pressPoint = new BPoint(e.getX(), e.getY());
                    isXDirection = false;
                    isYDirection = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressPoint = null;
                if(chart.onTapUp(e.getX(), e.getY())) {
                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.consume(); // avoid the event to be triggered twice
                int dx = e.getWheelRotation() * scrollPointsPerRotation;
                if (e.isAltDown()
                        || e.isControlDown()
                        //    || e.isShiftDown() // JAVA BUG on MAC!!!!
                        || e.isMetaDown()) { // scaleX
                    if(chart.onScaleX(null, distanceToScaleFactor(dx))) {
                        repaint();
                    }

                } else { // translateScrolls X
                    if (chart.onScrollX(null, dx)) {
                        repaint();
                    }
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = getBounds();
                BRectangle area = new BRectangle(0, 0, bounds.width, bounds.height);
                chart.onResize(area);
                repaint();
            }
        });
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int dx = 0;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            dx = scrollPointsPerRotation;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            dx = -scrollPointsPerRotation;

        }
        if (chart.onScrollX(null, dx)) {
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private double distanceToScaleFactor(int distance) {
        return 1 + defaultZoom * distance / 100;
    }

    public void update() {
        if (chart.update()) {
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        chart.draw(new SwingCanvas((Graphics2D) g));
    }
}
