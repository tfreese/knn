/**
 * 11.06.2008
 */
package de.freese.knn.net.trainer;

/**
 * Basisklasse des {@link INetTrainerListener}.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractNetTrainerListener implements INetTrainerListener
{
	/**
	 * Welches wievielte Event soll geloggt werden ?
	 */
	private final int logModulo;

	/**
	 * Creates a new {@link AbstractNetTrainerListener} object.
	 */
	public AbstractNetTrainerListener()
	{
		this(1);
	}

	/**
	 * Creates a new {@link AbstractNetTrainerListener} object.
	 * 
	 * @param logModulo int Welches wievielte Event soll geloggt werden ?
	 */
	public AbstractNetTrainerListener(final int logModulo)
	{
		super();

		this.logModulo = logModulo;
	}

	/**
	 * Welches wievielte Event soll geloggt werden ?
	 * 
	 * @return int
	 */
	protected int getLogModulo()
	{
		return this.logModulo;
	}

	/**
	 * @param event {@link NetTrainerCycleEndedEvent}
	 * @return String
	 */
	protected String toString(final NetTrainerCycleEndedEvent event)
	{
		return event.toString();
	}
}
