package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.axis.XAxisPosition;
import com.biorecorder.basechart.axis.YAxisPosition;
import com.biorecorder.basechart.data.XYData;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scales.CategoryScale;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.basechart.themes.WhiteTheme;
import com.biorecorder.basechart.traces.LineTracePainter;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by galafit on 27/9/18.
 */
public class NavigableChartTest extends JFrame{
    IntArrayList yData;
    IntArrayList xData;
    java.util.List<String> labels = new ArrayList();
    ChartPanel chartPanel;
    NavigableChart chart;
    XYData xyData;
    XYData xyData1;

    public NavigableChartTest() {
        int width = 400;
        int height = 500;

        setTitle("Test chart");

        yData = new IntArrayList();
        xData = new IntArrayList();

     /*  for (int i = 0; i <= 10; i++) {
            yData.add(i);
            xData.add(i);
            labels.add("l_"+i);
        }*/


        xyData = new XYData(xData, true);
        xyData.addYColumn("y", yData);

        xyData1 = new XYData(0, 1, true);
        xyData1.addYColumn("regular1", xData);
        xyData1.addYColumn("regular2", yData);

        chart = new NavigableChart(new WhiteTheme(true).getNavigableChartConfig());

       /* DataProcessingConfig navigatorProcessing = new DataProcessingConfig();
        double[] groupingIntervals = {20, 40};
        navigatorProcessing.setGroupingIntervals(groupingIntervals);
        navigatorProcessing.setGroupingForced(true);
       */
        chart.addChartTrace(xyData, new LineTracePainter(), true);

        chart.addNavigatorTrace(xyData, new LineTracePainter(), true);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        Thread t1 = new Thread(new Runnable() {
            int interval = 2000;
            @Override
            public void run() {

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                chart.setChartTraceColor(1, BColor.RED);
                chart.setChartTraceName(1, "new Name");
                chartPanel.repaint();

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NavigableChartConfig config = new DarkTheme().getNavigableChartConfig();
                config.setGap(20);
                chart.setConfig(config);
                chartPanel.repaint();
            }
        });
        //t1.start();

        Thread t = new Thread(new Runnable() {
            int interval = 1000;
            @Override
            public void run() {
                for (int count = 0; count < 2; count++) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int yDataLast = 0;
                    int xDataLast = 0;
                    if(yData.size() > 0) {
                        yDataLast = yData.get(yData.size() - 1);
                    }
                    if(xData.size() > 0) {
                        xDataLast = xData.get(xData.size() - 1);

                    }

                    for (int i = 1; i <= 1000; i++) {
                        yData.add(i + yDataLast);
                        xData.add(i + xDataLast);
                        labels.add("label_"+(i + xDataLast));

                    }
                    System.out.println("\ndata size: "+yData.size());

                    if(count == 1) {
                        chart.addChartStack();
                        chart.addChartTrace(xyData1, new LineTracePainter(), true);
                    }

                    if(count == 4) {
                        chart.removeChartTrace(0);
                    }

                    chart.appendData();
                    chartPanel.repaint();
                }
            }
        });
       t.start();
    }


    public static void main(String[] args) {
        NavigableChartTest chartTest = new NavigableChartTest();
    }
}
