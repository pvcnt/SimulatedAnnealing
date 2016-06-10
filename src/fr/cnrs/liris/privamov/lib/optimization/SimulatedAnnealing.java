package fr.cnrs.liris.privamov.lib.optimization;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Implementation of simulated annealing. The goal is to approximate the global optimum of a given
 * function, without exploring the entire search space.
 *
 * @param <T> Type of evaluated element
 * @link http://katrinaeg.com/simulated-annealing.html
 * @link https://en.wikipedia.org/wiki/Simulated_annealing
 */
class SimulatedAnnealing<T> {
    private final AnnealingSystem<T> system;
    private final CoolingSchedule coolingSchedule;
    private final int iters;
    private final Random random = new Random();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor.
     *
     * @param system          System to evaluate
     * @param coolingSchedule Cooling schedule
     * @param iters           Number of iterations at each step
     */
    public SimulatedAnnealing(AnnealingSystem<T> system, CoolingSchedule coolingSchedule, int iters) {
        if (iters <= 0) {
            throw new IllegalArgumentException("Number of iterations per step must be stricly positive (got $iters)");
        }
        this.system = system;
        this.coolingSchedule = coolingSchedule;
        this.iters = iters;
    }

    /**
     * Run the optimization.
     *
     * @return The "optimal" solution found with its cost
     */
    public AnnealingResult<T> optimize() {
        // Initial solution is provided by the system (with its associated cost).
        AnnealingResult<T> result = initialSolution();
        if (result.getCost() == 0) {
            // If the cost of the initial solution is null, we can terminate now.
            logger.info(String.format("Accepted solution %s (cost=0, initial)", result.getValue().toString()));
            return result;
        }

        // Initial temperature is provided by the cooling schedule.
        double temp = coolingSchedule.getStart();
        logger.info(String.format(
                "Initial solution %s (cost=%s) at T=%s until %s",
                result.getValue().toString(), result.getCost(), temp, coolingSchedule.getMinimum()));

        // We keep a track of the best solution found so far. That way, if we end up with a worst
        // solution than one we have seen so far, we can return it.
        AnnealingResult<T> bestSoFar = result;
        while (temp > coolingSchedule.getMinimum()) {
            // For each temperature, the simulation will be ran several times.
            for (int i = 0; i < iters; i++) {
                final AnnealingResult<T> newResult = neighbor(result.getValue());
                if (newResult.getCost() == 0) {
                    // If the cost of the new solution is null, we can terminate now.
                    logger.info(String.format(
                            "Accepted solution %s (cost=0) at T=%s, iter=%s",
                            newResult.getValue().toString(), temp, i));
                    return newResult;
                }
                // We compute an acceptance probability for the new solution.
                final double ap = system.acceptanceProbability(result.getCost(), newResult.getCost(), temp);
                if (ap < 0 || ap > 1) {
                    throw new IllegalArgumentException(String.format(
                            "Acceptance probability must be in [0,1] (got %s for oldCost=%s, newCost=%s, T=%s)",
                            ap, result.getCost(), newResult.getCost(), temp
                    ));
                }
                if (ap == 1 || ap >= random.nextDouble()) {
                    logger.info(String.format(
                            "Accepted %s (cost=%s, ap=%s) at T=%s, iter=%s",
                            newResult.getValue().toString(), newResult.getCost(), ap, temp, i));
                    result = newResult;
                    if (result.getCost() < bestSoFar.getCost()) {
                        bestSoFar = result;
                    }
                } else {
                    logger.info(String.format(
                            "Rejected %s (cost=%s, ap=%s) at T=%s, iter=%s",
                            newResult.getValue().toString(), newResult.getCost(), ap, temp, i));
                }
            }
            // We gradually decrease the temperature.
            temp = coolingSchedule.decrease(temp);
            logger.info(String.format("Decreased temperature to %s", temp));
        }
        if (bestSoFar.getCost() < result.getCost()) {
            logger.info(String.format(
                    "Accepted solution %s (cost=%s, best so far)",
                    bestSoFar.getValue().toString(), bestSoFar.getCost()));
            return bestSoFar;
        } else {
            logger.info(String.format("Accepted solution %s (cost=%s)", result.getValue().toString(), result.getCost()));
            return result;
        }
    }

    /**
     * Compute the initial solution and its associated cost.
     */
    private AnnealingResult<T> initialSolution() {
        final T solution = system.initialSolution();
        final double cost = system.cost(solution);
        return new AnnealingResult<>(solution, cost);
    }

    /**
     * Compute a neighbor solution and its associated cost.
     */
    private AnnealingResult<T> neighbor(T solution) {
        final T newSolution = system.neighbor(solution);
        final double cost = system.cost(newSolution);
        return new AnnealingResult<>(newSolution, cost);
    }
}
