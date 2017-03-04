package util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import dictionary.OxfordDictionary;

public class Sentence {

	private HashSet<String> wordList, typedWords;
	private LinkedBlockingQueue<String> incorrectWordsList;
	private int score;
	private LinkedList<String> verbs, nouns;
	String keyWord;

	public Sentence(String keyWord, LinkedBlockingQueue<String> incorrectWordsList) {
		this.wordList = new HashSet<>();
		this.typedWords = new HashSet<>();
		this.incorrectWordsList = incorrectWordsList;
		this.keyWord = keyWord;
		this.verbs = new LinkedList<>();
		this.nouns = new LinkedList<>();
		this.score = 0;
	}

	public void put(String s) throws InterruptedException {
		boolean[] nv = new boolean[2];
		LinkedList<String> originWord = new LinkedList<>();
		if (OxfordDictionary.isCorrect(s, nv, originWord)) {
			typedWords.add(s);
			if (originWord.isEmpty()) {
				wordList.add(s);
			} else {
				wordList.addAll(originWord);
			}
			score = s.length() * s.length();
			if (nv[0])
				nouns.add(s);
			if (nv[1])
				verbs.add(s);
		} else {
			incorrectWordsList.put(s);
			score = -s.length() * s.length() * s.length();
		}
	}

	public int getScore() {
		int tmp = this.score;
		this.score = 0;
		return tmp;
	}

	public String nextKeyWord() {
		Random rnd = new Random();
		verbs.addAll(nouns);
		Collections.shuffle(verbs);
		for (String word : verbs) {
			System.out.println("original word : " + word);
			String[] synonyms = OxfordDictionary.getSynonyms(word);
			if (synonyms == null)
				continue;
			LinkedList<String> candidates = new LinkedList<>();
			for (int i = 0; i < synonyms.length; i++) {
				boolean valid = true;
				for (int j = 0; j < synonyms[i].length(); j++) {
					if (synonyms[i].charAt(j) == '_') {
						valid = false;
						break;
					}
				}
				if (valid)
					candidates.add(synonyms[i]);
			}
			if (candidates.size() == 0) {
				continue;
			} else {
				return candidates.get(rnd.nextInt(candidates.size()));
			}
		}
		return keyWord;
	}

	public int checkSentence() {
		int error = 0;
		if (keyWord != null) {
			if (!wordList.contains(keyWord) && !typedWords.contains(keyWord)) {
				int tmp = keyWord.length() * keyWord.length();
				score = -tmp * tmp;
				error = 1;
			}
			if (verbs.isEmpty() || nouns.isEmpty())
				error = 2;
			else {
				if (verbs.size() == 1 && nouns.size() == 1) {
					String wordVerb = verbs.peek();
					String wordNoun = nouns.peek();
					if (wordVerb.equals(wordNoun))
						error = 2;
				}
			}
		}
		return error;
	}

}
