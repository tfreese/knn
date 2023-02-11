// Created: 12.07.2008
package de.freese.knn.bilderkennung;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import de.freese.knn.bilderkennung.utils.ImageData;

/**
 * Testklasse.
 *
 * @author Thomas Freese
 */
public class TestImagePixelTrainingInputSource extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @author Thomas Freese
     */
    private static class ImageTableRenderer extends DefaultTableCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient Image image;

        /**
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (this.image != null) {
                int imageHeight = this.image.getHeight(null);

                if (table.getRowHeight(row) < imageHeight) {
                    table.setRowHeight(row, imageHeight);
                }
            }

            return this;
        }

        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public void paint(final Graphics g) {
            // super.paint(g);

            if (this.image != null) {
                int imageWidth = this.image.getWidth(null);
                int imageHeight = this.image.getHeight(null);
                int x = (getWidth() - imageWidth) / 2;
                int y = (getHeight() - imageHeight) / 2;

                g.drawImage(this.image, x, y, imageWidth, imageHeight, this);
            }
        }

        /**
         * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
         */
        @Override
        protected void setValue(final Object value) {
            this.image = (value instanceof Image) ? (Image) value : null;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class TableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient final List<ImageData> dataList = new ArrayList<>();

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return Image.class;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return 3;
        }

        /**
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount() {
            return this.dataList.size();
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            ImageData imageData = this.dataList.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> imageData.getSourceImage();
                case 1 -> imageData.getEdgeImage();
                case 2 -> imageData.getBlackWhiteImage();
                default -> null;
            };
        }

        public void setList(final List<ImageData> objects) {
            this.dataList.clear();
            this.dataList.addAll(objects);

            fireTableDataChanged();
        }
    }

    public static void main(final String[] args) throws Exception {
        TestImagePixelTrainingInputSource imageTest = new TestImagePixelTrainingInputSource();
        imageTest.setVisible(true);
    }

    public TestImagePixelTrainingInputSource() throws Exception {
        super();

        init();
    }

    private JTable createTable() throws Exception {
        JTable table = new JTable();

        ImagePixelTrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
        TableModel tableModel = new TableModel();
        tableModel.setList(trainingInputSource.getImageData());

        table.setModel(tableModel);

        table.setDefaultRenderer(Image.class, new ImageTableRenderer());

        return table;
    }

    private void init() throws Exception {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Dimension dimension = new Dimension(1000, 1000);
        setSize(dimension);
        getContentPane().setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(createTable());

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
}
