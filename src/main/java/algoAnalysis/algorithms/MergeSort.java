package algoAnalysis.algorithms;

import algoAnalysis.metrics.AlgorithmMetrics;

import java.util.ArrayList;

/**
 * Merge Sort
 * @author nurzhqn0
 * @version 1.0
 */
public class MergeSort {
    private static final int INSERTION_SORT_CUTOFF = 15;

    /**
     * Reusable auxiliary buffer
     */
    private static ArrayList<Integer> auxBuffer;

    private static void ensureAuxBufferCapacity(int size) {
        if (auxBuffer == null) {
            auxBuffer = new ArrayList<>(size);
        }

        while (auxBuffer.size() < size) {
            auxBuffer.add(0);
        }
    }

    /**
     * Linear merge using reusable auxiliary buffer
     * Time Complexity: O(n)
     * Space Complexity: O(1) additional space (reuses buffer)
     */
    private static void merge(ArrayList<Integer> arr, int left, int middle, int right,
                              ArrayList<Integer> aux, AlgorithmMetrics metrics) {

        for (int i = left; i <= right; i++) {
            aux.set(i, arr.get(i));
            metrics.incrementAssignments();
        }

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            metrics.incrementComparisons();

            if (aux.get(i) <= aux.get(j)) {
                arr.set(k, aux.get(i));
                metrics.incrementAssignments();
                i++;
            } else {
                arr.set(k, aux.get(j));
                metrics.incrementAssignments();
                j++;
            }
            k++;
        }

        while (i <= middle) {
            arr.set(k, aux.get(i));
            metrics.incrementAssignments();
            i++;
            k++;
        }

        while (j <= right) {
            arr.set(k, aux.get(j));
            metrics.incrementAssignments();
            j++;
            k++;
        }
    }

    /**
     * Insertion sort for small subarrays
     * Time Complexity: O(n²) but very efficient for small n
     * Space Complexity: O(1)
     */
    private static void insertionSort(ArrayList<Integer> arr, int left, int right, AlgorithmMetrics metrics) {
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

    private static void mergeSort(ArrayList<Integer> arr, int left, int right,
                                  ArrayList<Integer> aux, AlgorithmMetrics metrics) {
        metrics.enterRecursion();

        // Small-n cutoff: insertion sort for small arrays
        if (right - left + 1 <= INSERTION_SORT_CUTOFF) {
            insertionSort(arr, left, right, metrics);
            metrics.exitRecursion();
            return;
        }

        if (left < right) {
            metrics.incrementComparisons();

            int middle = left + (right - left) / 2;

            mergeSort(arr, left, middle, aux, metrics);
            mergeSort(arr, middle + 1, right, aux, metrics);

            merge(arr, left, middle, right, aux, metrics);
        }

        metrics.exitRecursion();
    }

    public static void sort(ArrayList<Integer> arr, AlgorithmMetrics metrics) {
        metrics.startTiming();

        if (arr.size() <= 1) {
            metrics.stopTiming();
            return;
        }

        ensureAuxBufferCapacity(arr.size());

        mergeSort(arr, 0, arr.size() - 1, auxBuffer, metrics);

        metrics.stopTiming();
    }
}
