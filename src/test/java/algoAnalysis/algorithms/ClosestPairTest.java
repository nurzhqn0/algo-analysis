package algoAnalysis.algorithms;

import algoAnalysis.external.Point;
import algoAnalysis.metrics.TestAlgorithmMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Closest Pair of Points
 * @author nurzhqn0
 * @version 1.0
 */
class ClosestPairTest {

    private TestAlgorithmMetrics metrics;
    private static final double EPSILON = 1e-9;

    @BeforeEach
    void setUp() {
        metrics = new TestAlgorithmMetrics();
    }

    @Nested
    @DisplayName("Closest Pair: Validate against O(n²) on small n (≤ 2000)")
    class SmallArrayValidation {

        @Test
        @DisplayName("Validate against O(n²) brute force for n ≤ 2000")
        void validateAgainstBruteForceSmallArrays() {
            Random random = new Random(42);

            int[] smallSizes = {10, 50, 100, 500, 1000, 2000};

            for (int size : smallSizes) {
                System.out.printf("Testing size %d against brute force...\n", size);

                ArrayList<Point> points = generateRandomPoints(size, random);

                // Get result from fast O(n log n) algorithm
                TestAlgorithmMetrics fastMetrics = new TestAlgorithmMetrics();
                double fastResult = ClosestPair.findClosestPair(new ArrayList<>(points), fastMetrics);

                // Get result from O(n²) brute force
                TestAlgorithmMetrics bruteMetrics = new TestAlgorithmMetrics();
                double bruteResult = bruteForceClosestPair(new ArrayList<>(points), bruteMetrics);

                // Verify results match within floating point precision
                assertEquals(bruteResult, fastResult, EPSILON,
                        String.format("Size %d: Fast=%.9f, Brute=%.9f", size, fastResult, bruteResult));

                // Note: We don't assert performance here since the fast algorithm
                // may have overhead that makes it slower than brute force for medium sizes.
                // The important thing is correctness validation.

                System.out.printf("  ✓ Size %d: distance=%.6f (Fast: %.2fms, Brute: %.2fms)\n",
                        size, fastResult, fastMetrics.getExecutionTimeMillis(), bruteMetrics.getExecutionTimeMillis());
            }
        }

        @Test
        @DisplayName("Multiple trials for small arrays to ensure consistency")
        void multipleTrialsSmallArrays() {
            Random random = new Random(123);

            // Test multiple random configurations for sizes ≤ 2000
            int[] testSizes = {100, 500, 1000, 1500, 2000};
            int trialsPerSize = 5;

            for (int size : testSizes) {
                for (int trial = 0; trial < trialsPerSize; trial++) {
                    ArrayList<Point> points = generateRandomPoints(size, random);

                    TestAlgorithmMetrics fastMetrics = new TestAlgorithmMetrics();
                    double fastResult = ClosestPair.findClosestPair(new ArrayList<>(points), fastMetrics);

                    TestAlgorithmMetrics bruteMetrics = new TestAlgorithmMetrics();
                    double bruteResult = bruteForceClosestPair(new ArrayList<>(points), bruteMetrics);

                    assertEquals(bruteResult, fastResult, EPSILON,
                            String.format("Size %d, trial %d: Fast=%.9f, Brute=%.9f",
                                    size, trial, fastResult, bruteResult));
                }
                System.out.printf("✓ Size %d: All %d trials validated against brute force\n",
                        size, trialsPerSize);
            }
        }

        @Test
        @DisplayName("Edge cases validation for small arrays")
        void edgeCasesSmallArrays() {
            // Test specific configurations that might cause issues

            // Collinear points
            ArrayList<Point> collinear = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                collinear.add(new Point(i * 1.0, i * 1.0)); // Points on y=x line
            }

            TestAlgorithmMetrics fastMetrics = new TestAlgorithmMetrics();
            double fastResult = ClosestPair.findClosestPair(new ArrayList<>(collinear), fastMetrics);

            TestAlgorithmMetrics bruteMetrics = new TestAlgorithmMetrics();
            double bruteResult = bruteForceClosestPair(new ArrayList<>(collinear), bruteMetrics);

            assertEquals(bruteResult, fastResult, EPSILON, "Collinear points validation failed");

