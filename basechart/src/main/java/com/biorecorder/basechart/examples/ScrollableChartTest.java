package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.ScrollableChart;
import com.biorecorder.basechart.XYData;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.basechart.traces.LineTrace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by galafit on 27/9/18.
 */
public class ScrollableChartTest extends JFrame{
    IntArrayList yData1;
    IntArrayList yData2;
    IntArrayList xData;
    ChartPanel chartPanel;

    public ScrollableChartTest() {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        yData1 = new IntArrayList();
        yData2 = new IntArrayList();
        xData = new IntArrayList();

        for (int i = 0; i < 1600; i++) {
            yData1.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            yData2.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            xData.add(i);
        }

        XYData xyData1 = new XYData(0, 1);
        xyData1.addColumn(yData1);

        XYData xyData2 = new XYData();
        xyData2.addColumn(xData);
        xyData2.addColumn(yData2);

        ScrollableChart chart = new ScrollableChart();
        chart.addChartStack();
        chart.addChartTrace(0, new LineTrace(), xyData1, false, false);
        chart.addChartTrace(0, new LineTrace(), xyData2, false, false);
        chart.addChartStack();
        chart.addChartTrace(1, new LineTrace(), xyData1, false, false);


        chart.addPreviewStack();
        chart.addPreviewTrace(0, new LineTrace(), xyData1, false, false);
        chart.addPreviewTrace(0, new LineTrace(), xyData2, false, false);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void update() {
        for (int i = 1; i <= 80; i++) {
            yData1.add(100);
        }


        for (int i = 1; i <= 80; i++) {
            int lastValue = 0;
            if (xData.size() > 0) {
                lastValue = xData.get(xData.size() - 1);
            }
            xData.add(lastValue + 1);
        }
        for (int i = 1; i <= 80; i++) {
            yData2.add(i);
        }
        chartPanel.update();
    }


    public static void main(String[] args) {
        ScrollableChartTest chartTest = new ScrollableChartTest();

        final Timer timer = new Timer(10, new ActionListener() {
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter < 20) {
                    chartTest.update();
                    counter++;
                }
            }
        });
        timer.setInitialDelay(0);
        //  timer.start();

    }
}
