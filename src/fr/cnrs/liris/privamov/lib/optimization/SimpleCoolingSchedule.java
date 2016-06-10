package fr.cnrs.liris.privamov.lib.optimization;

public class SimpleCoolingSchedule implements CoolingSchedule {
    private double start;
    private double minimum;
    private double coolingRate;

    /**
     * Constructor.
     *
     * @param start       Initial temperature
     * @param minimum     Minimum temperature (simulation stops when it is reached)
     * @param coolingRate Cooling rate (temperature will be multiplied by this factor after each step)
     */
    public SimpleCoolingSchedule(double start, double minimum, double coolingRate) {
        if (start < minimum) {
            throw new IllegalArgumentException("Initial temperature must be greater than minimal temperature");
        }
        if (coolingRate <= 0 || coolingRate > 1) {
            throw new IllegalArgumentException("Cooling rate must be in [0,1] (got $coolingRate)");
        }
        this.start = start;
        this.minimum = minimum;
        this.coolingRate = coolingRate;
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double decrease(double temp) {
        return temp * coolingRate;
    }

    @Override
    public double getMinimum() {
        return minimum;
    }
}
