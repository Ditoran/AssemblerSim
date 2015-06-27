import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CpuSpeedPanel extends JPanel {

    JCheckBox check;
    JLabel label;

    public CpuSpeedPanel() {
	long cpuSpeed = System.nanoTime();
	for (int i = 0; i < 1000000; i++) {

	}
	cpuSpeed = System.nanoTime() - cpuSpeed;
	if (cpuSpeed > 1000000) {

	} else {
	    return;
	}
	setLayout(new BorderLayout());
	setSize(400, 200);
	label = new JLabel();
	label.setBounds(0, 0, 350, 100);
	label.setText("<html><body>Dein Prozessor besitzt leider nicht die Leistung, "
		+ "um AssemblerSim in Echtzeit zu simulieren.<br />"
		+ "Das Programm wird minimal langsamer laufen.<br />"
		+ "Bitte beachte, dass du z.B. bei Timern oder Warteschleifen die Zeiten bis "
		+ "zur Auslösung anpasst.</body></html>");
	add(label, BorderLayout.PAGE_START);
	check = new JCheckBox();
	check.setBounds(100, 170, 160, 20);
	check.setText("Nicht mehr zeigen");
	add(check, BorderLayout.EAST);
	check.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		try {
		    Properties props = new Properties();
		    props.load(new FileInputStream("res/settings.ini"));
		    props.setProperty("cpucheckoff",
			    Boolean.toString(check.isSelected()));
		    FileWriter fr = new FileWriter("res/settings.ini");
		    BufferedWriter bw = new BufferedWriter(fr);
		    props.store(bw, null);
		} catch (Exception e) {
		    // TODO: handle exception
		}
	    }
	});
	setVisible(true);

    }

}
