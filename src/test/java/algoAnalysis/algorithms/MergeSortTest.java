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

class MergeSortTest {

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
                ArrayList<Integer> array = new ArrayList<>();
                int size = random.nextInt(100) + 10; // Size 10-109

                for (int i = 0; i < size; i++) {
                    array.add(random.nextInt(1000));
                }

                ArrayList<Integer> expected = new ArrayList<>(array);
                Collections.sort(expected);

                MergeSort.sort(array, metrics);

                assertEquals(expected, array, "Trial " + trial + " failed");
                metrics.reset();
            }
        }

        @Test
        @DisplayName("Should sort adversarial arrays correctly")
        void shouldSortAdversarialArraysCorrectly() {
            ArrayList<Integer> reverseArray = new ArrayList<>();
            for (int i = 100; i >= 1; i--) {
                reverseArray.add(i);
            }

            ArrayList<Integer> expected = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                expected.add(i);
            }

            MergeSort.sort(reverseArray, metrics);
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

            MergeSort.sort(duplicateArray, metrics);
            assertEquals(expectedDuplicates, duplicateArray);
        }

        @Test
        @DisplayName("Should handle edge cases correctly")
        void shouldHandleEdgeCasesCorrectly() {
            ArrayList<Integer> empty = new ArrayList<>();
            MergeSort.sort(empty, metrics);
            assertTrue(empty.isEmpty());

            metrics.reset();
            ArrayList<Integer> single = new ArrayList<>(Arrays.asList(42));
            MergeSort.sort(single, metrics);
            assertEquals(Arrays.asList(42), single);

            metrics.reset();
            ArrayList<Integer> two = new ArrayList<>(Arrays.asList(2, 1));
            MergeSort.sort(two, metrics);
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

            MergeSort.sort(array, metrics);

            assertEquals(expected, array);
            assertTrue(isSorted(array), "Array should be sorted");
        }
    }

    @Nested
    @DisplayName("Recursion Depth Verification")
    class RecursionDepthTests {

        @Test
        @DisplayName("Should verify MergeSort recursion depth bounds")
        void shouldVerifyMergeSortRecursionDepthBounds() {
            for (int n : Arrays.asList(100, 500, 1000, 2000)) {
                ArrayList<Integer> array = generateRandomArray(n, 42);
                metrics.reset();

                MergeSort.sort(array, metrics);

                // MergeSort should have depth ≈ log2(n)
                int expectedMaxDepth = (int)Math.ceil(Math.log(n) / Math.log(2)) + 5; // +5 for safety margin

                assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                        String.format("For n=%d, recursion depth %d should be ≤ %d",
                                n, metrics.getMaxRecursionDepth(), expectedMaxDepth));
            }
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
            MergeSort.sort(array, metrics);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 5000, "Should complete within 5 seconds");
            assertTrue(isSorted(array), "Array should be properly sorted");
        }

        @Test
        @DisplayName("Should demonstrate O(n log n) behavior")
        void shouldDemonstrateLogLinearBehavior() {
            int[] sizes = {1000, 2000, 4000};
            long[] times = new long[sizes.length];

            for (int i = 0; i < sizes.length; i++) {
                ArrayList<Integer> array = generateRandomArray(sizes[i], 42);
                metrics.reset();

                long start = System.nanoTime();
                MergeSort.sort(array, metrics);
                long end = System.nanoTime();

                times[i] = end - start;
            }

            double ratio1 = (double)times[1] / times[0]; // 2000/1000
            double ratio2 = (double)times[2] / times[1]; // 4000/2000

            assertTrue(ratio1 < 4.0, "Growth from 1000 to 2000 should be less than 4x");
            assertTrue(ratio2 < 4.0, "Growth from 2000 to 4000 should be less than 4x");
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