package algoAnalysis.algorithms;

import algoAnalysis.metrics.AlgorithmMetrics;

import java.util.ArrayList;

public class DeterministicSelect {
    private static final int GROUP_SIZE = 5;

    public static int select(ArrayList<Integer> arr, int k, AlgorithmMetrics metrics) {
        if (k < 1 || k > arr.size()) {
            throw new IllegalArgumentException("k must be between 1 and array size");
        }

        metrics.startTiming();
        int result = deterministicSelect(arr, 0, arr.size() - 1, k - 1, metrics);
        metrics.stopTiming();
        return result;
    }

    private static int deterministicSelect(ArrayList<Integer> arr, int left, int right,
                                           int k, AlgorithmMetrics metrics) {
        metrics.enterRecursion();

        if (right - left + 1 <= GROUP_SIZE) {
            insertionSort(arr, left, right, metrics);
            metrics.exitRecursion();
            return arr.get(left + k);
        }

        int pivotValue = findMedianOfMedians(arr, left, right, metrics);

        int pivotIndex = partition(arr, left, right, pivotValue, metrics);

        int pivotRank = pivotIndex - left;

        int result;
        if (k == pivotRank) {
            // found
            result = arr.get(pivotIndex);
        } else if (k < pivotRank) {
            // left
            result = deterministicSelect(arr, left, pivotIndex - 1, k, metrics);
        } else {
            // right
            result = deterministicSelect(arr, pivotIndex + 1, right, k - pivotRank - 1, metrics);
        }

        metrics.exitRecursion();
        return result;
    }

    /**
     * median-of-medians pivot
     * Groups array into groups of 5, finds median of each group,
     * then recursively finds median of those medians
     */
    private static int findMedianOfMedians(ArrayList<Integer> arr, int left, int right,
                                           AlgorithmMetrics metrics) {
        int n = right - left + 1;

        if (n <= GROUP_SIZE) {
            insertionSort(arr, left, right, metrics);
            return arr.get(left + n / 2);
        }

        ArrayList<Integer> medians = new ArrayList<>();

        for (int i = left; i <= right; i += GROUP_SIZE) {
            int groupRight = Math.min(i + GROUP_SIZE - 1, right);

            insertionSort(arr, i, groupRight, metrics);
            int medianIndex = i + (groupRight - i) / 2;
            medians.add(arr.get(medianIndex));
            metrics.incrementAssignments();
        }

        return deterministicSelect(medians, 0, medians.size() - 1,
                medians.size() / 2, metrics);
    }

    /**
     * Partition array around pivot value using three-way partitioning
     * Returns index of pivot after partitioning
     */
    private static int partition(ArrayList<Integer> arr, int left, int right,
                                 int pivotValue, AlgorithmMetrics metrics) {
        int pivotIndex = -1;
        for (int i = left; i <= right; i++) {
            metrics.incrementComparisons();
            if (arr.get(i).equals(pivotValue)) {
                pivotIndex = i;
                break;
            }
        }

        if (pivotIndex != -1) {
            swap(arr, pivotIndex, right, metrics);
        }

        int i = left - 1;

        for (int j = left; j < right; j++) {
            metrics.incrementComparisons();
            if (arr.get(j) <= pivotValue) {
                i++;
                swap(arr, i, j, metrics);
            }
        }

        swap(arr, i + 1, right, metrics);
        return i + 1;
    }

    /**
     * insertion sort for small subarrays
     * Time Complexity: O(n²), efficient for small subarrays
     */
    private static void insertionSort(ArrayList<Integer> arr, int left, int right,
                                      AlgorithmMetrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr.get(i);
            metrics.incrementAssignments();
            int j = i - 1;

            while (j >= left) {
                metrics.incrementComparisons();
                if (arr.get(j) <= key) break;

                arr.set(j + 1, arr.get(j));
                metrics.incrementAssignments();
                j--;
            }

            arr.set(j + 1, key);
            metrics.incrementAssignments();
        }
    }

    private static void swap(ArrayList<Integer> arr, int i, int j, AlgorithmMetrics metrics) {
        if (i != j) {
            metrics.incrementSwaps();
            int temp = arr.get(i);
            arr.set(i, arr.get(j));
            arr.set(j, temp);
            metrics.incrementAssignments();
        }
    }

}