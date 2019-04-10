package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.TimeScale;
import com.biorecorder.basechart.themes.DarkTheme;
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

    IntArrayList list1 = new IntArrayList();
    IntArrayList list2 = new IntArrayList();

    Chart chart;
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        for (int i = 0; i < 150; i++) {
            list1.add(i);
            list2.add(i + 50);
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


        XYData regularData = new XYData(0, 1);
        regularData.addColumn(list1);
       // regularData.addColumn(list2);

        XYData noRegularData = new XYData();
        noRegularData.addColumn(list1);
        noRegularData.addColumn(list1);


        XYData unsortedData = new XYData();
        unsortedData.addColumn(xUnsort);
        unsortedData.addColumn(yUnsort);


        chart = new Chart(new DarkTheme(true).getChartConfig(), new TimeScale(), new LinearScale());
        chart.setXScale(0, new LinearScale());
        chart.addTrace(new LineTrace(unsortedData), false, true, false);
        chart.addStack();
        chart.addTrace(new LineTrace(regularData), true);
        chart.addTrace(new LineTrace(noRegularData), true);

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
                for (int count = 0; count < 10; count++) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int yData1Last = list1.get(list1.size() - 1);
                    int yData2Last = list2.get(list2.size() - 1);

                    for (int i = 1; i <= 171; i++) {
                        list1.add(10);
                        //list1.add(i + yData1Last);
                        list2.add(i + yData2Last);
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
