package fr.cnrs.liris.privamov.lib.optimization;

/**
 * Result of a simulated annealing.
 *
 * @param <T></T> Type of solution
 */
public class AnnealingResult<T> implements Comparable<AnnealingResult<T>> {
    private final T value;
    private final double cost;

    /**
     * Constructor.
     *
     * @param value The optimal solution found
     * @param cost  Cost of this solution
     */
    public AnnealingResult(T value, double cost) {
        this.value = value;
        this.cost = cost;
    }

    public T getValue() {
        return value;
    }

    public double getCost() {
        return cost;
    }


    @Override
    public int compareTo(AnnealingResult<T> that) {
        if (cost < that.cost) {
            return -1;
        } else if (cost > that.cost) {
            return 1;
        } else {
            return 0;
        }
    }
}
