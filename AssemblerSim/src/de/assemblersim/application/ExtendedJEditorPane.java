package de.assemblersim.application;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;

@SuppressWarnings("serial")
public class ExtendedJEditorPane extends JEditorPane {
	private Color backgroundColor = Color.WHITE;
	private int highlightedLine = -1;

	public ExtendedJEditorPane() {
		// TODO Auto-generated constructor stub
		super.setBackground(new Color(0, 0, 0, 0));
	}

	public ExtendedJEditorPane(URL initialPage) throws IOException {
		super(initialPage);
		// TODO Auto-generated constructor stub
	}

	public ExtendedJEditorPane(String url) throws IOException {
		super(url);
		// TODO Auto-generated constructor stub
	}

	public ExtendedJEditorPane(String type, String text) {
		super(type, text);
		// TODO Auto-generated constructor stub
	}

	public void setLineHighlight(int line) {
		this.highlightedLine = line;
		this.repaint();
	}

	public void setCaretLine(int line) {
		char[] paneText = this.getText().toCharArray();
		int lineCounter = 0;
		int i = 1;
		for (i=i; lineCounter<line && i<paneText.length; i++) {
			if(paneText[i]=='\n'){
				lineCounter++;
			}
		}
		this.requestFocusInWindow();
		this.setCaretPosition(i);
	}

	@Override
	public void setBackground(Color bg) {
		backgroundColor = bg;
	};

	@Override
	public void paint(Graphics g) {
		// code
		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(this.getX(), (this.highlightedLine - 1) * 17 + 1, this.getX() + this.getWidth(), 18);
		g.setColor(backgroundColor);

		super.paint(g);
	}

}