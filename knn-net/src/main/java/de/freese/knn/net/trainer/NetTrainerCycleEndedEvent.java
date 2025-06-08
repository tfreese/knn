// Created: 06.06.2008
package de.freese.knn.net.trainer;

import java.io.Serial;
import java.util.EventObject;

/**
 * Event, wenn ein Lernzyklus beendet ist.
 *
 * @author Thomas Freese
 */
public class NetTrainerCycleEndedEvent extends EventObject {
    @Serial
    private static final long serialVersionUID = -1343301845155055735L;

    private final double error;
    private final int iteration;
    private final double momentum;
    private final double teachFactor;

    public NetTrainerCycleEndedEvent(final Object source, final int iteration, final double error, final double teachFactor, final double momentum) {
        super(source);

        this.iteration = iteration;
        this.error = error;
        this.teachFactor = teachFactor;
        this.momentum = momentum;
    }

    /**
     * Netzfehler.
     */
    public double getError() {
        return error;
    }

    /**
     * Lernzyklus.
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * Aktuelles Momentum.
     */
    public double getMomentum() {
        return momentum;
    }

    /**
     * Aktuelle Lernrate.
     */
    public double getTeachFactor() {
        return teachFactor;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Iteration = ").append(getIteration());
        sb.append(", Lernrate = ").append(getTeachFactor());
        sb.append(", Momentum = ").append(getMomentum());
        sb.append(", Netzfehler = ").append(String.format("%7.3f %%", getError() * 100));

        return sb.toString();
    }
}
