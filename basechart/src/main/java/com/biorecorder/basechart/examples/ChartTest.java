package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
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
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        for (int i = 1; i <= 150; i++) {
            //yData1.add1((float) Math.sin(i));
            yData1.add(i);
            yData2.add(i + 20);
            yData3.add(i + 50);
        }

        for (int i = 1; i <= 150; i++) {
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

        XYData xyData1 = new XYData(1, 1);
        xyData1.addColumn(yData1);

        DataFrame df = new DataFrame();
        df.addColumn(xData);
        df.addColumn(yData1);
        df.addColumn(yData2);
        df.addColumn(yData3);
        df.addColumn(new SquareFunction(), 1);
        df.setColumnName(2, "eeg");
        df.setColumnAggFunctions(0, AggregateFunction.FIRST);
        df.setColumnAggFunctions(1, AggregateFunction.FIRST);
        df.setColumnAggFunctions(2, AggregateFunction.FIRST);
        df.setColumnAggFunctions(3, AggregateFunction.FIRST);

        int[] order = {0, 4, 4, 1};
        DataFrame df1 = new DataFrame(df, order);
        XYData xyData2 = new XYData(df1);


        XYData xyData3 = new XYData();
        xyData3.addColumn(xUnsort);
        xyData3.addColumn(yUnsort);

        Chart chart = new Chart(new WhiteTheme().getChartConfig());

       // chart.addTrace(new LineTrace(xyData1), false);
       // chart.addStack();
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
            int interval = 3000;
            @Override
            public void run() {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //chart.addStack();
                chart.addTrace(new LineTrace(xyData2), true, false, false);
                chartPanel.repaint();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                chart.removeTrace(chart.traceCount() - 1);
                chartPanel.repaint();

                String[] names = chart.getCurveNames();
                for (String name : names) {
                    System.out.println(name);
                }

            }
        });
       // t.start();

    }


    public static void main(String[] args) {
       ChartTest chartTest = new ChartTest();
    }
}
