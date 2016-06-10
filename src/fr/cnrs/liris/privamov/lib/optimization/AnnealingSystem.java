package fr.cnrs.liris.privamov.lib.optimization;

/**
 * A system being evaluated through simulated annealing.
 *
 * @param <T> Type of element to evaluate
 */
public interface AnnealingSystem<T> {
    /**
     * Generate an initial solution. It should include some randomness.
     */
    T initialSolution();

    /**
     * Return the cost of a given solution. It should be deterministic.
     *
     * @param solution A solution to evaluate
     */
    double cost(T solution);

    /**
     * Generate a neighboring solution from a given solution. It should include some randomness.
     *
     * @param solution A solution
     */
    T neighbor(T solution);

    /**
     * Return the acceptance probability.
     *
     * @param oldCost Cost of the actual solution
     * @param newCost Cost of the candidate solution
     * @param temp    Current temperature
     * @return A probability for the new solution to be accepted (hence in [0,1])
     */
    default double acceptanceProbability(double oldCost, double newCost, double temp) {
        if (oldCost == 0) {
            return 0;
        } else if (newCost < oldCost) {
            return 1;
        } else {
            return 1d / (1 + Math.exp((newCost - oldCost) / temp));
        }
    }
}