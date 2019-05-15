package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.scales.CategoryScale;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.TimeScale;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.data.frame.Aggregation;
import com.biorecorder.data.frame.SquareFunction;
import com.biorecorder.data.list.DoubleArrayList;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.data.list.LongArrayList;
import com.biorecorder.data.utils.PrimitiveUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {
    IntArrayList yUnsort = new IntArrayList();
    IntArrayList xUnsort = new IntArrayList();

    DoubleArrayList list1 = new DoubleArrayList();
    IntArrayList list2 = new IntArrayList();

    List<String> labels = new ArrayList();

    Chart chart;
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        double value = 0;
       /* for (int i = 0; i <= 250; i++) {
            list1.add(value);
            list2.add(50);
            labels.add("lab_"+i);
            value += 0.33;
        }*/


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
        regularData.addColumn(list1);
        //regularData.addColumn(new SquareFunction(), 0);
        regularData.setColumnName(1, "rg");

       /* XYData noRegularData = new XYData(true);
        noRegularData.addColumn(list1);
        noRegularData.addColumn(list1);
        noRegularData.setColumnName(1, "nr");


        XYData unsortedData = new XYData(false);
        unsortedData.addColumn(xUnsort);
        unsortedData.addColumn(yUnsort);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        LongArrayList timeArray = new LongArrayList();
        for (int i = 0; i < 150; i++) {
            timeArray.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        XYData timeData = new XYData(false);
        timeData.addColumn(timeArray);
        timeData.addColumn(list1);*/

        chart = new Chart(new CategoryScale());
        chart.addTrace(new LineTrace(regularData), true);

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
            /*chart.addTrace(new LineTrace(unsortedData), false, true, false);
            chart.addTrace(new LineTrace(timeData), false, true, false);
            chart.addStack();
            chart.addTrace(new LineTrace(noRegularData), true);
            chart.addTrace(new LineTrace(regularData), true);
            chartPanel.repaint();*/

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

                    double value = 0;
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
        t.start();
    }

    public static void main(String[] args) {
       ChartTest chartTest = new ChartTest();
    }
}