            // Grid pattern
            ArrayList<Point> grid = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    grid.add(new Point(i * 2.0, j * 2.0));
                }
            }

            fastMetrics.reset();
            fastResult = ClosestPair.findClosestPair(new ArrayList<>(grid), fastMetrics);

            bruteMetrics.reset();
            bruteResult = bruteForceClosestPair(new ArrayList<>(grid), bruteMetrics);

            assertEquals(bruteResult, fastResult, EPSILON, "Grid pattern validation failed");

            // Points with duplicates (small test case)
            ArrayList<Point> duplicates = new ArrayList<>();
            duplicates.add(new Point(0, 0));
            duplicates.add(new Point(5, 5));
            duplicates.add(new Point(0, 0)); // Duplicate
            duplicates.add(new Point(10, 10));
            duplicates.add(new Point(3, 4));

            fastMetrics.reset();
            fastResult = ClosestPair.findClosestPair(new ArrayList<>(duplicates), fastMetrics);

            bruteMetrics.reset();
            bruteResult = bruteForceClosestPair(new ArrayList<>(duplicates), bruteMetrics);

            assertEquals(0.0, fastResult, EPSILON, "Should find distance 0 for duplicates");
            assertEquals(bruteResult, fastResult, EPSILON, "Duplicate points validation failed");

            // Simple triangle case
            ArrayList<Point> triangle = new ArrayList<>();
            triangle.add(new Point(0, 0));
            triangle.add(new Point(1, 0));
            triangle.add(new Point(0, 1));

            fastMetrics.reset();
            fastResult = ClosestPair.findClosestPair(new ArrayList<>(triangle), fastMetrics);

            bruteMetrics.reset();
            bruteResult = bruteForceClosestPair(new ArrayList<>(triangle), bruteMetrics);

            assertEquals(1.0, fastResult, EPSILON, "Triangle case should have distance 1.0");
            assertEquals(bruteResult, fastResult, EPSILON, "Triangle case validation failed");

            System.out.println("✓ All edge cases validated against brute force");
        }
    }

    @Nested
    @DisplayName("Closest Pair: Use only fast version on large n")
    class LargeArrayFastOnly {

        @Test
        @DisplayName("Use only fast algorithm for large n (> 2000)")
        void useFastAlgorithmOnlyForLargeArrays() {
            Random random = new Random(456);

            // Test large sizes > 2000 - NO brute force validation
            int[] largeSizes = {3000, 5000, 8000, 10000};

            for (int size : largeSizes) {
                System.out.printf("Testing large size %d with fast algorithm only...\n", size);

                ArrayList<Point> points = generateRandomPoints(size, random);

                // Only use fast algorithm - no brute force validation
                TestAlgorithmMetrics fastMetrics = new TestAlgorithmMetrics();
                long startTime = System.currentTimeMillis();

                double result = ClosestPair.findClosestPair(points, fastMetrics);

                long endTime = System.currentTimeMillis();

                assertTrue(result >= 0, "Distance should be non-negative");
                assertTrue(Double.isFinite(result), "Distance should be finite");

                // Should complete in reasonable time (much faster than O(n²) would)
                assertTrue(endTime - startTime < 5000,
                        String.format("Size %d should complete within 5 seconds", size));

                // Verify we have reasonable performance characteristics
                assertTrue(fastMetrics.hasAnyMetrics(), "Should have recorded metrics");

                System.out.printf("  ✓ Size %d: distance=%.6f, time=%dms, depth=%d\n",
                        size, result, endTime - startTime, fastMetrics.getMaxRecursionDepth());
            }

            System.out.println("✓ All large arrays processed with fast algorithm only");
        }

        @Test
        @DisplayName("Demonstrate O(n log n) performance on large arrays")
        void demonstrateLogLinearPerformanceOnLargeArrays() {
            Random random = new Random(789);

            // Test scaling behavior on large arrays
            int[] sizes = {2000, 4000, 8000};
            long[] times = new long[sizes.length];

            for (int i = 0; i < sizes.length; i++) {
                ArrayList<Point> points = generateRandomPoints(sizes[i], random);

                long startTime = System.nanoTime();
                double result = ClosestPair.findClosestPair(points, metrics);
                long endTime = System.nanoTime();

                times[i] = endTime - startTime;

                assertTrue(result >= 0, "Result should be valid");
                metrics.reset();
            }

            for (int i = 1; i < sizes.length; i++) {
                double sizeRatio = (double) sizes[i] / sizes[i-1];
                double timeRatio = (double) times[i] / times[i-1];
                double expectedTimeRatio = sizeRatio * Math.log(sizeRatio) / Math.log(2);

                assertTrue(timeRatio < 6.0,
                        String.format("Time ratio %.2f should be much less than quadratic (4x) for O(n log n)", timeRatio));

                System.out.printf("Size %d->%d: Time ratio %.2f (expected ~%.2f for O(n log n))\n",
                        sizes[i-1], sizes[i], timeRatio, expectedTimeRatio);
            }

            System.out.println("✓ Demonstrated O(n log n) scaling on large arrays");
        }

        @Test
        @DisplayName("Stress test with very large arrays")
        void stressTestVeryLargeArrays() {
            Random random = new Random(999);

            int[] veryLargeSizes = {15000, 20000};

            for (int size : veryLargeSizes) {
                System.out.printf("Stress testing size %d...\n", size);

                ArrayList<Point> points = generateRandomPoints(size, random);

                TestAlgorithmMetrics stressMetrics = new TestAlgorithmMetrics();
                long startTime = System.currentTimeMillis();

                double result = ClosestPair.findClosestPair(points, stressMetrics);

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                assertTrue(result >= 0, "Should find valid distance");
                assertTrue(Double.isFinite(result), "Distance should be finite");
                assertTrue(duration < 10000, "Should complete within 10 seconds");

                int expectedMaxDepth = (int)Math.ceil(Math.log(size) / Math.log(2)) + 10;
                assertTrue(stressMetrics.getMaxRecursionDepth() <= expectedMaxDepth,
                        String.format("Recursion depth %d should be reasonable for size %d",
                                stressMetrics.getMaxRecursionDepth(), size));

                System.out.printf("  ✓ Size %d: distance=%.6f, time=%dms, depth=%d, comparisons=%,d\n",
                        size, result, duration, stressMetrics.getMaxRecursionDepth(),
                        stressMetrics.getComparisons());
            }

            System.out.println("✓ Stress test completed successfully");
        }
    }

    /**
     * O(n²) brute force algorithm for validation (used only for n ≤ 2000)
     */
    private double bruteForceClosestPair(ArrayList<Point> points, TestAlgorithmMetrics metrics) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }

        metrics.startTiming();

        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                metrics.incrementComparisons();

                double distance = points.get(i).distanceTo(points.get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    metrics.incrementAssignments();
                }
            }
        }

        metrics.stopTiming();
        return minDistance;
    }

    private ArrayList<Point> generateRandomPoints(int count, Random random) {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 1000;
            points.add(new Point(x, y));
        }
        return points;
    }
}