package algoAnalysis.algorithms;

import algoAnalysis.external.Point;
import algoAnalysis.metrics.AlgorithmMetrics;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Closest Pair of Points
 * @author nurzhqn0
 * @version 1.0
 */
public class ClosestPair {
    /**
     * Finds the minimum distance between any two points using divide-and-conquer
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    public static double findClosestPair(ArrayList<Point> points, AlgorithmMetrics metrics) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }

        metrics.startTiming();

        Point[] pointsByX = points.toArray(new Point[0]);
        Point[] pointsByY = points.toArray(new Point[0]);

        Arrays.sort(pointsByX, (p1, p2) -> Double.compare(p1.x, p2.x));
        Arrays.sort(pointsByY, (p1, p2) -> Double.compare(p1.y, p2.y));

        double result = closestPairRec(pointsByX, pointsByY, 0, pointsByX.length - 1, metrics);

        metrics.stopTiming();
        return result;
    }

    private static double closestPairRec(Point[] pointsByX, Point[] pointsByY,
                                         int left, int right, AlgorithmMetrics metrics) {
        metrics.enterRecursion();

        int n = right - left + 1;

        if (n <= 3) {
            double result = bruteForce(pointsByX, left, right, metrics);
            metrics.exitRecursion();
            return result;
        }

        int mid = left + (right - left) / 2;
        Point midPoint = pointsByX[mid];

        // Create left and right y-sorted arrays for recursive calls
        ArrayList<Point> leftList = new ArrayList<>();
        ArrayList<Point> rightList = new ArrayList<>();

        for (Point point : pointsByY) {
            metrics.incrementComparisons();

            if (point.x <= midPoint.x) {
                leftList.add(point);
                metrics.incrementAssignments();
            } else {
                rightList.add(point);
                metrics.incrementAssignments();
            }
        }

        Point[] leftY = leftList.toArray(new Point[0]);
        Point[] rightY = rightList.toArray(new Point[0]);

        // Conquer: recursively solve left and right subproblems
        double leftMin = closestPairRec(pointsByX, leftY, left, mid, metrics);
        double rightMin = closestPairRec(pointsByX, rightY, mid + 1, right, metrics);

        // Find minimum of the two halves
        double minDist = Math.min(leftMin, rightMin);
        metrics.incrementComparisons();

        // Combine: check strip around dividing line for closer pairs
        double stripMin = findClosestInStrip(pointsByY, midPoint.x, minDist, metrics);

        double finalResult = Math.min(minDist, stripMin);
        metrics.incrementComparisons();

        metrics.exitRecursion();
        return finalResult;
    }

    /**
     * Finds the closest pair in the strip around the dividing line
     * Time Complexity: O(n) - at most 7n comparisons
     */
    private static double findClosestInStrip(Point[] pointsByY, double midX,
                                             double minDist, AlgorithmMetrics metrics) {
        // Build strip containing points within minDist of dividing line
        ArrayList<Point> strip = new ArrayList<>();

        for (Point point : pointsByY) {
            metrics.incrementComparisons();
            if (Math.abs(point.x - midX) < minDist) {
                strip.add(point);
                metrics.incrementAssignments();
            }
        }

        double stripMin = minDist;

        for (int i = 0; i < strip.size(); i++) {
            Point p1 = strip.get(i);

            // 7-neighbor optimization: geometric proof shows at most 7 points
            // in the strip can be closer than current minimum
            for (int j = i + 1; j < strip.size() && j <= i + 7; j++) {
                Point p2 = strip.get(j);

                metrics.incrementComparisons();

                if (p2.y - p1.y >= stripMin) {
                    break;
                }

                double distance = p1.distanceTo(p2);
                metrics.incrementComparisons();

                if (distance < stripMin) {
                    stripMin = distance;
                    metrics.incrementAssignments();
                }
            }
        }

        return stripMin;
    }

    /**
     * Brute force for base cases with small number of points
     * Time Complexity: O(n²) where n = right - left + 1
     */
    private static double bruteForce(Point[] points, int left, int right,
                                     AlgorithmMetrics metrics) {
        double minDistance = Double.MAX_VALUE;

        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                double distance = points[i].distanceTo(points[j]);
                metrics.incrementComparisons();

                if (distance < minDistance) {
                    minDistance = distance;
                    metrics.incrementAssignments();
                }
            }
        }

        return minDistance;
    }
}