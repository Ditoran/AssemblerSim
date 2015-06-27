package display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Display extends JComponent {

	Pixel[][][] display = new Pixel[32][8][5];
	private int cursorPos = 0;
	private boolean initialized = false;
	
	private boolean[][][] displayram = new boolean[8][8][5];

	private boolean[][][] charTable = new boolean[256][8][5];

	public Display() {
		init();
		int position_x = 4;
		int position_y = 4;
		for (int i = 0; i < display.length; i++) {
			for (int j = 0; j < display[0].length; j++) {
				for (int k = 0; k < display[0][0].length; k++) {

					display[i][j][k] = new Pixel(false, position_x, position_y);
					position_x += 3;

				}
				position_x -= 15;
				position_y += 3;
			}
			position_x += 18;
			position_y -= 24;
			if (i == 15) {
				position_x -= 288;
				position_y += 27;
			}
		}
	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(120, 166, 72));
		g.fillRect(0, 0, 400, 400);

		for (int i = 0; i < display.length; i++) {
			for (int j = 0; j < display[0].length; j++) {
				for (int k = 0; k < display[0][0].length; k++) {

					if (display[i][j][k].isActivated()) {
						g.setColor(new Color(29, 70, 51));
					} else {
						g.setColor(new Color(91, 159, 73));
					}
					g.fillRect(display[i][j][k].getX(),	display[i][j][k].getY(), 2, 2);

				}
			}
		}
		//this.repaint();

	}
	
	public void init() {

		BufferedImage image;
		URL file = this.getClass().getClassLoader().getResource("images/pixeldata.bmp");
		try {
			image = ImageIO.read(file);
			for (int i = 0; i < 256; i++) {
				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 5; x++) {
						if (image.getRGB(x + 5 * i, y) < -1) {
							charTable[i][y][x] = true;
						} else {
							charTable[i][y][x] = false;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void setCursorPos(int pos) {
		if (pos>15) {
			pos = pos-48;
		}
		cursorPos = pos;
	}
	
	public void setChar(int chr) {
		if (chr < 8) {
			fromRamtoDisplay(chr);
			return;
		}
		int c = chr;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				display[cursorPos][i][j].setActivated(charTable[c][i][j]);
			}
		}
		repaint();
	}
	
	private void fromRamtoDisplay(int chr) {
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				if (displayram[chr][i][j]) {
					display[cursorPos][i][j].setActivated(true);
				} else {
					display[cursorPos][i][j].setActivated(false);
				}
			}
		}
		repaint();
	}

	public void setChar(char chr) {
		int c = (int) chr;
		setChar(c);
		/*for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				display[cursorPos][i][j].setActivated(charTable[c][i][j]);
			}
		}
		repaint();*/
	}
	
	public void setNumber(int chr) {
		int c = chr+48;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				display[cursorPos][i][j].setActivated(charTable[c][i][j]);
			}
		}
		repaint();
	}
	
	public void setText(String[][] source, String dptr) {
		int tempjump = 0;
		String marke = dptr;
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		tempjump++;
		
		String text = source[tempjump][1];
		
		for (int i = 0; i < text.length(); i++) {
				setChar(text.charAt(i));
				cursorPos++;
		}
		repaint();
	}
	
	public void setTextLine1(String[][] source, String dptr) {
		cursorPos = 0;
		int tempjump = 0;
		String marke = dptr;
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		tempjump++;
		
		String text = source[tempjump][1];
		
		for (int i = 0; i < text.length(); i++) {
				setChar(text.charAt(i));
				cursorPos++;
		}
		repaint();
	}
	
	public void setTextLine2(String[][] source, String dptr) {
		cursorPos = 16;
		int tempjump = 0;
		String marke = dptr;
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		tempjump++;
		
		String text = source[tempjump][1];
		
		for (int i = 0; i < text.length(); i++) {
				setChar(text.charAt(i));
				cursorPos++;
		}
		repaint();
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void deleteLine1() {
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 5; k++) {
					display[i][j][k].setActivated(false);
				}
			}
		}
		repaint();
	}
	
	public void deleteLine2() {
		for (int i = 16; i < 32; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 5; k++) {
					display[i][j][k].setActivated(false);
				}
			}
		}
		repaint();
	}
	
	public void deleteAllLines() {
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 5; k++) {
					display[i][j][k].setActivated(false);
				}
			}
		}
		repaint();
	}

	public void loadToRam(int ramcell, String[][]source, String dptr) {
		int tempjump = 0;
		String marke = dptr;
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		tempjump++;
		
		String text = source[tempjump][1];
		String[] rows = text.replace("b", "").split(",");
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 5; k++) {
					if (rows[j].charAt(k) == '1') {
						displayram[ramcell][j][k] = true;
					} else {
						displayram[ramcell][j][k] = false;
					}
				}
				
			}
		}
	}
}