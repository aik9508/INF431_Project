package dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class OxfordDictionary {

	protected static String dictionarySynonym(String word) {
		final String language = "en";
		final String word_id = word.toLowerCase(); // word id is case sensitive
													// and lowercase is required
		return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id
				+ "/synonyms;antonyms";
	}

	protected static String dictionaryEntries(String word) {
		final String language = "en";
		final String word_id = word.toLowerCase(); // word id is case sensitive
													// and lowercase is required
		return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
	}

	protected static String dictionaryInflectedForm(String word) {
		final String language = "en";
		final String word_id = word.toLowerCase(); // word id is case sensitive
													// and lowercase is required
		return "https://od-api.oxforddictionaries.com:443/api/v1/inflections/" + language + "/" + word_id;
	}

	protected static String doInBackground(String params) {

		// TODO: replace with your own app id and app key
		final String app_id = "b0f3c05c";
		final String app_key = "d023490df554ca4af578c62a8e0513db";
		try {
			URL url = new URL(params);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("app_id", app_id);
			urlConnection.setRequestProperty("app_key", app_key);

			// read the output from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			return stringBuilder.toString();

		} catch (Exception e) {
			// e.printStackTrace();
			return "Not Found";
		}
	}

	public static boolean isCorrect(String word, boolean[] nv, LinkedList<String> originWord) {
		String params = dictionaryInflectedForm(word);
		String response = doInBackground(params);
		if (response.equals("Not Found")) {
			params = dictionaryEntries(word);
			response = doInBackground(params);
			return !response.equals("Not Found");
		} else {
			JSONObject json = new JSONObject(response);
			JSONObject results = json.getJSONArray("results").getJSONObject(0);
			JSONArray lexicalEntries = results.getJSONArray("lexicalEntries");
			// System.out.println(lexicalEntries.getJSONObject(0));
			for (int i = 0; i < lexicalEntries.length(); i++) {
				String category = lexicalEntries.getJSONObject(i).getString("lexicalCategory");
				originWord.add( lexicalEntries.getJSONObject(i).getJSONArray("inflectionOf").getJSONObject(0)
						.getString("text").toLowerCase());
				if (category.equals("Noun"))
					nv[0] = true;
				else if (category.equals("Verb"))
					nv[1] = true;
			}
			return true;
		}
	}

	public static String[] getSynonyms(String word) {
		String params = dictionarySynonym(word);
		String response = doInBackground(params);
		if (response.equals("Not Found"))
			return null;
		else {
			JSONObject obj = new JSONObject(response);
			JSONObject results = obj.getJSONArray("results").getJSONObject(0);
			JSONObject lexicalEntry = results.getJSONArray("lexicalEntries").getJSONObject(0);
			JSONObject entry = lexicalEntry.getJSONArray("entries").getJSONObject(0);
			JSONObject sense = entry.getJSONArray("senses").getJSONObject(0);
			JSONArray synonyms = sense.getJSONArray("synonyms");
			String[] syns = new String[synonyms.length()];
			for (int i = 0; i < synonyms.length(); i++) {
				syns[i] = synonyms.getJSONObject(i).getString("id");
			}
			return syns;
		}
	}

	public static void main(String[] args) {
		boolean[] nv = new boolean[2];
		LinkedList<String> originWord=new LinkedList<>();
		String word = "researching";
		System.out.println("word : " + word);
		System.out.println("isCorrect : " + isCorrect(word, nv, originWord));
		System.out.println("isNoun : " + nv[0]);
		System.out.println("isVerb : " + nv[1]);
	}
}
