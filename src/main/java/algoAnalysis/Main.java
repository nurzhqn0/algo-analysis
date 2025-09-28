package algoAnalysis;

import algoAnalysis.algorithms.ClosestPair;
import algoAnalysis.algorithms.DeterministicSelect;
import algoAnalysis.algorithms.MergeSort;
import algoAnalysis.algorithms.QuickSort;
import algoAnalysis.external.Point;
import algoAnalysis.metrics.AlgorithmMetrics;
import algoAnalysis.metrics.CSVWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Main class for testing all algorithms
 * @author nurzhqn0
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Algorithm Performance Analysis ===\n");

        testSortingAlgorithms();

        System.out.println("\n" + "=".repeat(50) + "\n");

        testSelectionAlgorithm();

        System.out.println("\n" + "=".repeat(50) + "\n");

        testClosestPairAlgorithm();
    }

    private static void testSortingAlgorithms() {
        System.out.println("SORTING ALGORITHMS COMPARISON");
        System.out.println("-".repeat(30));

        ArrayList<Integer> mergeTesting = new ArrayList<>(Arrays.asList(64, 34, 25, 12, 22, 11, 90, 22));
        ArrayList<Integer> quickTesting = new ArrayList<>(Arrays.asList(64, 34, 25, 12, 22, 11, 90, 22)); // Same data for fair comparison

        AlgorithmMetrics mergeMetrics = new AlgorithmMetrics();
        AlgorithmMetrics quickMetrics = new AlgorithmMetrics();

        System.out.println("Original array: " + mergeTesting);

        MergeSort.sort(mergeTesting, mergeMetrics);
        QuickSort.sort(quickTesting, quickMetrics);

        System.out.println("\nMergeSort Results:");
        System.out.println("Sorted array:   " + mergeTesting);
        System.out.println("Comparisons:    " + mergeMetrics.getComparisons());
        System.out.println("Swaps:          " + mergeMetrics.getSwaps());
        System.out.println("Assignments:    " + mergeMetrics.getAssignments());
        System.out.println("Max Depth:      " + mergeMetrics.getMaxDepth());
        System.out.println("Time:           " + String.format("%.3f ms", mergeMetrics.getExecutionTimeMillis()));

        System.out.println("\nQuickSort Results:");
        System.out.println("Sorted array:   " + quickTesting);
        System.out.println("Comparisons:    " + quickMetrics.getComparisons());
        System.out.println("Swaps:          " + quickMetrics.getSwaps());
        System.out.println("Assignments:    " + quickMetrics.getAssignments());
        System.out.println("Max Depth:      " + quickMetrics.getMaxDepth());
        System.out.println("Time:           " + String.format("%.3f ms", quickMetrics.getExecutionTimeMillis()));

        try {
            CSVWriter.writeMetricsToCSV("sorting_results.csv", "MergeSort", mergeTesting.size(), mergeMetrics);
            CSVWriter.writeMetricsToCSV("sorting_results.csv", "QuickSort", quickTesting.size(), quickMetrics);
        } catch (IOException e) {
            System.err.println("Error writing sorting results to CSV: " + e.getMessage());
        }
    }

    private static void testSelectionAlgorithm() {
        System.out.println("DETERMINISTIC SELECT ALGORITHM");
        System.out.println("-".repeat(31));

        ArrayList<Integer> selectArray = new ArrayList<>(Arrays.asList(3, 6, 2, 8, 1, 9, 4, 7, 5, 10));
        AlgorithmMetrics selectMetrics = new AlgorithmMetrics();

        System.out.println("Original array: " + selectArray);

        int[] kValues = {1, 3, 5, 8, 10}; // 1st, 3rd, 5th, 8th, 10th smallest

        System.out.println("\nFinding order statistics:");

        for (int k : kValues) {
            ArrayList<Integer> testArray = new ArrayList<>(selectArray);
            selectMetrics.reset();

            int result = DeterministicSelect.select(testArray, k, selectMetrics);

            System.out.printf("k=%2d: %d-th smallest = %2d (Comparisons: %3d, Time: %.3f ms)\n",
                    k, k, result, selectMetrics.getComparisons(), selectMetrics.getExecutionTimeMillis());

            // Write to CSV
            try {
                CSVWriter.writeMetricsToCSV("selection_results.csv", "DeterministicSelect",
                        selectArray.size(), selectMetrics);
            } catch (IOException e) {
                System.err.println("Error writing selection results to CSV: " + e.getMessage());
            }
        }

        // Test with larger array
        System.out.println("\nTesting with larger array (100 elements):");
        ArrayList<Integer> largeArray = generateRandomArray(100, 42);
        selectMetrics.reset();

        int median = DeterministicSelect.select(new ArrayList<>(largeArray), 50, selectMetrics);
        System.out.printf("Median (50th element): %d\n", median);
        System.out.printf("Performance: %d comparisons, %.3f ms, max depth: %d\n",
                selectMetrics.getComparisons(), selectMetrics.getExecutionTimeMillis(), selectMetrics.getMaxDepth());
    }

    private static void testClosestPairAlgorithm() {
        System.out.println("CLOSEST PAIR OF POINTS ALGORITHM");
        System.out.println("-".repeat(33));

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(2, 3));
        points.add(new Point(12, 30));
        points.add(new Point(40, 50));
        points.add(new Point(5, 1));
        points.add(new Point(12, 10));
        points.add(new Point(3, 4));

        AlgorithmMetrics closestPairMetrics = new AlgorithmMetrics();

        System.out.println("Test points:");
        for (int i = 0; i < points.size(); i++) {
            System.out.printf("Point %d: %s\n", i + 1, points.get(i));
        }

        double minDistance = ClosestPair.findClosestPair(points, closestPairMetrics);

        System.out.println("\nClosest Pair Results:");
        System.out.printf("Minimum distance: %.6f\n", minDistance);
        System.out.println("Comparisons:      " + closestPairMetrics.getComparisons());
        System.out.println("Assignments:      " + closestPairMetrics.getAssignments());
        System.out.println("Max Depth:        " + closestPairMetrics.getMaxDepth());
        System.out.printf("Time:             %.3f ms\n", closestPairMetrics.getExecutionTimeMillis());

        try {
            CSVWriter.writeMetricsToCSV("closest_pair_results.csv", "ClosestPair",
                    points.size(), closestPairMetrics);
        } catch (IOException e) {
            System.err.println("Error writing closest pair results to CSV: " + e.getMessage());
        }

        System.out.println("\nTesting with larger point set (500 random points):");
        ArrayList<Point> randomPoints = generateRandomPoints(500, 123);
        closestPairMetrics.reset();

        double largeMinDistance = ClosestPair.findClosestPair(randomPoints, closestPairMetrics);
        System.out.printf("Minimum distance: %.6f\n", largeMinDistance);
        System.out.printf("Performance: %d comparisons, %.3f ms, max depth: %d\n",
                closestPairMetrics.getComparisons(), closestPairMetrics.getExecutionTimeMillis(),
                closestPairMetrics.getMaxDepth());

        try {
            CSVWriter.writeMetricsToCSV("closest_pair_results.csv", "ClosestPair",
                    randomPoints.size(), closestPairMetrics);
        } catch (IOException e) {
            System.err.println("Error writing large closest pair results to CSV: " + e.getMessage());
        }
    }

    private static ArrayList<Integer> generateRandomArray(int size, int seed) {
        ArrayList<Integer> array = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(1000) + 1);
        }
        return array;
    }

    private static ArrayList<Point> generateRandomPoints(int count, int seed) {
        ArrayList<Point> points = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 1000;
            points.add(new Point(x, y));
        }
        return points;
    }
}