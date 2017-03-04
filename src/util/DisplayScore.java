package util;

public class DisplayScore implements Runnable {
	private Display display;

	public DisplayScore(Display display) {
		this.display = display;
	}

	@Override
	public void run() {
		while (true) {
			display.score.setText(display.currentScore + "");
		}
	}
}