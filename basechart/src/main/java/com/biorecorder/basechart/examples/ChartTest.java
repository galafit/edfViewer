package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.Chart;
import com.biorecorder.basechart.XYData;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.basechart.traces.LineTrace;

import javax.swing.*;
import java.awt.*;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {
    IntArrayList yData1 = new IntArrayList();
    IntArrayList yData2 = new IntArrayList();
    IntArrayList xData2 = new IntArrayList();
    IntArrayList yData3 = new IntArrayList();
    IntArrayList xData3 = new IntArrayList();

    IntArrayList yData_col1 = new IntArrayList();
    IntArrayList yData_col2 = new IntArrayList();
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        for (int i = 0; i < 160; i++) {
            //yData1.add1((float) Math.sin(i));
            yData1.add(i);
            yData_col1.add(i + 20);
            yData_col2.add(i + 50);
        }

        for (int i = 0; i < 160; i++) {
            yData2.add(i);
        }
        for (int i = 0; i < 160; i++) {
            xData2.add(i);
        }


        xData3.add(50);
        xData3.add(300);
        xData3.add(200);
        xData3.add(100);
        xData3.add(150);
        xData3.add(20);

        yData3.add(100);
        yData3.add(200);
        yData3.add(150);
        yData3.add(10);
        yData3.add(300);
        yData3.add(300);

        XYData xyData1 = new XYData(0, 1);
        xyData1.addColumn(yData1);

        XYData xyData2 = new XYData();
        xyData2.addColumn(xData2);
        xyData2.addColumn(yData2);
        xyData2.addColumn(yData_col1);
        xyData2.addColumn(yData_col2);
        xyData2.setColumnName(2, "eeg");

        XYData xyData3 = new XYData();
        xyData3.addColumn(xData3);
        xyData3.addColumn(yData3);

        Chart chart = new Chart();

        //chart.addTrace(new LineTrace(xyData1), true, false, false);
        chart.addTrace(new LineTrace(xyData2), false, false, false);
        chart.addStack();
        chart.addTrace(new LineTrace(xyData3), false, false, false);

        chart.setXMinMax(0, -100, 200);
        chart.setYMinMax(0, -100, 300);

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
       ChartTest chartTest = new ChartTest();
    }
}
