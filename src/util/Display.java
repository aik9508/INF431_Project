package util;

import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class Display {
	Sentence currentSentence;
	public LinkedBlockingQueue<String> incorrectWordsList;
	public int currentScore;
	private Text score;
	private Text synonym;
	private static final String POISON = "eric.goubault";

	public Display(Text score, Text synonym) {
		this.currentScore = 0;
		this.score = score;
		this.synonym = synonym;
		this.incorrectWordsList = new LinkedBlockingQueue<>();
		this.currentSentence = new Sentence(null, incorrectWordsList);
	}

	public String fixSentence() {
		int error = currentSentence.checkSentence();
		currentScore += currentSentence.getScore();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				score.setText(currentScore + "");
			}
		});
		String nextKeyWord = currentSentence.nextKeyWord();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				synonym.setText(nextKeyWord);
			}
		});
		System.out.println(nextKeyWord);
		return nextKeyWord;
	}

	public void put(String word) throws InterruptedException {
		currentSentence.put(word);
		currentScore += currentSentence.getScore();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				score.setText(currentScore + "");
			}
		});
	}

	public void kill() throws InterruptedException {
		incorrectWordsList.put(POISON);
	}

	public void addNewSentence(String nextKeyWord) {
		this.currentSentence = new Sentence(nextKeyWord, incorrectWordsList);
	}

}
