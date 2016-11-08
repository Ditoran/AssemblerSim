package de.assemblersim.application;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

public class Donation extends JDialog {

    private static final long serialVersionUID = 4732494351145169728L;

    JToggleButton paypal;
    ButtonGroup group;

    JRadioButton value_1;
    JRadioButton value_2;
    JRadioButton value_3;

    public Donation() {
	
	setSize(382, 276);
	setResizable(false);
	setTitle("Spenden");
	setLocationRelativeTo(null);
	Container cp = getContentPane();
	cp.setLayout(null);

	JLabel lblVielenDankDass = new JLabel(
		"<html><center>Vielen Dank, dass Du AssemblerSim unterst\u00fctzen m\u00f6chtest.<br />Alle Spenden dienen zur Finanzierung der Website.<br /><br />Bitte w\u00e4hle zun\u00e4chst einen Zahungsanbieter aus:</center></html>");
	lblVielenDankDass.setBounds(10, 11, 356, 78);
	getContentPane().add(lblVielenDankDass);

	value_1 = new JRadioButton("1\u20AC");
	value_1.setBounds(99, 179, 50, 23);
	value_1.setFocusPainted(false);
	getContentPane().add(value_1);

	value_2 = new JRadioButton("2\u20AC");
	value_2.setBounds(151, 179, 50, 23);
	value_2.setFocusPainted(false);
	getContentPane().add(value_2);

	value_3 = new JRadioButton("3\u20AC");
	value_3.setBounds(203, 179, 50, 23);
	value_3.setFocusPainted(false);
	getContentPane().add(value_3);

	group = new ButtonGroup();
	group.add(value_1);
	group.add(value_2);
	group.add(value_3);


	paypal = new JToggleButton("");
	paypal.setSelectedIcon(new ImageIcon(Donation.class
		.getResource("/icons/paypal.png")));
	paypal.setRolloverIcon(new ImageIcon(Donation.class
		.getResource("/icons/paypal.png")));
	paypal.setIcon(new ImageIcon(Donation.class
		.getResource("/icons/paypal_grey.png")));
	paypal.setBounds(161, 100, 60, 60);
	getContentPane().add(paypal);
	paypal.setToolTipText("Paypal");
	paypal.setBorderPainted(false);
	paypal.setContentAreaFilled(false);
	paypal.setFocusPainted(false);
	paypal.setOpaque(false);
	paypal.setSelected(true);
	
	JButton donate = new JButton("Spenden");
	donate.setBounds(259, 179, 91, 23);
	getContentPane().add(donate);
	donate.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
			String url = "";
			if (paypal.isSelected()) {
			    if (value_1.isSelected()) {
				url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=X7P7JF4T6AYLQ";
			    } else if (value_2.isSelected()) {
				url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UXSJZBRPAWYU6";
			    } else if (value_3.isSelected()) {
				url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7P4U7GFZ4GBMS";
			    }
			}
			if (url.length() > 0) {
			    try {
				Desktop.getDesktop().browse(new URI(url));
			    } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    } catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    }
			}
	    }
	});

	JLabel lblIchMchte = new JLabel("Ich m\u00f6chte");
	lblIchMchte.setBounds(23, 183, 70, 14);
	getContentPane().add(lblIchMchte);

	setModal(true);
	setVisible(true);
    }
}