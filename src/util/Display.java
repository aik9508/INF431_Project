package util;

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Display {
	Sentence currentSentence;
	JTextArea incorrectWords;
	LinkedBlockingQueue<String> incorrectWordsList;
	JLabel score;
	int currentScore;

	public Display(JTextArea jta, JLabel jl) {
		this.currentScore = 0;
		this.incorrectWords = jta;
		this.score = jl;
		this.incorrectWordsList = new LinkedBlockingQueue<>();
		this.currentSentence = new Sentence(null, incorrectWordsList);
	}

	public String fixSentence() {
		int error=currentSentence.checkSentence();
		currentScore+=currentSentence.getScore();
		String nextKeyWord=currentSentence.nextKeyWord();
		System.out.println(nextKeyWord);
		return nextKeyWord;
	}

	public void put(String word) throws InterruptedException {
		currentSentence.put(word);
		currentScore+=currentSentence.getScore();
	}

	public void addNewSentence(String nextKeyWord) {
		this.currentSentence=new Sentence(nextKeyWord, incorrectWordsList);
	}

}
