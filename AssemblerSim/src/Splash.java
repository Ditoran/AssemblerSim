import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	if (day >= 1029 && day <= 1101) {
	    // Halloween
	    splashImage = new ImageIcon(this.getClass().getResource(
		    "/images/splash_halloween.jpg"));
	} else if (day >= 1130 && day < 1207) {
	    // 1. Advent
	    splashImage = new ImageIcon(this.getClass().getResource(
		    "/images/splash_1_advent.jpg"));
	} else if (day >= 1207 && day < 1214) {
	    // 2. Advent
	    splashImage = new ImageIcon(this.getClass().getResource(
		    "/images/splash_2_advent.jpg"));
	} else if (day >= 1214 && day < 1221) {
	    // 3. Advent
	    splashImage = new ImageIcon(this.getClass().getResource(
		    "/images/splash_3_advent.jpg"));
	} else if (day >= 1221 && day < 1224) {
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

    public static void main(String[] args) {
	new Splash();
    }

}
