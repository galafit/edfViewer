package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.Chart;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.data.XYData;
import com.biorecorder.util.lists.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.basechart.traces.LineTrace;

import javax.swing.*;
import java.awt.*;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {
    IntArrayList yData1;
    IntArrayList yData2;
    IntArrayList xData;
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        yData1 = new IntArrayList();
        yData2 = new IntArrayList();
        xData = new IntArrayList();

        for (int i = 0; i < 1600; i++) {
            //yData1.add((float) Math.sin(i));
            yData1.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            yData2.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            xData.add(i);
        }


        XYData xyData1 = new XYData();
        xyData1.setYData(yData1);

        XYData xyData2 = new XYData();
        xyData2.setYData(yData2);
        xyData2.setXData(xData);


        Chart chart = new Chart();

        chart.addStack();

        chart.addTrace(0, new LineTrace(), xyData1, false, false);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);

    }


    public static void main(String[] args) {
       // ChartTest chartTest = new ChartTest();
        int arr[] = {1, 2, 3, 4, 5};
        DataSeries dataSeries = new DataSeries();
        dataSeries.addYData(arr);
        for (int i = 0; i < dataSeries.size(); i++) {
            System.out.println(i);
        }
    }
}
