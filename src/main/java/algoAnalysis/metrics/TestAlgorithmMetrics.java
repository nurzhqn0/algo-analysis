package algoAnalysis.metrics;

/**
 * Test Algorithm Metrics
 * @author nurzhqn0
 * @version 1.0
 */
public class TestAlgorithmMetrics extends AlgorithmMetrics {
    private long comparisons = 0;
    private long assignments = 0;
    private long swaps = 0;
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
    public void incrementAssignments() {
        assignments++;
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

    public long getComparisons() {
        return comparisons;
    }

    public long getAssignments() {
        return assignments;
    }

    public long getSwaps() {
        return swaps;
    }

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public int getCurrentRecursionDepth() {
        return recursionDepth;
    }

    public boolean isTimingStarted() {
        return timingStarted;
    }

    public boolean isTimingStopped() {
        return timingStopped;
    }

    public long getExecutionTime() {
        return endTime - startTime;
    }

    public double getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
    }

    public void reset() {
        comparisons = 0;
        assignments = 0;
        swaps = 0;
        recursionDepth = 0;
        maxRecursionDepth = 0;
        timingStarted = false;
        timingStopped = false;
        startTime = 0;
        endTime = 0;
    }

    public String getMetricsSummary() {
        return String.format(
                "Metrics Summary: Comparisons=%d, Assignments=%d, Swaps=%d, " +
                        "MaxRecursionDepth=%d, ExecutionTime=%d ns",
                comparisons, assignments, swaps, maxRecursionDepth, getExecutionTime()
        );
    }

    public boolean hasAnyMetrics() {
        return comparisons > 0 || assignments > 0 || swaps > 0 ||
                maxRecursionDepth > 0 || timingStarted;
    }
}