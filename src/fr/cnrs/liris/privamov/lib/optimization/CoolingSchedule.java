package fr.cnrs.liris.privamov.lib.optimization;

/**
 * A cooling schedule manages the temperature during a simulated annealing.
 */
public interface CoolingSchedule {
    /**
     * Return the initial temperature.
     */
    double getStart();

    /**
     * Minimum temperature under which the simulation will stop.
     */
    double getMinimum();

    /**
     * Decrease the temperature. If it goes below the minimum temperature, simulation will stop.
     *
     * @param temp Actual temperature
     * @return New temperature
     */
    double decrease(double temp);
}