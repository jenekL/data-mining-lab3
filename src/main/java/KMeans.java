import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class KMeans {

    private int NUM_CLUSTERS = 6;
    private int NUM_POINTS = 200;
    private static final int MIN_COORDINATE = -5;
    private static final int MAX_COORDINATE = 5;
    private static final int SLEEP_TIME = 0;
    private ScatterPlotExample plot;

    private List<Point> points;
    private List<Cluster> clusters;

    public KMeans(ScatterPlotExample plot) {
        this.points = new ArrayList<>();
        this.clusters = new ArrayList<>();
        this.plot = plot;
    }

    public void init() throws InterruptedException {
        try {
            points = readFile("je.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);
            Point centroid;
            switch (i) {
                case 1:
                    centroid = new Point(0, -1);
                    break;
                case 2:
                    centroid = new Point(-2, 0);

                    break;
                case 3:
                    centroid = new Point(3, 2);
                    break;
                case 4:
                    centroid = new Point(2, 4);
                    break;
                case 5:
                    centroid = new Point(5, 2);
                    break;
                case 6:
                    centroid = new Point(3, 4);
                    break;
                default:
                    centroid = Point.createRandomPoint(MIN_COORDINATE, MAX_COORDINATE);
                    break;

            }

            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }
        plotClusters();
    }

    private void plotClusters() throws InterruptedException {
        plot.setDataSet(clusters);

        Thread.sleep(SLEEP_TIME);

        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    private List<Point> readFile(String path) throws IOException {
        FileReader fr = new FileReader(path);
        Scanner scan = new Scanner(fr);
        List<Point> pts = new ArrayList<>();
        int i = 1;

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            System.out.println(i + " : " + line);
            String[] split = line.split(",");
            i++;
            pts.add(new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
        }

        fr.close();
        return pts;
    }

    public void calculate() throws InterruptedException {
        boolean finish = false;
        int iteration = 0;

        while (!finish) {

            clearClusters();

            List lastCentroids = getCentroids();

            assignCluster();

            calculateCentroids();

            iteration++;

            List currentCentroids = getCentroids();

            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += Point.distance((Point) lastCentroids.get(i), (Point) currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();

            if (distance == 0) {
                finish = true;
            }
        }
        JOptionPane.showMessageDialog(null, "Finished with " + iteration + " iterations");


        List<Point> points1 = new ArrayList<>();
        points1.add(new Point(1, 0));
        points1.add(new Point(-1, 1));
        points1.add(new Point(-1, -1));

        testing(points1);
        points.addAll(points1);

        plot.setDataSet(clusters);
    }

    private void testing(List<Point> points){

        List<Point> testPoints = points;

        double max = Double.MAX_VALUE;
        double min;
        int cluster = 0;
        double distance = 0.0;

        for(Point point: testPoints) {
            min = max;
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            System.out.println("POINT [" + point.getX() + "," + point.getY() + "] in cluster " + (cluster + 1) );
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List getCentroids() {
        List<Point> centroids = new ArrayList<>(NUM_CLUSTERS);
        for (Cluster cluster : clusters) {
            Point aux = cluster.getCentroid();
            Point point = new Point(aux.getX(), aux.getY());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min;
        int cluster = 0;
        double distance = 0.0;

        for (Point point : points) {
            min = max;
            for (int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }

    private void calculateCentroids() {
        for (Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> list = cluster.getPoints();
            int n_points = list.size();

            for (Point point : list) {
                sumX += point.getX();
                sumY += point.getY();
            }

            Point centroid = cluster.getCentroid();
            if (n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }
}
