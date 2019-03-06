package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.NavigableChart;
import com.biorecorder.basechart.XYData;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.basechart.themes.WhiteTheme;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.basechart.LineTrace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by galafit on 27/9/18.
 */
public class NavigableChartTest extends JFrame{
    IntArrayList yData1;
    IntArrayList yData2;
    IntArrayList xData;
    ChartPanel chartPanel;

    public NavigableChartTest() {
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

        NavigableChart chart = new NavigableChart(new WhiteTheme().getNavigableChartConfig());
        chart.addChartTrace(new LineTrace(xyData1), false );
       // chart.addChartTrace(new LineTrace(xyData2), false , false, false);
       // chart.addChartStack();
       // chart.addChartTrace(new LineTrace(xyData1), false );


        chart.addNavigatorTrace( new LineTrace(xyData1), false);
        //chart.addNavigatorStack();
       // chart.addNavigatorTrace(new LineTrace(xyData2), false);

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
        NavigableChartTest chartTest = new NavigableChartTest();

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
