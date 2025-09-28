package algoAnalysis.algorithms;

import algoAnalysis.metrics.AlgorithmMetrics;
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

class QuickSortTest {

    private TestAlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new TestAlgorithmMetrics();
    }

    /**
     * Test implementation of AlgorithmMetrics for testing purposes
     */
    private static class TestAlgorithmMetrics extends AlgorithmMetrics {
        private int comparisons = 0;
        private int swaps = 0;
        private int recursionDepth = 0;
        private int maxRecursionDepth = 0;
        private boolean timingStarted = false;
        private boolean timingStopped = false;
        private long startTime;
        private long endTime;

        @Override
        public void incrementComparisons() {
            comparisons++;
        }

        @Override
        public void incrementSwaps() {
            swaps++;
        }

        @Override
        public void enterRecursion() {
            recursionDepth++;
            maxRecursionDepth = Math.max(maxRecursionDepth, recursionDepth);
        }

        @Override
        public void exitRecursion() {
            recursionDepth--;
        }

        @Override
        public void startTiming() {
            timingStarted = true;
            startTime = System.nanoTime();
        }

        @Override
        public void stopTiming() {
            timingStopped = true;
            endTime = System.nanoTime();
        }

        // Getters for testing
        public long getComparisons() { return comparisons; }
        public long getSwaps() { return swaps; }
        public int getMaxRecursionDepth() { return maxRecursionDepth; }
        public boolean isTimingStarted() { return timingStarted; }
        public boolean isTimingStopped() { return timingStopped; }
        public long getExecutionTime() { return endTime - startTime; }
    }

    @Nested
    @DisplayName("Basic Sorting Tests")
    class BasicSortingTests {

        @Test
        @DisplayName("Should sort empty array")
        void shouldSortEmptyArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            ArrayList<Integer> expected = new ArrayList<>();

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
        }

        @Test
        @DisplayName("Should sort single element array")
        void shouldSortSingleElementArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(42));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(42));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getMaxRecursionDepth() > 0); // QuickSort always calls recursion
        }

        @Test
        @DisplayName("Should sort two element array")
        void shouldSortTwoElementArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(2, 1));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
            assertTrue(metrics.getMaxRecursionDepth() > 0);
        }

        @Test
        @DisplayName("Should sort already sorted array")
        void shouldSortAlreadySortedArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0); // Random pivot causes swaps
            assertTrue(metrics.getMaxRecursionDepth() > 0);
        }

        @Test
        @DisplayName("Should sort reverse sorted array")
        void shouldSortReverseSortedArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 4, 3, 2, 1));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should sort array with duplicates")
        void shouldSortArrayWithDuplicates() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 6, 9));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should sort array with all identical elements")
        void shouldSortArrayWithIdenticalElements() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(7, 7, 7, 7, 7));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(7, 7, 7, 7, 7));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            // Swaps may or may not occur with identical elements depending on random pivot
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should sort array with negative numbers")
        void shouldSortArrayWithNegativeNumbers() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(-5, 3, -1, 8, -10, 0));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(-10, -5, -1, 0, 3, 8));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should sort array with maximum and minimum integer values")
        void shouldSortArrayWithExtremeValues() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(
                    Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 1, -1
            ));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(
                    Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE
            ));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
        }

        @ParameterizedTest
        @ValueSource(ints = {3, 5, 8, 10, 15, 20, 50})
        @DisplayName("Should handle various array sizes correctly")
        void shouldHandleVariousArraySizes(int size) {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            Random random = new Random(42); // Fixed seed for reproducibility

            for (int i = 0; i < size; i++) {
                array.add(random.nextInt(1000));
            }

            ArrayList<Integer> expected = new ArrayList<>(array);
            Collections.sort(expected);

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());

            if (size > 1) {
                assertTrue(metrics.getComparisons() > 0);
                assertTrue(metrics.getSwaps() > 0);
                assertTrue(metrics.getMaxRecursionDepth() > 0);
            }
        }
    }

    @Nested
    @DisplayName("Large Array Tests")
    class LargeArrayTests {

        @Test
        @DisplayName("Should sort large random array")
        void shouldSortLargeRandomArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            Random random = new Random(123); // Fixed seed for reproducibility
            int size = 1000;

            for (int i = 0; i < size; i++) {
                array.add(random.nextInt(10000));
            }

            ArrayList<Integer> expected = new ArrayList<>(array);
            Collections.sort(expected);

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getExecutionTime() > 0);

            // Should have significant number of operations for large array
            assertTrue(metrics.getComparisons() > size);
            assertTrue(metrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should sort very large array efficiently")
        void shouldSortVeryLargeArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            int size = 5000; // Reduced size for faster testing

            // Create a mixed scenario: partially sorted
            for (int i = 1; i <= size/2; i++) {
                array.add(i);
            }
            for (int i = size; i > size/2; i--) {
                array.add(i);
            }

            ArrayList<Integer> expected = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                expected.add(i);
            }

            // When
            long startTime = System.currentTimeMillis();
            QuickSort.sort(array, metrics);
            long endTime = System.currentTimeMillis();

            // Then
            assertEquals(expected, array);
            assertTrue(endTime - startTime < 3000); // Should complete within 3 seconds
            assertTrue(metrics.getExecutionTime() > 0);
        }
    }

    @Nested
    @DisplayName("Metrics Verification Tests")
    class MetricsTests {

        @Test
        @DisplayName("Should track metrics correctly for basic array")
        void shouldTrackMetricsCorrectly() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getSwaps() > 0);
            assertTrue(metrics.getMaxRecursionDepth() > 0);
            assertTrue(metrics.getExecutionTime() > 0);
        }

        @Test
        @DisplayName("Should have more operations for larger arrays")
        void shouldHaveMoreOperationsForLargerArrays() {
            // Given
            ArrayList<Integer> smallArray = new ArrayList<>(Arrays.asList(3, 1, 4));
            ArrayList<Integer> largeArray = new ArrayList<>(Arrays.asList(8, 3, 1, 7, 4, 6, 2, 5, 9));

            TestAlgorithmMetrics smallMetrics = new TestAlgorithmMetrics();
            TestAlgorithmMetrics largeMetrics = new TestAlgorithmMetrics();

            // When
            QuickSort.sort(smallArray, smallMetrics);
            QuickSort.sort(largeArray, largeMetrics);

            // Then
            assertTrue(largeMetrics.getComparisons() > smallMetrics.getComparisons());
            // Note: Swaps might vary due to randomization, so we check that both have swaps
            assertTrue(smallMetrics.getSwaps() > 0);
            assertTrue(largeMetrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should track recursion depth correctly")
        void shouldTrackRecursionDepthCorrectly() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(8, 4, 2, 1, 3, 5, 7, 6));

            // When
            QuickSort.sort(array, metrics);

            // Then
            // For array of size 8, max recursion depth should be reasonable
            assertTrue(metrics.getMaxRecursionDepth() > 0);
            assertTrue(metrics.getMaxRecursionDepth() <= 20); // Reasonable upper bound considering worst case
        }
    }

    @Nested
    @DisplayName("Partition Function Tests")
    class PartitionTests {

        @Test
        @DisplayName("Should partition array correctly")
        void shouldPartitionArrayCorrectly() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(3, 6, 8, 10, 1, 2, 1));
            int low = 0;
            int high = array.size() - 1;
            TestAlgorithmMetrics partitionMetrics = new TestAlgorithmMetrics();

            // When
            int pivotIndex = QuickSort.partition(array, low, high, partitionMetrics);

            // Then
            assertTrue(pivotIndex >= low && pivotIndex <= high);

            // All elements to the left of pivot should be <= pivot
            int pivotValue = array.get(pivotIndex);
            for (int i = low; i < pivotIndex; i++) {
                assertTrue(array.get(i) <= pivotValue);
            }

            // All elements to the right of pivot should be >= pivot
            for (int i = pivotIndex + 1; i <= high; i++) {
                assertTrue(array.get(i) >= pivotValue);
            }

            // Should have recorded some comparisons and swaps
            assertTrue(partitionMetrics.getComparisons() > 0);
            assertTrue(partitionMetrics.getSwaps() > 0);
        }

        @Test
        @DisplayName("Should handle partition with duplicates")
        void shouldHandlePartitionWithDuplicates() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5));
            int low = 0;
            int high = array.size() - 1;
            TestAlgorithmMetrics partitionMetrics = new TestAlgorithmMetrics();

            // When
            int pivotIndex = QuickSort.partition(array, low, high, partitionMetrics);

            // Then
            assertTrue(pivotIndex >= low && pivotIndex <= high);
            assertEquals(5, (int) array.get(pivotIndex));

            // All elements should still be 5
            for (int value : array) {
                assertEquals(5, (int) value);
            }
        }
    }

    @Nested
    @DisplayName("Swap Function Tests")
    class SwapTests {

        @Test
        @DisplayName("Should swap two elements correctly")
        void shouldSwapTwoElementsCorrectly() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 3, 8, 1));
            TestAlgorithmMetrics swapMetrics = new TestAlgorithmMetrics();
            int originalFirst = array.get(0);
            int originalSecond = array.get(2);

            // When
            QuickSort.swap(array, 0, 2, swapMetrics);

            // Then
            assertEquals(originalSecond, (int) array.get(0));
            assertEquals(originalFirst, (int) array.get(2));
            assertEquals(1, swapMetrics.getSwaps());
        }

        @Test
        @DisplayName("Should handle swapping same element")
        void shouldHandleSwappingSameElement() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 3, 8, 1));
            TestAlgorithmMetrics swapMetrics = new TestAlgorithmMetrics();
            ArrayList<Integer> originalArray = new ArrayList<>(array);

            // When
            QuickSort.swap(array, 1, 1, swapMetrics);

            // Then
            assertEquals(originalArray, array); // Array should remain unchanged
            assertEquals(1, swapMetrics.getSwaps()); // But swap should still be counted
        }
    }

    @Nested
    @DisplayName("Randomization Tests")
    class RandomizationTests {

        @Test
        @DisplayName("Should produce consistent results despite randomization")
        void shouldProduceConsistentResults() {
            // Given
            ArrayList<Integer> array1 = new ArrayList<>(Arrays.asList(9, 4, 7, 1, 3, 8, 2, 6, 5));
            ArrayList<Integer> array2 = new ArrayList<>(array1);
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

            TestAlgorithmMetrics metrics1 = new TestAlgorithmMetrics();
            TestAlgorithmMetrics metrics2 = new TestAlgorithmMetrics();

            // When
            QuickSort.sort(array1, metrics1);
            QuickSort.sort(array2, metrics2);

            // Then
            assertEquals(expected, array1);
            assertEquals(expected, array2);
            assertEquals(array1, array2); // Both should be sorted identically
        }
    }

    @Nested
    @DisplayName("Correctness Verification Tests")
    class CorrectnessTests {

        @Test
        @DisplayName("Should verify sorting correctness with random data")
        void shouldVerifySortingCorrectnessWithRandomData() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            Random random = new Random(999);

            for (int i = 0; i < 100; i++) {
                array.add(random.nextInt(1000));
            }

            ArrayList<Integer> expected = new ArrayList<>(array);
            Collections.sort(expected);

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);

            // Verify it's actually sorted
            for (int i = 1; i < array.size(); i++) {
                assertTrue(array.get(i) >= array.get(i - 1));
            }
        }

        @Test
        @DisplayName("Should maintain all original elements after sorting")
        void shouldMaintainAllOriginalElementsAfterSorting() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));
            ArrayList<Integer> originalCopy = new ArrayList<>(array);

            // When
            QuickSort.sort(array, metrics);

            // Then
            assertEquals(originalCopy.size(), array.size());

            // Count frequency of each element to ensure nothing was lost or added
            for (int original : originalCopy) {
                long originalCount = originalCopy.stream().filter(x -> x.equals(original)).count();
                long sortedCount = array.stream().filter(x -> x.equals(original)).count();
                assertEquals(originalCount, sortedCount);
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete sorting within reasonable time")
        void shouldCompleteSortingWithinReasonableTime() {
            // Given
            ArrayList<Integer> array = generateRandomArray(2000, 123);

            // When
            long start = System.currentTimeMillis();
            QuickSort.sort(array, metrics);
            long end = System.currentTimeMillis();

            // Then
            assertTrue(end - start < 1000, "Sorting 2000 elements should complete within 1 second");
            assertTrue(isSorted(array), "Array should be properly sorted");
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
}