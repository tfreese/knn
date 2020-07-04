/**
 * 17.04.2008
 */
package de.freese.knn.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Panel der Buttons
 * 
 * @author Thomas Freese
 */
public class KnnButtonPanel extends JPanel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public static final int MAT_HEIGHT = 9;

	/**
	 * 
	 */
	public static final int MAT_WIDTH = 6;

	/**
	 * 
	 */
	private JToggleButton[] toggleButtons = new JToggleButton[MAT_HEIGHT * MAT_WIDTH];

	/**
	 * Creates a new {@link KnnButtonPanel} object.
	 * 
	 * @param actionListener {@link ActionListener}
	 */
	public KnnButtonPanel(final ActionListener actionListener)
	{
		super();

		setLayout(new GridLayout(MAT_HEIGHT, MAT_WIDTH));
		setMinimumSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));
		setMaximumSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));
		setPreferredSize(new Dimension(MAT_WIDTH * 50, MAT_HEIGHT * 50));

		int i = 0;

		for (int x = 0; x < MAT_WIDTH; x++)
		{
			for (int y = 0; y < MAT_HEIGHT; y++, i++)
			{
				JToggleButton button = new JToggleButton(String.valueOf(i));
				button.setBackground(Color.BLUE);
				button.setPreferredSize(new Dimension(50, 50));
				button.addActionListener(actionListener);

				add(button);
				this.toggleButtons[i] = button;
			}
		}
	}

	/**
	 * Eingangsvektor des NeuralenNetzes.
	 * 
	 * @return double[]
	 */
	public double[] getInputVector()
	{
		double[] result = new double[this.toggleButtons.length];

		for (int i = 0; i < this.toggleButtons.length; i++)
		{
			result[i] = this.toggleButtons[i].isSelected() ? 1.0D : 0.0D;
		}

		return result;
	}
}
