package de.assemblersim.application.display;

public class Pixel {
	
	private boolean activated;
	private int x;
	private int y;

	public Pixel(boolean activated, int x, int y) {
		this.activated = activated;
		this.x = x;
		this.y = y;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}