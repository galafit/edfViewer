package com.biorecorder.basechart;

import com.biorecorder.basechart.config.ChartConfig;
import com.biorecorder.basechart.config.traces.LineTraceConfig;
import com.biorecorder.basechart.data.FloatArrayList;
import com.biorecorder.basechart.data.IntArrayList;
import com.biorecorder.basechart.data.XYData;

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

        for (int i = 0; i < 16000; i++) {
            //yData1.add((float) Math.sin(i));
            yData1.add(i);
        }

        for (int i = 0; i < 16000; i++) {
            yData2.add(i + 1000);
        }
        for (int i = 0; i < 16000; i++) {
            xData.add(i);
        }

        IntArrayList bandYData = new IntArrayList();
        int counter = 0;


     /*   for (int i = 0; i < 1600; i++) {
            if(counter < 100) {
               bandYData.add(1);
            } else {
                bandYData.add(0);
            }
            counter++;
            if(counter > 200) {
                counter = 0;
            }
        }*/


        XYData xyData1 = new XYData();
        xyData1.setYData(yData1);

        XYData xyData2 = new XYData();
        xyData2.setYData(yData2);
        xyData2.setXData(xData);

        XYData xyData3 = new XYData();
        xyData3.setYData(yData2);
       // xyData3.setYGroupingType(GroupingType.MAX);

        XYData bandData = new XYData();
        bandData.setYData(bandYData);

        ChartConfig config = new ChartConfig(false, true);

        config.addTrace(new LineTraceConfig(false), xyData1);
        //config.addChartStack(5);
       // config.addTrace(new BooleanTraceConfig(), bandData, "Band");

        config.addChartStack(5);
       // config.addTrace(new LineTraceConfig(true), xyData1, "EEG1", "uVolts");
        config.addTrace(new LineTraceConfig(false), xyData2);


        config.addChartStack(5);
        config.addTrace(new LineTraceConfig(true), xyData2, "EEG2", "uVolts");
     //   config.addTrace(new LineTraceConfig(false), xyData1);

    /*    config.addChartStack(5, new Range(-500, 100));
        config.addTrace(new LineTraceConfig(false), xyData1);
        config.addTrace(new LineTraceConfig(true), xyData2, "EEG3", "uVolts");

        config.addChartStack(5, new Range(-500.0, null));
        config.addTrace(new LineTraceConfig(false), xyData1);
        config.addTrace(new LineTraceConfig(true), xyData2, "EEG4", "uVolts");*/

        // config.updatePreviewMinMax(new Range(0, 1000));
        // config.addScroll(0, 100);
      //  config.addPreviewTrace(new LineTraceConfig(), xyData3, "PREV", "kg");
       // config.addPreviewTrace(new LineTraceConfig(), xyData2);
       // config.addPreviewTrace(new LineTraceConfig(), xyData1);

        //config.addPreviewGroupingInterval(1000);


        chartPanel = new ChartPanel(config);

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
