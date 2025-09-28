package algoAnalysis.algorithms;

import algoAnalysis.metrics.AlgorithmMetrics;

import java.util.ArrayList;
import java.util.Random;

public class QuickSort {
    public static int partition(ArrayList<Integer> arr, int low, int high, AlgorithmMetrics metrics) {
        Random random = new Random();
        int randomIndex = random.nextInt(high - low + 1) + low;

        swap(arr, randomIndex, high, metrics);

        int pivot = arr.get(high);
        int i = low - 1;

        for (int j = low; j <= high - 1; j++) {
            metrics.incrementComparisons();

            if (arr.get(j) < pivot) {
                i++;
                swap(arr, i, j, metrics);
            }
        }

        swap(arr, i + 1, high, metrics);
        return i + 1;
    }

    public static void swap(ArrayList<Integer> arr, int i, int j, AlgorithmMetrics metrics) {
        metrics.incrementSwaps();

        int temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }

    public static void quickSort(ArrayList<Integer> arr, int low, int high,  AlgorithmMetrics metrics) {
        metrics.enterRecursion();

        if (low < high) {
            metrics.incrementComparisons();

            int pi = partition(arr, low, high, metrics);
            quickSort(arr, low, pi - 1, metrics);
            quickSort(arr, pi + 1, high, metrics);
        }

        metrics.exitRecursion();
    }

    public static void sort(ArrayList<Integer> arr, AlgorithmMetrics metrics) {
        metrics.startTiming();
        quickSort(arr, 0, arr.size() - 1, metrics);
        metrics.stopTiming();
    }
}
