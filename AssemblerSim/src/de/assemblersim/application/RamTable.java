package de.assemblersim.application;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class RamTable extends JTable{

	String[][] ramdata = new String[][] { { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
		{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
		{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
		{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
		{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" } };
	
	@SuppressWarnings("serial")
	public RamTable() {
		this.setModel(new DefaultTableModel(){
			@Override
			public String getColumnName(int index) {
			    return "";
			}
			@Override
			public int getColumnCount() {
		        return ramdata[0].length;
		    }
			@Override
		    public int getRowCount() {
		        return ramdata.length;
		    }
			@Override
		    public Object getValueAt(int r, int c) {
				return ramdata[r][c];
		    }
			@Override
			public void setValueAt(Object value, int r, int c){
				ramdata[r][c] = value.toString();
			}
		});
	}
	
}
