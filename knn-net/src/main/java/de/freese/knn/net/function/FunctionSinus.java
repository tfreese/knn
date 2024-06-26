// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sinus Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionSinus implements Function {
    @Override
    public double calculate(final double value) {
        return Math.sin(value);
    }
}
