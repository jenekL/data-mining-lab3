import java.awt.*;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlotExample extends JFrame {

    private XYPlot plot;
    public ScatterPlotExample(String title, XYDataset dataset) {
        super(title);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "KMeans",
                "X-Axis", "Y-Axis", dataset, PlotOrientation.VERTICAL,
                true, true, true);


        plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);

        Thread t = new Thread(()->{
            KMeans kmeans = new KMeans(this);
            try {
                kmeans.init();
                kmeans.calculate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public void setPanel(Container contentPane){
        setContentPane(contentPane);
    }

    public void setDataSet(List<Cluster> clusterList){
        plot.setDataset(fromClusterToDataset(clusterList));
    }

    public XYDataset fromClusterToDataset(List<Cluster> clusterList){
        XYSeriesCollection dataset = new XYSeriesCollection();
        for(Cluster cluster : clusterList){
            XYSeries series = new XYSeries(cluster.getId());
            for(Point p : cluster.points) {
                series.add(p.getX(), p.getY());
            }
            XYSeries center = new XYSeries("center " + cluster.getId());
            Point centroid = cluster.getCentroid();
            center.add(centroid.getX(), centroid.getY());
            dataset.addSeries(series);
            dataset.addSeries(center);
        }
        return dataset;
    }

    public static XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScatterPlotExample example = new ScatterPlotExample("iad3lab", ScatterPlotExample.createDataset());
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}