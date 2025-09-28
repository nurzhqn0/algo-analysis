package algoAnalysis.algorithms;

import algoAnalysis.metrics.TestAlgorithmMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Quick Sort
 * @author nurzhqn0
 * @version 1.0
 */
class QuickSortTest {

    private TestAlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new TestAlgorithmMetrics();
    }

    @Nested
    @DisplayName("Sorting Correctness Tests")
    class SortingCorrectnessTests {

        @Test
        @DisplayName("Should sort random arrays correctly")
        void shouldSortRandomArraysCorrectly() {
            Random random = new Random(42);

            for (int trial = 0; trial < 20; trial++) {
                // Generate random array
                ArrayList<Integer> array = new ArrayList<>();
                int size = random.nextInt(100) + 10; // Size 10-109

                for (int i = 0; i < size; i++) {
                    array.add(random.nextInt(1000));
                }

                ArrayList<Integer> expected = new ArrayList<>(array);
                Collections.sort(expected);

                // When
                QuickSort.sort(array, metrics);

                // Then
                assertEquals(expected, array, "Trial " + trial + " failed");
                metrics.reset();
            }
        }

        @Test
        @DisplayName("Should sort adversarial arrays correctly")
        void shouldSortAdversarialArraysCorrectly() {
            // Test reverse sorted array
            ArrayList<Integer> reverseArray = new ArrayList<>();
            for (int i = 100; i >= 1; i--) {
                reverseArray.add(i);
            }

            ArrayList<Integer> expected = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                expected.add(i);
            }

            QuickSort.sort(reverseArray, metrics);
            assertEquals(expected, reverseArray);

            metrics.reset();
            ArrayList<Integer> duplicateArray = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                duplicateArray.add(5);
                duplicateArray.add(10);
                duplicateArray.add(15);
            }

            ArrayList<Integer> expectedDuplicates = new ArrayList<>(duplicateArray);
            Collections.sort(expectedDuplicates);

            QuickSort.sort(duplicateArray, metrics);
            assertEquals(expectedDuplicates, duplicateArray);
        }

        @Test
        @DisplayName("Should handle edge cases correctly")
        void shouldHandleEdgeCasesCorrectly() {
            // Empty array
            ArrayList<Integer> empty = new ArrayList<>();
            QuickSort.sort(empty, metrics);
            assertTrue(empty.isEmpty());

            // Single element
            metrics.reset();
            ArrayList<Integer> single = new ArrayList<>(Arrays.asList(42));
            QuickSort.sort(single, metrics);
            assertEquals(Arrays.asList(42), single);

            // Two elements
            metrics.reset();
            ArrayList<Integer> two = new ArrayList<>(Arrays.asList(2, 1));
            QuickSort.sort(two, metrics);
            assertEquals(Arrays.asList(1, 2), two);
        }

        @ParameterizedTest
        @ValueSource(ints = {100, 500, 1000, 2000})
        @DisplayName("Should sort large arrays correctly")
        void shouldSortLargeArraysCorrectly(int size) {
            ArrayList<Integer> array = new ArrayList<>();
            Random random = new Random(size); // Use size as seed for reproducibility

            for (int i = 0; i < size; i++) {
                array.add(random.nextInt(10000));
            }

            ArrayList<Integer> expected = new ArrayList<>(array);
            Collections.sort(expected);

            QuickSort.sort(array, metrics);

            assertEquals(expected, array);
            assertTrue(isSorted(array), "Array should be sorted");
        }
    }

    @Nested
    @DisplayName("Recursion Depth Verification")
    class RecursionDepthTests {

        @Test
        @DisplayName("Should verify QuickSort recursion depth bounds")
        void shouldVerifyQuickSortRecursionDepthBounds() {
            for (int n : Arrays.asList(100, 500, 1000, 2000)) {
                ArrayList<Integer> array = generateRandomArray(n, 42);
                metrics.reset();

                QuickSort.sort(array, metrics);

                // QuickSort should have depth ≲ 2*floor(log₂(n)) + O(1) under randomized pivot
                int expectedMaxDepth = 2 * (int)Math.floor(Math.log(n) / Math.log(2)) + 10; // +10 for O(1) constant

                assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                        String.format("For n=%d, QS recursion depth %d should be ≤ %d (2*floor(log₂(n)) + O(1))",
                                n, metrics.getMaxRecursionDepth(), expectedMaxDepth));
            }
        }

        @Test
        @DisplayName("Should verify recursion depth across multiple random trials")
        void shouldVerifyRecursionDepthAcrossTrials() {
            int n = 1000;
            int maxDepthObserved = 0;

            // Test across 10 trials to account for randomization
            for (int trial = 0; trial < 10; trial++) {
                ArrayList<Integer> array = generateRandomArray(n, trial);
                metrics.reset();

                QuickSort.sort(array, metrics);
                maxDepthObserved = Math.max(maxDepthObserved, metrics.getMaxRecursionDepth());
            }

            int expectedMaxDepth = 2 * (int)Math.floor(Math.log(n) / Math.log(2)) + 10;
            assertTrue(maxDepthObserved <= expectedMaxDepth,
                    String.format("Max observed depth %d should be ≤ %d across all trials",
                            maxDepthObserved, expectedMaxDepth));
        }
    }

    @Nested
    @DisplayName("Performance Verification")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete within reasonable time bounds")
        void shouldCompleteWithinReasonableTimeBounds() {
            ArrayList<Integer> array = generateRandomArray(5000, 123);

            long startTime = System.currentTimeMillis();
            QuickSort.sort(array, metrics);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 5000, "Should complete within 5 seconds");
            assertTrue(isSorted(array), "Array should be properly sorted");
        }
    }

    private ArrayList<Integer> generateRandomArray(int size, int seed) {
        ArrayList<Integer> array = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(10000));
        }
        return array;
    }

    private boolean isSorted(ArrayList<Integer> array) {
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) < array.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
}