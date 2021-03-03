package de.assemblersim.application.cloud;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class CloudFileChooser {

	private JDialog dialog;
	private JTable table;

	private boolean isLoadMode;
	private CloudFile selectedFile;

	public CloudFileChooser() {

		this.dialog = new JDialog();
		this.dialog.setModal(true);
		this.dialog.setSize(500, 300);
		this.dialog.setLocationRelativeTo(null);
		this.dialog.getContentPane().setLayout(new BorderLayout());

		this.table = new JTable();
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		JTableHeader th = this.table.getTableHeader();
		TableColumn c = new TableColumn();
		c.setHeaderValue("test");
		th.getColumnModel().addColumn(c);
		TableColumn c2 = new TableColumn();
		c2.setHeaderValue("test2");
		th.getColumnModel().addColumn(c2);
		c2.setMaxWidth(50);
		this.table.setModel(new CloudTableModel(Cloud.getInstance().getCloudFileModel()));
		this.table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					int col = table.columnAtPoint(e.getPoint());
					if (row >= 0 && col >= 0) {
						System.out.println(table.getModel().getValueAt(row, col));
						selectedFile = Cloud.getInstance().getCloudFileModel().getFiles().get(row);
						dialog.dispose();
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);

		this.dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public void showOpenDialog() {
		this.dialog.setVisible(true);
	}

	public void showSaveDialog() {

	}

	public CloudFile getSelectedFile() {
		return this.selectedFile;
	}
}
