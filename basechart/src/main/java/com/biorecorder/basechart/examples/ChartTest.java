package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.scales.CategoryScale;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.TimeScale;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.data.frame.Aggregation;
import com.biorecorder.data.frame.SquareFunction;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

        for (int i = 0; i < 150; i++) {
            list1.add(i);
            list2.add(i + 50);
            labels.add("lab_"+i);
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


        XYData regularData = new XYData(0, 1, true);
        //regularData.addColumn(new SquareFunction(), 0);
        regularData.addColumn(list1);
        //regularData.setColumnAggFunctions(1, Aggregation.SUM);
       // regularData.addColumn(list2);
        regularData.setColumnName(1, "rg");

        XYData noRegularData = new XYData(true);
        noRegularData.addColumn(list1);
        noRegularData.addColumn(list1);
        noRegularData.setColumnName(1, "nr");
    //    noRegularData.setColumnAggFunctions(1, Aggregation.SUM);


        XYData unsortedData = new XYData(false);
        unsortedData.addColumn(xUnsort);
        unsortedData.addColumn(yUnsort);


        chart = new Chart(new DarkTheme(true).getChartConfig());
        //chart.setXScale(0, new CategoryScale(labels));
        chart.setXScale(0, new TimeScale());
        //chart.addTrace(new LineTrace(unsortedData), false, true, false);
        //chart.addStack();
        chart.addTrace(new LineTrace(noRegularData), true);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            int interval = 5;
            @Override
            public void run() {
                for (int count = 0; count < 15; count++) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int yData1Last = list1.get(list1.size() - 1);
                    int yData2Last = list2.get(list2.size() - 1);

                    for (int i = 1; i < 171; i++) {
                        list1.add(list1.size());
                        //list1.add(i + yData1Last);
                       // list2.add(i + yData2Last);
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
