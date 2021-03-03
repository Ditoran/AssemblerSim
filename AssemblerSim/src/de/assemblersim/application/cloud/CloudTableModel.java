package de.assemblersim.application.cloud;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

public class CloudTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1008505373441519122L;

	CloudFileModel fileModel;

	public CloudTableModel(CloudFileModel fileModel) {
		this.fileModel = fileModel;
	}

	@Override
	public int getRowCount() {
		return fileModel.getFiles().size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return Arrays.asList("Datei", "\u00c4nderungsdatum").toArray()[column].toString();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return fileModel.getFiles().get(rowIndex).getTitle();
		} else {
			Date date = new Date();
			date.setTime(Long.parseLong(fileModel.getFiles().get(rowIndex).getModificationDate()) * 1000);
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			return df.format(date);
		}
	}

}