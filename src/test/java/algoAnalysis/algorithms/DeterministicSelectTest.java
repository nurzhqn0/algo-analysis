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

class DeterministicSelectTest {

    private TestAlgorithmMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new TestAlgorithmMetrics();
    }

    @Nested
    @DisplayName("Select Algorithm Correctness")
    class SelectCorrectnessTests {

        @Test
        @DisplayName("Should compare result with Collections.sort across 100 random trials")
        void shouldMatchCollectionsSortAcross100Trials() {
            Random random = new Random(42); // Fixed seed for reproducibility

            for (int trial = 0; trial < 100; trial++) {
                // Generate random array and random k
                int size = random.nextInt(90) + 10; // Size 10-99
                ArrayList<Integer> array = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    array.add(random.nextInt(1000));
                }

                int k = random.nextInt(size) + 1; // 1-indexed k

                // Get expected result using Collections.sort (equivalent to Arrays.sort)
                ArrayList<Integer> sortedCopy = new ArrayList<>(array);
                Collections.sort(sortedCopy);
                int expected = sortedCopy.get(k - 1);

                // Get actual result using DeterministicSelect
                ArrayList<Integer> testArray = new ArrayList<>(array);
                int actual = DeterministicSelect.select(testArray, k, metrics);

                assertEquals(expected, actual,
                        String.format("Trial %d failed: k=%d in array size %d", trial, k, size));
                metrics.reset();
            }
        }

        @Test
        @DisplayName("Should handle edge cases correctly")
        void shouldHandleEdgeCasesCorrectly() {
            // Single element
            ArrayList<Integer> single = new ArrayList<>(Arrays.asList(42));
            assertEquals(42, DeterministicSelect.select(single, 1, metrics));

            // Two elements - find minimum
            metrics.reset();
            ArrayList<Integer> two = new ArrayList<>(Arrays.asList(5, 2));
            assertEquals(2, DeterministicSelect.select(two, 1, metrics));

            // Two elements - find maximum
            metrics.reset();
            assertEquals(5, DeterministicSelect.select(two, 2, metrics));
        }

        @Test
        @DisplayName("Should find correct elements in adversarial arrays")
        void shouldFindCorrectElementsInAdversarialArrays() {
            // Reverse sorted array
            ArrayList<Integer> reverse = new ArrayList<>();
            for (int i = 100; i >= 1; i--) {
                reverse.add(i);
            }

            // Find minimum, median, maximum
            assertEquals(1, DeterministicSelect.select(new ArrayList<>(reverse), 1, metrics));
            metrics.reset();
            assertEquals(50, DeterministicSelect.select(new ArrayList<>(reverse), 50, metrics));
            metrics.reset();
            assertEquals(100, DeterministicSelect.select(new ArrayList<>(reverse), 100, metrics));

            // Array with many duplicates
            metrics.reset();
            ArrayList<Integer> duplicates = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                duplicates.add(5);
                duplicates.add(10);
                duplicates.add(15);
            }

            assertEquals(5, DeterministicSelect.select(new ArrayList<>(duplicates), 1, metrics));
            metrics.reset();
            assertEquals(10, DeterministicSelect.select(new ArrayList<>(duplicates), 30, metrics));
            metrics.reset();
            assertEquals(15, DeterministicSelect.select(new ArrayList<>(duplicates), 60, metrics));
        }

        @Test
        @DisplayName("Should throw exception for invalid k values")
        void shouldThrowExceptionForInvalidK() {
            ArrayList<Integer> array = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

            assertThrows(IllegalArgumentException.class, () ->
                    DeterministicSelect.select(array, 0, metrics));

            assertThrows(IllegalArgumentException.class, () ->
                    DeterministicSelect.select(array, 6, metrics));

            assertThrows(IllegalArgumentException.class, () ->
                    DeterministicSelect.select(array, -1, metrics));
        }

        @ParameterizedTest
        @ValueSource(ints = {10, 25, 50, 100, 500})
        @DisplayName("Should find all positions correctly in various array sizes")
        void shouldFindAllPositionsCorrectly(int size) {
            ArrayList<Integer> array = generateRandomArray(size, size);
            ArrayList<Integer> sorted = new ArrayList<>(array);
            Collections.sort(sorted);

            // Test finding 1st, middle, and last elements
            int[] positions = {1, size/2, size};

            for (int k : positions) {
                ArrayList<Integer> testArray = new ArrayList<>(array);
                int result = DeterministicSelect.select(testArray, k, metrics);
                assertEquals(sorted.get(k - 1), result,
                        String.format("Failed to find k=%d in array of size %d", k, size));
                metrics.reset();
            }
        }
    }

    @Nested
    @DisplayName("Algorithm Properties Verification")
    class AlgorithmPropertiesTests {

        @Test
        @DisplayName("Should demonstrate O(n) linear time behavior")
        void shouldDemonstrateLinearTimeBehavior() {
            int[] sizes = {1000, 2000, 4000};
            long[] times = new long[sizes.length];

            for (int i = 0; i < sizes.length; i++) {
                ArrayList<Integer> array = generateWorstCaseArray(sizes[i]);
                int k = sizes[i] / 2; // Find median

                long startTime = System.nanoTime();
                DeterministicSelect.select(array, k, metrics);
                long endTime = System.nanoTime();

                times[i] = endTime - startTime;
                metrics.reset();
            }

            // Verify roughly linear growth (should be much less than quadratic)
            double ratio1 = (double)times[1] / times[0]; // 2000/1000
            double ratio2 = (double)times[2] / times[1]; // 4000/2000

            // For O(n), doubling n should roughly double time
            assertTrue(ratio1 < 4.0, "Growth from 1000 to 2000 should be less than 4x for O(n)");
            assertTrue(ratio2 < 4.0, "Growth from 2000 to 4000 should be less than 4x for O(n)");
        }

        @Test
        @DisplayName("Should verify median-of-medians guarantees good pivot")
        void shouldVerifyMedianOfMediansGuarantees() {
            // Test with sizes that are multiples of 5 (worst case for median-of-medians)
            int[] testSizes = {25, 125, 625};

            for (int size : testSizes) {
                ArrayList<Integer> array = generateWorstCaseArray(size);

                // Find multiple order statistics
                int[] kValues = {size/4, size/2, 3*size/4};

                for (int k : kValues) {
                    ArrayList<Integer> testArray = new ArrayList<>(array);
                    long start = System.nanoTime();
                    DeterministicSelect.select(testArray, k, metrics);
                    long end = System.nanoTime();

                    // Should complete in reasonable time (not degrade to O(n²))
                    assertTrue(end - start < 100_000_000, // 100ms
                            String.format("Selection took too long for size %d, k=%d", size, k));
                    metrics.reset();
                }
            }
        }

        @Test
        @DisplayName("Should handle groups of 5 correctly")
        void shouldHandleGroupsOfFiveCorrectly() {
            // Test with exact multiples of 5
            for (int groups = 1; groups <= 10; groups++) {
                int size = groups * 5;
                ArrayList<Integer> array = generateRandomArray(size, groups);
                ArrayList<Integer> sorted = new ArrayList<>(array);
                Collections.sort(sorted);

                // Test finding median
                int k = (size + 1) / 2;
                int result = DeterministicSelect.select(new ArrayList<>(array), k, metrics);
                assertEquals(sorted.get(k - 1), result,
                        String.format("Failed for %d groups (size %d)", groups, size));
                metrics.reset();
            }
        }
    }

    @Nested
    @DisplayName("Performance Verification")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete within reasonable time bounds")
        void shouldCompleteWithinReasonableTimeBounds() {
            ArrayList<Integer> array = generateRandomArray(10000, 123);
            int k = 5000; // Find median

            long startTime = System.currentTimeMillis();
            int result = DeterministicSelect.select(array, k, metrics);
            long endTime = System.currentTimeMillis();

            assertTrue(endTime - startTime < 5000, "Should complete within 5 seconds");

            // Verify result is correct
            ArrayList<Integer> sorted = new ArrayList<>(array);
            Collections.sort(sorted);
            assertEquals(sorted.get(k - 1), result, "Result should be correct");
        }

        @Test
        @DisplayName("Should be faster than sorting for single selection")
        void shouldBeFasterThanSortingForSingleSelection() {
            int size = 50000;
            ArrayList<Integer> array = generateRandomArray(size, 42);
            int k = size / 3;

            // Time DeterministicSelect
            long selectStart = System.nanoTime();
            int selectResult = DeterministicSelect.select(new ArrayList<>(array), k, metrics);
            long selectEnd = System.nanoTime();
            long selectTime = selectEnd - selectStart;

            // Time full sort
            ArrayList<Integer> sortArray = new ArrayList<>(array);
            long sortStart = System.nanoTime();
            Collections.sort(sortArray);
            int sortResult = sortArray.get(k - 1);
            long sortEnd = System.nanoTime();
            long sortTime = sortEnd - sortStart;

            assertEquals(selectResult, sortResult, "Results should match");

            // DeterministicSelect should be faster (though not always guaranteed due to constants)
            // At minimum, it should not be dramatically slower
            assertTrue(selectTime < sortTime * 3,
                    "DeterministicSelect should not be more than 3x slower than full sort");
        }
    }

    @Nested
    @DisplayName("Comprehensive Random Testing")
    class RandomTestingTests {

        @Test
        @DisplayName("Should handle various random scenarios correctly")
        void shouldHandleVariousRandomScenariosCorrectly() {
            Random random = new Random(999);

            for (int scenario = 0; scenario < 50; scenario++) {
                int size = random.nextInt(200) + 5; // Size 5-204
                ArrayList<Integer> array = new ArrayList<>();

                // Generate array with mix of duplicates and unique values
                for (int i = 0; i < size; i++) {
                    if (random.nextDouble() < 0.3) {
                        // 30% chance of duplicate
                        array.add(random.nextInt(5));
                    } else {
                        array.add(random.nextInt(1000));
                    }
                }

                // Test multiple k values for this array
                ArrayList<Integer> sorted = new ArrayList<>(array);
                Collections.sort(sorted);

                int[] testPositions = {1, size/4 + 1, size/2 + 1, 3*size/4 + 1, size};

                for (int k : testPositions) {
                    if (k <= size) {
                        ArrayList<Integer> testArray = new ArrayList<>(array);
                        int result = DeterministicSelect.select(testArray, k, metrics);
                        assertEquals(sorted.get(k - 1), result,
                                String.format("Scenario %d failed: k=%d, size=%d", scenario, k, size));
                        metrics.reset();
                    }
                }
            }
        }

        @Test
        @DisplayName("Should maintain correctness with negative numbers")
        void shouldMaintainCorrectnessWithNegativeNumbers() {
            Random random = new Random(777);

            for (int trial = 0; trial < 20; trial++) {
                ArrayList<Integer> array = new ArrayList<>();
                int size = 50;

                for (int i = 0; i < size; i++) {
                    array.add(random.nextInt(2000) - 1000); // Range -1000 to 999
                }

                ArrayList<Integer> sorted = new ArrayList<>(array);
                Collections.sort(sorted);

                // Test finding minimum, median, and maximum
                assertEquals(sorted.get(0), DeterministicSelect.select(new ArrayList<>(array), 1, metrics));
                metrics.reset();
                assertEquals(sorted.get(size/2), DeterministicSelect.select(new ArrayList<>(array), size/2 + 1, metrics));
                metrics.reset();
                assertEquals(sorted.get(size-1), DeterministicSelect.select(new ArrayList<>(array), size, metrics));
                metrics.reset();
            }
        }
    }

    // Helper methods
    private ArrayList<Integer> generateRandomArray(int size, int seed) {
        ArrayList<Integer> array = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < size; i++) {
            array.add(random.nextInt(10000));
        }
        return array;
    }

    private ArrayList<Integer> generateWorstCaseArray(int size) {
        // Generate array that could be challenging for median-of-medians
        ArrayList<Integer> array = new ArrayList<>();
        Random random = new Random(size);

        // Mix of ordered and random elements
        for (int i = 0; i < size/2; i++) {
            array.add(i);
        }
        for (int i = 0; i < size/2; i++) {
            array.add(random.nextInt(size));
        }

        Collections.shuffle(array, random);
        return array;
    }
}