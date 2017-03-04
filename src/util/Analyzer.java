package util;

import java.util.concurrent.LinkedBlockingQueue;

public class Analyzer implements Runnable {
	private LinkedBlockingQueue<String> wordList;
	private StringBuffer textBuffer;
	private Display display = null;
	private static final char[] NONSEPARATOR = { ',', ';', ':', '\"' };
	private static final char[] CONNECTOR = { '-', '_', '+', '\\', '/' };
	private String lastTakenWord = ".";
	private boolean realSentence = false;

	public Analyzer() {
		this.wordList = new LinkedBlockingQueue<>();
		this.textBuffer = new StringBuffer();
	}

	public void deleteCharacter() {
		if (textBuffer.length() > 0) {
			textBuffer.deleteCharAt(textBuffer.length() - 1);
		}
	}

	public void putCharacter(char c) throws InterruptedException {
		if (Character.isSpaceChar(c)) {
			if (textBuffer.length() > 0) {
				wordList.put(textBuffer.toString());
				textBuffer.setLength(0);
			}
		} else if (!Character.isAlphabetic(c) && !Character.isDigit(c) && !isConnector(c) && c != '\'') {
			if (textBuffer.length() > 0) {
				wordList.put(textBuffer.toString());
				textBuffer.setLength(0);
			}
			wordList.put(c + "");
		} else {
			textBuffer.append(c);
		}
	}

	public void connectTo(Display d) {
		display = d;
	}

	private static boolean isNONSeparator(char c) {
		for (char d : NONSEPARATOR)
			if (d == c)
				return true;
		return false;
	}

	private static boolean isConnector(char c) {
		for (char d : CONNECTOR)
			if (d == c)
				return true;
		return false;
	}

	private static boolean containsConnector(String word) {
		for (int i = 0; i < word.length(); i++) {
			for (int j = 0; j < CONNECTOR.length; j++) {
				if (word.charAt(i) == j)
					return true;
			}
		}
		return false;
	}

	private boolean containsDigitalCharacter(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isDigit(word.charAt(i)))
				return true;
		}
		return false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String word = wordList.take();
				System.out.println(word);
				if (word.length() == 1 && !Character.isAlphabetic(word.charAt(0))) {
					if (!realSentence)
						continue;
					if (lastTakenWord.length() == 1 && !isNONSeparator(lastTakenWord.charAt(0))) {
						lastTakenWord = word;
						continue;
					}
					if (!isNONSeparator(word.charAt(0))) {
						String nextKeyWord = display.fixSentence();
						display.addNewSentence(nextKeyWord);
						realSentence = false;
					}
				} else {
					if (!containsDigitalCharacter(word) && !containsConnector(word)) {
						display.put(word.toLowerCase());
						realSentence = true;
					}
				}
				lastTakenWord = word;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
