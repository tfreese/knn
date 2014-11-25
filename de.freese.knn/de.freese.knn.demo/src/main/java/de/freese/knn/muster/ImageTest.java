/**
 * 12.07.2008
 */
package de.freese.knn.muster;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import de.freese.knn.muster.imageop.ImageData;

/**
 * Testklasse.
 * 
 * @author Thomas Freese
 */
public class ImageTest extends JFrame
{
	/**
	 * CellRenderer für Images.
	 * 
	 * @author Thomas Freese
	 */
	private class ImageTableRenderer extends DefaultTableCellRenderer
	{
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		private Image image = null;

		/**
		 * Creates a new {@link ImageTableRenderer} object.
		 */
		public ImageTableRenderer()
		{
			super();
		}

		/**
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
														final int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (this.image != null)
			{
				int imageHeight = this.image.getHeight(null);

				if (table.getRowHeight(row) < imageHeight)
				{
					table.setRowHeight(row, imageHeight);
				}
			}

			return this;
		}

		/**
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(final Graphics g)
		{
			// super.paint(g);
			Image image = this.image;

			if (image != null)
			{
				int imageWidth = image.getWidth(null);
				int imageHeight = image.getHeight(null);
				int x = (getWidth() - imageWidth) / 2;
				int y = (getHeight() - imageHeight) / 2;

				g.drawImage(image, x, y, imageWidth, imageHeight, this);
			}
		}

		/**
		 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
		 */
		@Override
		protected void setValue(final Object value)
		{
			this.image = (value instanceof Image) ? (Image) value : null;
		}
	}

	/**
	 * TableModel.
	 * 
	 * @author Thomas Freese
	 */
	private class TableModel extends AbstractTableModel
	{
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		private List<ImageData> dataList = null;

		/**
		 * Creates a new {@link TableModel} object.
		 */
		public TableModel()
		{
			super();

			this.dataList = new ArrayList<>();
		}

		/**
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(final int columnIndex)
		{
			return Image.class;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return 3;
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			return this.dataList.size();
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			ImageData imageData = this.dataList.get(rowIndex);

			Object value = null;

			switch (columnIndex)
			{
				case 0:
					value = imageData.getSourceImage();

					break;

				case 1:
					value = imageData.getEdgeImage();

					break;

				case 2:
					value = imageData.getBlackWhiteImage();

					break;

				default:
					break;
			}

			return value;
		}

		/**
		 * @param objects {@link List}
		 */
		public void setList(final List<ImageData> objects)
		{
			this.dataList = objects;
			fireTableDataChanged();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		ImageTest imageTest = new ImageTest();
		imageTest.setVisible(true);
	}

	/**
	 * Creates a new {@link ImageTest} object.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	public ImageTest() throws Exception
	{
		super();

		init();
	}

	/**
	 * @return {@link JTable}
	 * @throws Exception Falls was schief geht.
	 */
	private JTable createTable() throws Exception
	{
		JTable table = new JTable();

		ImagePixelTrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
		TableModel tableModel = new TableModel();
		tableModel.setList(trainingInputSource.getImageData());

		table.setModel(tableModel);

		table.setDefaultRenderer(Image.class, new ImageTableRenderer());

		return table;
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	private void init() throws Exception
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Dimension dimension = new Dimension(1000, 1000);
		setSize(dimension);
		getContentPane().setLayout(new BorderLayout());

		JScrollPane scrollPane = new JScrollPane(createTable());

		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
}
