/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.reporter;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class BenchmarkChart {
    private JFreeChart chart;
    private String bname;
    private long startTime;
    private XYSeries totalHeap;
    private XYSeries freeHeap;
    private Hashtable<String, XYSeries> addedSeries = new Hashtable<String, XYSeries>();
    private XYSeriesCollection mainDataset = new XYSeriesCollection();
    private XYPlot plot;
    private HashMap<String, XYSeries> analyzersMap = new HashMap<String, XYSeries>();
    int axisIndex;
    
    private static Color[] colors = new Color[] {Color.BLUE, Color.RED, Color.BLACK };
    
    public BenchmarkChart(String bname) {
        this.bname = bname;
        chart = ChartFactory.createXYLineChart(bname,
                "time from start (millisec)",
                "loop result (millisec)",
                mainDataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        
        plot = chart.getXYPlot();
    }
    
    
    
    public void setStartTime(long startTime) {
        if (this.startTime == 0) {
            this.startTime = startTime;
        }
    }
    
    public void addTotalHeapInfo(long time, long bytes) {
        if (totalHeap == null) {
            totalHeap = new XYSeries("total heap");
        }
        totalHeap.add(time - startTime, bytes / (1024 * 1024));
    }
    
    
    public void addFreeHeapInfo(long time, long bytes) {
        if (freeHeap == null) {
            freeHeap = new XYSeries("free heap");
        }
        freeHeap.add(time - startTime, bytes / (1024 * 1024));
    }
    
    public void addAnalyzerReport(String name, String unit, long time, long value) {
        if (!analyzersMap.containsKey(name)) {
            XYSeries series = new XYSeries(name + " (" + unit + ")");
            analyzersMap.put(name, series);
            addAxis(name, unit, series);
        }
        
        analyzersMap.get(name).add(time - startTime, value);
    }
    
    public void addAxis(String name, String unit, XYSeries series) {
        NumberAxis axis = new NumberAxis(unit);
        axisIndex ++;
        plot.setRangeAxis(axisIndex, axis);
        plot.setRangeAxisLocation(axisIndex, AxisLocation.BOTTOM_OR_RIGHT);
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        plot.setDataset(axisIndex, collection);
        plot.mapDatasetToRangeAxis(axisIndex, axisIndex);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(axisIndex, renderer);
        plot.getRenderer(axisIndex).setPaint(colors[axisIndex % colors.length]);
    }
    
    
    public void addTimeInfo(String threadName, long startTime, long endTime) {
        long x = endTime - this.startTime;
        long y = endTime - startTime;
        if (addedSeries.containsKey(threadName)) {
            ((XYSeries)addedSeries.get(threadName)).add(x, y);
        } else {
            XYSeries series = new XYSeries(threadName);
            series.add(x, y);
            addedSeries.put(threadName, series);
            mainDataset.addSeries(series);
        }
    }
    
    public void addTimeInfo(long startTime, long endTime) {
        long x = endTime - this.startTime;
        long y = endTime - startTime;
        String thr = "x";
        if (addedSeries.containsKey(thr)) {
            ((XYSeries)addedSeries.get(thr)).add(x, y);
        } else {
            XYSeries series = new XYSeries(thr);
            series.add(x, y);
            addedSeries.put(thr, series);
            mainDataset.addSeries(series);
        }
    }    
    
    public void addMarker(long time, String label) {
        Marker currentEnd = new ValueMarker(time - startTime);
        currentEnd.setPaint(Color.BLACK);
        currentEnd.setLabel(label);
        currentEnd.setLabelFont(currentEnd.getLabelFont().deriveFont(Font.BOLD, 16.0f));
        currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        currentEnd.setLabelTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        plot.addDomainMarker(currentEnd);
    }
    
    private void setMemoryAxis() {
        if (totalHeap != null || freeHeap != null) {
            axisIndex++;
            plot.setRangeAxis(axisIndex, new NumberAxis("heap, MB"));
            plot.setRangeAxisLocation(axisIndex, AxisLocation.BOTTOM_OR_RIGHT);
            XYSeriesCollection memusage = new XYSeriesCollection();
            memusage.addSeries(freeHeap);
            memusage.addSeries(totalHeap);
            plot.setDataset(axisIndex, memusage);
            plot.mapDatasetToRangeAxis(axisIndex, axisIndex);
            XYAreaRenderer renderer = new XYAreaRenderer();
            plot.setRenderer(axisIndex, renderer);
            renderer.setSeriesPaint(0, new Color(247, 174, 185));
            renderer.setSeriesPaint(1, new Color(155, 181, 255));
            renderer.setOutline(true);
            renderer.setOutlinePaint(Color.GRAY);
            plot.setForegroundAlpha(0.8f);
        }
    }
    
    
    public void buildJPEG() {
        setMemoryAxis();        
        try {
            updateColors();
            ChartUtilities.saveChartAsJPEG(new File(Utils.getFullImageName(bname)),
                    chart, 900, 600);
        } catch (Exception e) {
            System.out.println("Problems...");
        }
    }
    
    private void updateColors() {
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)chart.getXYPlot().getRenderer();
        renderer.setShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setSeriesItemLabelsVisible(0, true);
        chart.getXYPlot().setOutlinePaint(Color.BLACK);
        renderer.setOutlinePaint(Color.BLACK);
        chart.getLegend(0).setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(true);
        chart.setBorderPaint(Color.BLUE);
        chart.setBackgroundPaint(Color.WHITE);
        renderer.setUseFillPaint(true);
        renderer.setFillPaint(new Color(204, 251,237));
        for (int i = 0; i < addedSeries.size(); i ++) {
            Color paint = (Color)renderer.getSeriesPaint(i);            
            renderer.setSeriesPaint(i, paint.darker().darker());
        }
    }
}
