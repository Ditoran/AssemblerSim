package de.assemblersim.application;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Splash {

	JFrame frame;

	public Splash() {
		
		frame = new JFrame();
		frame.setSize(551, 168);
		frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);

		Image icon = new ImageIcon(this.getClass().getResource(
				"/icons/icon.png")).getImage();
		frame.setIconImage(icon);

		Icon splashImage;
		DateFormat dateFormat = new SimpleDateFormat("MMdd");
		Date date = new Date();
		
		int day = Integer.parseInt(dateFormat.format(date));
		Date advent = getFirstAdvent();
		if (day >= 1029 && day <= 1101) {
			// Halloween
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_halloween.jpg"));
		} else if (day >= 1127 && day < 1204) {
			// 1. Advent
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_1_advent.jpg"));
		} else if (day >= 1204 && day < 1211) {
			// 2. Advent
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_2_advent.jpg"));
		} else if (day >= 1211 && day < 1218) {
			// 3. Advent
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_3_advent.jpg"));
		} else if (day >= 1218 && day < 1224) {
			// 4. Advent
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_4_advent.jpg"));
		} else if (day >= 1224 && day < 1227) {
			// Weihnachten
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_christmas.jpg"));
		} else if (day == 1231 || day == 0101) {
			// Silvester
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash_silvester.jpg"));
		} else {
			splashImage = new ImageIcon(this.getClass().getResource(
					"/images/splash.jpg"));
		}

		JLabel image = new JLabel();
		image.setBounds(0, 0, 551, 168);
		image.setIcon(splashImage);
		frame.add(image);
		frame.setVisible(true);
		new AssemblerSim("AssemblerSim");
		frame.setVisible(false);

	}

	private Date getFirstAdvent(){
		Calendar cal = new GregorianCalendar();
		System.out.println("heute: " + cal.get(Calendar.DATE));
		cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 24);
		while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			cal.add(Calendar.DAY_OF_WEEK, -1);
		cal.add(Calendar.DAY_OF_WEEK, -21);
		return cal.getTime();
	}
	
	public static void main(String[] args) {
		new Splash();
	}

}
