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
    private static final class ImageTableRenderer extends DefaultTableCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        private transient Image image;

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (image != null) {
                final int imageHeight = image.getHeight(null);

                if (table.getRowHeight(row) < imageHeight) {
                    table.setRowHeight(row, imageHeight);
                }
            }

            return this;
        }

        @Override
        public void paint(final Graphics g) {
            // super.paint(g);

            if (image != null) {
                final int imageWidth = image.getWidth(null);
                final int imageHeight = image.getHeight(null);
                final int x = (getWidth() - imageWidth) / 2;
                final int y = (getHeight() - imageHeight) / 2;

                g.drawImage(image, x, y, imageWidth, imageHeight, this);
            }
        }

        @Override
        protected void setValue(final Object value) {
            image = (value instanceof Image) ? (Image) value : null;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class TableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 1L;

        private final transient List<ImageData> dataList = new ArrayList<>();

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return Image.class;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getRowCount() {
            return dataList.size();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final ImageData imageData = dataList.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> imageData.getSourceImage();
                case 1 -> imageData.getEdgeImage();
                case 2 -> imageData.getBlackWhiteImage();
                default -> null;
            };
        }

        public void setList(final List<ImageData> objects) {
            dataList.clear();
            dataList.addAll(objects);

            fireTableDataChanged();
        }
    }

    static void main() throws Exception {
        final TestImagePixelTrainingInputSource imageTest = new TestImagePixelTrainingInputSource();
        imageTest.setVisible(true);
    }

    public TestImagePixelTrainingInputSource() throws Exception {
        super();

        init();
    }

    private JTable createTable() throws Exception {
        final JTable table = new JTable();

        final ImagePixelTrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
        final TableModel tableModel = new TableModel();
        tableModel.setList(trainingInputSource.getImageData());

        table.setModel(tableModel);

        table.setDefaultRenderer(Image.class, new ImageTableRenderer());

        return table;
    }

    private void init() throws Exception {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final Dimension dimension = new Dimension(1000, 1000);
        setSize(dimension);
        getContentPane().setLayout(new BorderLayout());

        final JScrollPane scrollPane = new JScrollPane(createTable());

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
}
