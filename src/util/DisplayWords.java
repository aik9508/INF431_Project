package util;

public class DisplayWords implements Runnable {
	private Display display;

	public DisplayWords(Display display) {
		this.display = display;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String word = display.incorrectWordsList.take();
				System.out.println(word);
				display.incorrectWords.append("\n" + word);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
