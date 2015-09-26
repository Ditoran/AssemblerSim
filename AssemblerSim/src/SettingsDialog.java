import java.awt.Container;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;
	private JButton jButtonSave = new JButton();
	private JButton jButtonAbort = new JButton();
	private JLabel jLabelUseCleaner = new JLabel();
	private JLabel jLabelSetDesign = new JLabel();
	private JLabel jLabelSetPrintHeader = new JLabel();
	private JLabel helpSetPrintHeader = new JLabel();
	private JLabel jLabelSetTrayIcon = new JLabel();
	private JLabel helpSetTrayIcon = new JLabel();
	private JCheckBox checkboxUseCleaner = new JCheckBox();
	private JCheckBox checkboxSetPrintHeader = new JCheckBox();
	private JCheckBox checkboxSetTrayIcon = new JCheckBox();
	private JComboBox<String> themeBox = new JComboBox<String>(new String[]{"Nimbus","SystemLookAndFeel"});
	
	AssemblerSim assemblerSim;
	
	public SettingsDialog(AssemblerSim assemblerSim) {
		this.assemblerSim = assemblerSim;
		int width = 350;
		int height = 225;
		setSize(width, height);
		setResizable(false);
		setLocationRelativeTo(null);
		Container cp = getContentPane();
		cp.setLayout(null);
		setTitle("Einstellungen");
		jButtonSave.setBounds(64, 140, 100, 30);
		jButtonSave.setText("Speichern");
		jButtonSave.setMargin(new Insets(2, 2, 2, 2));
		jButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Save settings

				try {
					Properties props = new Properties();
					props.load(new FileInputStream("res/settings.ini"));
					props.setProperty("codecleaner", Boolean.toString(checkboxUseCleaner.isSelected()));
					props.setProperty("trayIcon", Boolean.toString(checkboxSetTrayIcon.isSelected()));
					props.setProperty("printHeader", Boolean.toString(checkboxSetPrintHeader.isSelected()));
					props.setProperty("theme", themeBox.getSelectedItem().toString());
					FileWriter fr = new FileWriter("res/settings.ini");
					BufferedWriter bw = new BufferedWriter(fr);
					props.store(bw, null);					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (JOptionPane.showConfirmDialog(null,
						"Die Einstellungen werden erst nach einem Neustart\n"+
				"des Programmes wirksam. Jetzt neustarten?\n\n"
				+ "Der bisherige Programmcode bleibt erhalten.",
						"", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					restart();
				}
				dispose();
			}
		});
		cp.add(jButtonSave);
		jButtonAbort.setBounds(184, 140, 100, 30);
		jButtonAbort.setText("Abbrechen");
		jButtonAbort.setMargin(new Insets(2, 2, 2, 2));
		jButtonAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		cp.add(jButtonAbort);

		jLabelUseCleaner.setBounds(40, 15, 110, 20);
		jLabelUseCleaner.setText("Code Cleaner");
		cp.add(jLabelUseCleaner);
		checkboxUseCleaner.setBounds(160, 15, 120, 20);
		checkboxUseCleaner.setText("(empfohlen)");
		cp.add(checkboxUseCleaner);
		
		jLabelSetTrayIcon.setBounds(40, 40, 110, 20);
		jLabelSetTrayIcon.setText("TrayIcon");
		cp.add(jLabelSetTrayIcon);
		checkboxSetTrayIcon.setBounds(160, 40, 20, 20);
		cp.add(checkboxSetTrayIcon);
		helpSetPrintHeader.setBounds(185, 65, 20, 20);
		helpSetPrintHeader.setIcon(new ImageIcon(this.getClass().getResource(
				"/icons/help.gif")));
		helpSetPrintHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpSetPrintHeader.setToolTipText("<html><div align=\"center\">Diese Option erstellt<br />"
				+ "auf den Ausdrucken<br />"
				+ "automatisch Kopf- und<br />"
				+ "Fu\u00dfzeilen mit<br />"
				+ "Seitennummerierung.</div></html>");
		cp.add(helpSetPrintHeader);
		
		jLabelSetPrintHeader.setBounds(40, 65, 110, 20);
		jLabelSetPrintHeader.setText("Drucke Header");
		cp.add(jLabelSetPrintHeader);
		checkboxSetPrintHeader.setBounds(160, 65, 20, 20);
		cp.add(checkboxSetPrintHeader);
		helpSetTrayIcon.setBounds(185, 40, 20, 20);
		helpSetTrayIcon.setIcon(new ImageIcon(this.getClass().getResource(
				"/icons/help.gif")));
		helpSetTrayIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpSetTrayIcon.setToolTipText("<html><div align=\"center\">Das TrayIcon wird in der<br />"
				+ "Taskleiste links neben<br />"
				+ "der Uhr dargestellt.<br />"
				+ "Verschiedene Aktionen sind<br />"
				+ "von dort aus steuerbar.</div></html>");
		cp.add(helpSetPrintHeader);
		cp.add(helpSetTrayIcon);
		
		jLabelSetDesign.setBounds(40, 90, 110, 20);
		jLabelSetDesign.setText("Design");
		cp.add(jLabelSetDesign);
		themeBox.setBounds(160, 90, 150, 20);
		themeBox.setToolTipText("Neustart des Programms erforderlich");
		cp.add(themeBox);

		// Load Settings
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("res/settings.ini"));
			
			checkboxUseCleaner.setSelected(Boolean.parseBoolean(props.getProperty("codecleaner")));
			checkboxSetTrayIcon.setSelected(Boolean.parseBoolean(props.getProperty("trayIcon")));
			checkboxSetPrintHeader.setSelected(Boolean.parseBoolean(props.getProperty("printHeader")));
			themeBox.setSelectedItem(props.getProperty("theme"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		setModal(true);
		setVisible(true);
	}
	
	public void restart(){
		File f = new File(".temprestart");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(assemblerSim.codebox.getText());
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String javaBin = System.getProperty("java.home") + "/bin/java";  
	    File jarFile;  
	    try{  
	        jarFile = new File  
	        (getClass().getProtectionDomain()  
	        .getCodeSource().getLocation().toURI());  
	    } catch(Exception e) {  
	         return;
	    }  
	  
	    /* is it a jar file? */  
	    if ( !jarFile.getName().endsWith(".jar") )  
	    return;   //no, it's a .class probably  
	  
	    String  toExec[] = new String[] { javaBin, "-jar", jarFile.getPath() };  
	    try{  
	        @SuppressWarnings("unused")
			Process p = Runtime.getRuntime().exec( toExec );  
	    } catch(Exception e) {  
	        e.printStackTrace();  
	        return;  
	    }  
	  
	    System.exit(0);
	}

}