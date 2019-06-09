package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.data.XYData;
import com.biorecorder.basechart.traces.LineTrace;
import com.biorecorder.data.frame.SquareFunction;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.data.list.LongArrayList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {
    IntArrayList yUnsort = new IntArrayList();
    IntArrayList xUnsort = new IntArrayList();

    IntArrayList list1 = new IntArrayList();
    IntArrayList list2 = new IntArrayList();

    List<String> labels = new ArrayList();

    Chart chart;
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        int value = 0;
        for (int i = 0; i <= 5; i++) {
            list1.add(value);
            list2.add(50);
            labels.add("lab_"+i);
            value += 1;
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


        XYData regularData = new XYData(labels, true);
        regularData.addColumn("reg", list1);


        XYData noRegularData = new XYData(true);
        noRegularData.addColumn("x", list1);
        noRegularData.addColumn("non-reg", list1);
        noRegularData.addColumn("function", new SquareFunction(), 0);


        XYData unsortedData = new XYData(false);
        unsortedData.addColumn("x", xUnsort);
        unsortedData.addColumn("unsort", yUnsort);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        LongArrayList timeArray = new LongArrayList();
        for (int i = 0; i < 150; i++) {
            timeArray.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        XYData timeData = new XYData(false);
        timeData.addColumn("time", timeArray);
        timeData.addColumn("y", list1);

        chart = new Chart();
        //chart.addTraces(new LineTrace(regularData), true);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        try {
            Thread.sleep(1000);
            chart.addTraces(unsortedData, new LineTrace(), true, XAxisPosition.TOP, YAxisPosition.RIGHT);
            chart.addStack();
            chart.addTraces(noRegularData, new LineTrace(), true);
            chart.addTraces(regularData, new LineTrace(), false, XAxisPosition.BOTTOM, YAxisPosition.RIGHT);

            chartPanel.repaint();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            int interval = 100;
            @Override
            public void run() {
                for (int count = 0; count < 10; count++) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int value = 0;
                    if(list1.size() > 0) {
                        value = list1.get(list1.size() - 1);
                    }
                    for (int i = 1; i < 2; i++) {
                        value += 1;
                        list1.add(value);
                        labels.add("lab_"+value);
                    }

                    chart.appendData();
                    chartPanel.repaint();
                }
                System.out.println(list1.size());
            }
        });
       // t.start();
    }

    public static void main(String[] args) {
       ChartTest chartTest = new ChartTest();
    }
}
