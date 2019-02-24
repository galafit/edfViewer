package com.biorecorder.basechart.examples;

import com.biorecorder.basechart.NavigableChart;
import com.biorecorder.basechart.XYData;
import com.biorecorder.basechart.swing.ChartPanel;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.basechart.LineTrace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by hdablin on 24.03.17.
 */
public class MainFrame extends JFrame {
    IntArrayList yData1;
    IntArrayList yData2;
    IntArrayList xData;
    ChartPanel chartPanel;

    public MainFrame()  {
        int width = 500;
        int height = 500;

        setTitle("Test chart");

        yData1 = new IntArrayList();
        yData2 = new IntArrayList();
        xData = new IntArrayList();

        for (int i = 0; i < 1600; i++) {
            //yData1.add1((float) Math.sin(i));
            yData1.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            yData2.add(i);
        }

        for (int i = 0; i < 1600; i++) {
            xData.add(i);
        }

        IntArrayList bandYData = new IntArrayList();
        int counter = 0;


     /*   for (int i = 0; i < 1600; i++) {
            if(counter < 100) {
               bandYData.add1(1);
            } else {
                bandYData.add1(0);
            }
            counter++;
            if(counter > 200) {
                counter = 0;
            }
        }*/


        XYData xyData1 = new XYData(1, 1);
        xyData1.addColumn(yData1);

        XYData xyData2 = new XYData();
        xyData2.addColumn(xData);
        xyData2.addColumn(yData2);


      /*  XYData xyData3 = new XYData();
        xyData3.setYData(yData2);
       // xyData3.setYGroupingType(GroupingType.MAX);

        XYData bandData = new XYData();
        bandData.setYData(bandYData);*/

        NavigableChart chart = new NavigableChart();

        chart.addChartStack();

        chart.addChartTrace(0, new LineTrace(xyData1), true, false, false);
        //config.addChartStack(5);
       // config.addTrace(new BooleanTraceConfig(), bandData, "Band");

       // chart.addChartStack(5);
       // config.addTrace(new LineTraceConfig(true), xyData1, "EEG1", "uVolts");
       // config.addTrace(new LineTraceConfig(false), xyData2);


       // chart.addChartStack();
       // chart.addChartTrace(1, new LineTrace(new LineTraceConfig(true)), xyData1, false, false);

        //  config.addTrace(new LineTraceConfig(true), xyData2, "EEG2", "uVolts");
     //   config.addTrace(new LineTraceConfig(false), xyData1);

    /*    config.addChartStack(5, new Range(-500, 100));
        config.addTrace(new LineTraceConfig(false), xyData1);
        config.addTrace(new LineTraceConfig(true), xyData2, "EEG3", "uVolts");

        config.addChartStack(5, new Range(-500.0, null));
        config.addTrace(new LineTraceConfig(false), xyData1);
        config.addTrace(new LineTraceConfig(true), xyData2, "EEG4", "uVolts");*/

        // config.updatePreviewMinMax(new Range(0, 1000));
        // config.addScroll(0, 100);
      //  config.addNavigatorTrace(new LineTraceConfig(), xyData3, "PREV", "kg");
       // config.addNavigatorTrace(new LineTraceConfig(), xyData2);
       // config.addNavigatorTrace(new LineTraceConfig(), xyData1);

        //config.addPreviewGroupingInterval(1000);


        chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(width, height));
        add(chartPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(chartPanel);
        setLocationRelativeTo(null);
        setVisible(true);
      /*  try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chart.setTheme(new WhiteTheme());
        chartPanel.repaint();*/
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
        MainFrame mainFrame = new MainFrame();

        final Timer timer = new Timer(10, new ActionListener() {
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter < 20) {
                    mainFrame.update();
                    counter++;
                }
            }
        });
        timer.setInitialDelay(0);
      //  timer.start();

    }
}
