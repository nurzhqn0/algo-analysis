package algoAnalysis.metrics;

/**
 * Algorithm Metrics class
 * @author nurzhqn0
 * @version 1.0
 */
public class AlgorithmMetrics {
    private long comparisons;
    private long swaps;
    private long assignments;
    private int maxDepth;
    private int currentDepth;
    private long startTime;
    private long endTime;

    public AlgorithmMetrics() {
        reset();
    }

    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.assignments = 0;
        this.maxDepth = 0;
        this.currentDepth = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementSwaps() {
        swaps++;
    }

    public void incrementAssignments() {
        assignments++;
    }

    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public void exitRecursion() {
        currentDepth--;
    }

    public void startTiming() {
        startTime = System.nanoTime();
    }

    public void stopTiming() {
        endTime = System.nanoTime();
    }

    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getAssignments() { return assignments; }
    public int getMaxDepth() { return maxDepth; }
    public long getExecutionTimeNanos() { return endTime - startTime; }
    public double getExecutionTimeMillis() { return (endTime - startTime) / 1_000_000.0; }
}
