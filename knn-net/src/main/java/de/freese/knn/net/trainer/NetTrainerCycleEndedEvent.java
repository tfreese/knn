/**
 * 06.06.2008
 */
package de.freese.knn.net.trainer;

import java.util.EventObject;

/**
 * Event, wenn ein Lernzyklus beendet ist.
 *
 * @author Thomas Freese
 */
public class NetTrainerCycleEndedEvent extends EventObject
{
    /**
     *
     */
    private static final long serialVersionUID = -1343301845155055735L;

    /**
     *
     */
    private final double error;

    /**
     *
     */
    private final int iteration;

    /**
     *
     */
    private final double momentum;

    /**
     *
     */
    private final double teachFactor;

    /**
     * Creates a new {@link NetTrainerCycleEndedEvent} object.
     *
     * @param source Object
     * @param iteration int
     * @param error double
     * @param teachFactor double
     * @param momentum double
     */
    public NetTrainerCycleEndedEvent(final Object source, final int iteration, final double error, final double teachFactor, final double momentum)
    {
        super(source);

        this.iteration = iteration;
        this.error = error;
        this.teachFactor = teachFactor;
        this.momentum = momentum;
    }

    /**
     * Netzfehler.
     *
     * @return double
     */
    public double getError()
    {
        return this.error;
    }

    /**
     * Lernzyklus.
     *
     * @return int
     */
    public int getIteration()
    {
        return this.iteration;
    }

    /**
     * Aktuelles Momentum.
     *
     * @return double
     */
    public double getMomentum()
    {
        return this.momentum;
    }

    /**
     * Aktuelle Lernrate.
     *
     * @return double
     */
    public double getTeachFactor()
    {
        return this.teachFactor;
    }

    /**
     * @see java.util.EventObject#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Iteration = ").append(getIteration());
        sb.append(", Lernrate = ").append(getTeachFactor());
        sb.append(", Momentum = ").append(getMomentum());
        sb.append(", Netzfehler = ").append(String.format("%7.3f %%", getError() * 100));

        return sb.toString();
    }
}
