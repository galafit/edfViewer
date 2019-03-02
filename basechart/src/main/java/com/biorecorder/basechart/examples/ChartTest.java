package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.themes.WhiteTheme;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.swing.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by galafit on 21/9/18.
 */
public class ChartTest extends JFrame {
    IntArrayList yData1 = new IntArrayList();
    IntArrayList yData2 = new IntArrayList();
    IntArrayList xData2 = new IntArrayList();
    IntArrayList yData3 = new IntArrayList();
    IntArrayList xData3 = new IntArrayList();

    IntArrayList yData_col0 = new IntArrayList();
    IntArrayList yData_col1 = new IntArrayList();
    IntArrayList yData_col2 = new IntArrayList();
    ChartPanel chartPanel;

    public ChartTest()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        for (int i = 0; i <= 1600; i++) {
            yData1.add(i);
        }


        for (int i = 0; i < 160; i++) {
            //yData1.add1((float) Math.sin(i));
            yData_col0.add(i);
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
        xyData2.addColumn(yData_col0);
        xyData2.addColumn(yData_col1);
        xyData2.addColumn(yData_col2);
        xyData2.setColumnName(2, "eeg");

        XYData xyData3 = new XYData();
        xyData3.addColumn(xData3);
        xyData3.addColumn(yData3);

        Chart chart = new Chart(new WhiteTheme().getChartConfig());

        chart.addTrace(new LineTrace(xyData1), false, false, false);
        chart.addStack();
        chart.addTrace(new LineTrace(xyData2), false, false, false);
        chart.addStack();
        chart.addTrace(new LineTrace(xyData3), false, false, false);

        chart.setXMinMax(0, 0, 200);
        chart.setYMinMax(1, - 0.333, 0.76);

        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);


        final Timer timer = new Timer(6000, new ActionListener() {
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter == 0) {
                    CurveNumber curveNumber = chart.getSelectedCurveNumber();
                    chart.setCurveColor(curveNumber.getTraceNumber(), curveNumber.getCurveNumber(), BColor.MAGENTA);
                    chart.setCurveName(curveNumber.getTraceNumber(), curveNumber.getCurveNumber(), "bla bla bla");
                    chartPanel.repaint();
                }

                counter++;
            }
        });

        timer.start();

    }


    public static void main(String[] args) {
       ChartTest chartTest = new ChartTest();
    }
}
