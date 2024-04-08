// Created: 17.04.2008
package de.freese.knn.buttons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.freese.knn.net.NeuralNet;

/**
 * Panel der Buttons
 *
 * @author Thomas Freese
 */
public class KnnButtonPanel extends JPanel implements ActionListener {
    public static final int MAT_HEIGHT = 9;
    public static final int MAT_WIDTH = 6;
    @Serial
    private static final long serialVersionUID = 1L;

    private final JLabel[] labelsOutput = new JLabel[10];
    private final transient NeuralNet neuralNet;
    private final JToggleButton[] toggleButtons = new JToggleButton[MAT_HEIGHT * MAT_WIDTH];

    private JLabel labelRecognized;

    public KnnButtonPanel(final NeuralNet neuralNet) {
        super();

        this.neuralNet = neuralNet;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final double[] outputVector = this.neuralNet.getOutput(getInputVector());

        double output = Double.MIN_VALUE;
        double value = Double.NaN;

        for (int i = 0; i < outputVector.length; i++) {
            final double percent = outputVector[i] * 100D;

            this.labelsOutput[i].setText(String.format("%d: %6.3f %%", i, percent));

            if (percent > 80D) {
                this.labelsOutput[i].setForeground(Color.RED);
            }
            else if (percent > 50D) {
                this.labelsOutput[i].setForeground(Color.BLUE);
            }
            else {
                this.labelsOutput[i].setForeground(Color.BLACK);
            }

            if (outputVector[i] > output) {
                output = outputVector[i];
                value = i;
            }
        }

        this.labelRecognized.setText("Erkannt als: " + value);
    }

    public KnnButtonPanel initGui() {
        final JPanel buttonPanel = createButtonPanel(this);
        final JPanel outputPanel = createOutputPanel();
        this.labelRecognized = new JLabel("Erkannt als: ");

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.EAST);
        add(this.labelRecognized, BorderLayout.SOUTH);

        return this;
    }

    private JPanel createButtonPanel(final ActionListener actionListener) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(MAT_HEIGHT, MAT_WIDTH));
        panel.setMinimumSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));
        panel.setMaximumSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));
        panel.setPreferredSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));

        int i = 0;

        for (int x = 0; x < MAT_WIDTH; x++) {
            for (int y = 0; y < MAT_HEIGHT; y++, i++) {
                final JToggleButton button = new JToggleButton(String.valueOf(i));
                button.setBackground(Color.LIGHT_GRAY);
                button.setPreferredSize(new Dimension(50, 50));
                button.addActionListener(actionListener);

                panel.add(button);
                this.toggleButtons[i] = button;
            }
        }

        return panel;
    }

    private JPanel createOutputPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 1));
        panel.add(new JLabel("Output-Neuronen"));

        for (int i = 0; i < this.labelsOutput.length; i++) {
            this.labelsOutput[i] = new JLabel(String.valueOf(i));
            panel.add(this.labelsOutput[i]);
        }

        return panel;
    }

    /**
     * Eingangsvektor des NeuralenNetzes.
     */
    private double[] getInputVector() {
        final double[] result = new double[this.toggleButtons.length];

        for (int i = 0; i < this.toggleButtons.length; i++) {
            result[i] = this.toggleButtons[i].isSelected() ? 1.0D : 0.0D;
        }

        return result;
    }
}
