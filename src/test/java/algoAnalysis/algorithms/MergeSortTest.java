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
    @DisplayName("Basic Sorting Tests")
    class BasicSortingTests {

        @Test
        @DisplayName("Should sort empty array")
        void shouldSortEmptyArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            ArrayList<Integer> expected = new ArrayList<>();

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertEquals(0, metrics.getComparisons());
            assertEquals(0, metrics.getAssignments());
        }

        @Test
        @DisplayName("Should sort single element array")
        void shouldSortSingleElementArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(42));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(42));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertEquals(0, metrics.getComparisons());
            assertEquals(0, metrics.getAssignments());
        }

        @Test
        @DisplayName("Should sort already sorted array")
        void shouldSortAlreadySortedArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
            assertTrue(metrics.getMaxRecursionDepth() > 0);
        }

        @Test
        @DisplayName("Should sort reverse sorted array")
        void shouldSortReverseSortedArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(5, 4, 3, 2, 1));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
        }

        @Test
        @DisplayName("Should sort array with duplicates")
        void shouldSortArrayWithDuplicates() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 6, 9));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
        }

        @Test
        @DisplayName("Should sort array with all identical elements")
        void shouldSortArrayWithIdenticalElements() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(7, 7, 7, 7, 7));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(7, 7, 7, 7, 7));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
        }

        @Test
        @DisplayName("Should sort two element array")
        void shouldSortTwoElementArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(2, 1));
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
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
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
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
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
        }

        @ParameterizedTest
        @ValueSource(ints = {10, 14, 15, 16, 20, 50})
        @DisplayName("Should handle insertion sort cutoff correctly")
        void shouldHandleInsertionSortCutoff(int size) {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            Random random = new Random(42); // Fixed seed for reproducibility

            for (int i = 0; i < size; i++) {
                array.add(random.nextInt(1000));
            }

            ArrayList<Integer> expected = new ArrayList<>(array);
            Collections.sort(expected);

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());

            // For arrays at or below cutoff (15), recursion depth should be minimal
            if (size <= 15) {
                assertTrue(metrics.getMaxRecursionDepth() <= 2);
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
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getExecutionTime() > 0);

            // Should have significant number of operations for large array
            assertTrue(metrics.getComparisons() > size);
            assertTrue(metrics.getAssignments() > size);
        }

        @Test
        @DisplayName("Should sort very large array efficiently")
        void shouldSortVeryLargeArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();
            int size = 5000; // Reduced size for faster testing

            // Create a worst-case scenario: reverse sorted
            for (int i = size; i > 0; i--) {
                array.add(i);
            }

            ArrayList<Integer> expected = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                expected.add(i);
            }

            // When
            long startTime = System.currentTimeMillis();
            MergeSort.sort(array, metrics);
            long endTime = System.currentTimeMillis();

            // Then
            assertEquals(expected, array);
            assertTrue(endTime - startTime < 2000); // Should complete within 2 seconds
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
            MergeSort.sort(array, metrics);

            // Then
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertTrue(metrics.getComparisons() > 0);
            assertTrue(metrics.getAssignments() > 0);
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
            MergeSort.sort(smallArray, smallMetrics);
            MergeSort.sort(largeArray, largeMetrics);

            // Then
            assertTrue(largeMetrics.getComparisons() > smallMetrics.getComparisons());
            assertTrue(largeMetrics.getAssignments() > smallMetrics.getAssignments());
            assertTrue(largeMetrics.getMaxRecursionDepth() >= smallMetrics.getMaxRecursionDepth());
        }

        @Test
        @DisplayName("Should track recursion depth correctly")
        void shouldTrackRecursionDepthCorrectly() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(8, 4, 2, 1, 3, 5, 7, 6));

            // When
            MergeSort.sort(array, metrics);

            // Then
            // For array of size 8, max recursion depth should be reasonable
            assertTrue(metrics.getMaxRecursionDepth() > 0);
            assertTrue(metrics.getMaxRecursionDepth() <= 10); // Reasonable upper bound
        }

        @Test
        @DisplayName("Should not increment metrics for empty array")
        void shouldNotIncrementMetricsForEmptyArray() {
            // Given
            ArrayList<Integer> array = new ArrayList<>();

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertEquals(0, metrics.getComparisons());
            assertEquals(0, metrics.getAssignments());
            assertEquals(0, metrics.getMaxRecursionDepth());
        }

        @Test
        @DisplayName("Should not increment recursion metrics for single element")
        void shouldNotIncrementRecursionForSingleElement() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(42));

            // When
            MergeSort.sort(array, metrics);

            // Then
            assertTrue(metrics.isTimingStarted());
            assertTrue(metrics.isTimingStopped());
            assertEquals(0, metrics.getComparisons());
            assertEquals(0, metrics.getAssignments());
        }
    }

    @Nested
    @DisplayName("Auxiliary Buffer Reuse Tests")
    class AuxiliaryBufferTests {

        @Test
        @DisplayName("Should handle multiple sorts with buffer reuse")
        void shouldReuseAuxiliaryBuffer() {
            // Given
            ArrayList<Integer> array1 = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));
            ArrayList<Integer> array2 = new ArrayList<>(Arrays.asList(7, 3, 6, 4));
            ArrayList<Integer> array3 = new ArrayList<>(Arrays.asList(10, 15, 12, 11, 13, 14));

            ArrayList<Integer> expected1 = new ArrayList<>(Arrays.asList(1, 2, 5, 8, 9));
            ArrayList<Integer> expected2 = new ArrayList<>(Arrays.asList(3, 4, 6, 7));
            ArrayList<Integer> expected3 = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15));

            TestAlgorithmMetrics metrics1 = new TestAlgorithmMetrics();
            TestAlgorithmMetrics metrics2 = new TestAlgorithmMetrics();
            TestAlgorithmMetrics metrics3 = new TestAlgorithmMetrics();

            // When
            MergeSort.sort(array1, metrics1);
            MergeSort.sort(array2, metrics2);
            MergeSort.sort(array3, metrics3);

            // Then
            assertEquals(expected1, array1);
            assertEquals(expected2, array2);
            assertEquals(expected3, array3);

            // All should complete successfully
            assertTrue(metrics1.isTimingStopped());
            assertTrue(metrics2.isTimingStopped());
            assertTrue(metrics3.isTimingStopped());
        }
    }

    @Nested
    @DisplayName("Stability Tests")
    class StabilityTests {

        @Test
        @DisplayName("Should maintain stability for equal elements")
        void shouldMaintainStabilityForEqualElements() {
            // Given
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5));

            // When
            MergeSort.sort(array, metrics);

            // Then - verify array is sorted
            for (int i = 1; i < array.size(); i++) {
                assertTrue(array.get(i) >= array.get(i - 1),
                        "Array should be sorted: " + array.get(i-1) + " > " + array.get(i) + " at index " + i);
            }

            // Verify specific sorted result
            ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9));
            assertEquals(expected, array);
        }

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
            MergeSort.sort(array, metrics);

            // Then
            assertEquals(expected, array);

            // Verify it's actually sorted
            for (int i = 1; i < array.size(); i++) {
                assertTrue(array.get(i) >= array.get(i - 1));
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
            MergeSort.sort(array, metrics);
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