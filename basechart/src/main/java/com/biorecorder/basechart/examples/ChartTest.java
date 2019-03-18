package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.basechart.themes.WhiteTheme;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.frame.DataFrame;
import com.biorecorder.data.frame.SquareFunction;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {

    IntArrayList yUnsort = new IntArrayList();
    IntArrayList xUnsort = new IntArrayList();

    IntArrayList xData = new IntArrayList();
    IntArrayList yData1 = new IntArrayList();
    IntArrayList yData2 = new IntArrayList();
    IntArrayList yData3 = new IntArrayList();
    Chart chart;
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        for (int i = 0; i < 15; i++) {
            yData1.add(i);
            yData2.add(i + 20);
            yData3.add(i + 50);
        }

        for (int i = 0; i < 15; i++) {
            xData.add(i);
        }


        xUnsort.add(50);
        xUnsort.add(300);
        xUnsort.add(200);
        xUnsort.add(100);
        xUnsort.add(150);
        xUnsort.add(20);

        yUnsort.add(100);
        yUnsort.add(200);
        yUnsort.add(150);
        yUnsort.add(10);
        yUnsort.add(300);
        yUnsort.add(300);



        XYData xyData2 = new XYData();
        xyData2.addColumn(xData);
        xyData2.addColumn(yData1);
        xyData2.addColumn(new SquareFunction(), 1);
        xyData2.setColumnName(2, "eeg");
        //xyData2.addColumn(yData2);
        //xyData2.addColumn(yData3);


        XYData xyData3 = new XYData();
        xyData3.addColumn(xUnsort);
        xyData3.addColumn(yUnsort);


        XYData regularData = new XYData(0, 1);
        regularData.addColumn(yData1);
        XYData noRegularData = new XYData();
        noRegularData.addColumn(yData1);
        noRegularData.addColumn(yData1);


        chart = new Chart(new DarkTheme().getChartConfig());
       // chart.setXMinMax(0, 0, 500);
        chart.addTrace(new LineTrace(xyData3), false, true, false);
        chart.addStack();
        chart.addTrace(new LineTrace(xyData2), true);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        Thread t = new Thread(new Runnable() {
            int interval = 1000;
            @Override
            public void run() {
                for (int count = 0; count < 20; count++) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int yData1Last = yData1.get(yData1.size() - 1);
                    int yData2Last = yData2.get(yData2.size() - 1);
                    int yData3Last = yData3.get(yData2.size() - 1);
                    for (int i = 1; i <= 171; i++) {
                        yData1.add(i + yData1Last);
                        yData2.add(i + yData2Last);
                        yData3.add(i + yData3Last);
                    }
                    int xDataLast = xData.get(xData.size() - 1);
                    for (int i = 1; i <= 171; i++) {
                        xData.add(i + xDataLast);
                    }
                    chart.appendData();
                    chartPanel.repaint();
                }
            }
        });
       // t.start();

    }


    public static void main(String[] args) {
       ChartTest chartTest = new ChartTest();
    }
}
