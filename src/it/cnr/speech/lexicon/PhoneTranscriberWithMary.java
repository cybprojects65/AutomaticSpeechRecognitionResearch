package it.cnr.speech.lexicon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhoneTranscriberWithMary {

	public static String getTranscriptionURL = "http://146.48.87.169:59125/process?INPUT_TEXT=#TEXT#&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it";
	public static String getTranscriptionURLLocalhost = "http://localhost:59125/process?INPUT_TEXT=#TEXT#&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it";
	
	public static String getURL(String endpoint) throws Exception {
		String result = null;

		// Send a GET request to the servlet

		// Send data
		String urlStr = endpoint;

		URL url = new URL(urlStr);
		URLConnection conn = url.openConnection();
		
		String redirect = conn.getHeaderField("Location");
		if (redirect != null){
			//redirect= redirect.replace("https", "http");
		    conn = new URL(redirect).openConnection();
		}
		
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(20000);

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		result = sb.toString();

		return result;
	}

	public static String getURLRedirect(String endpoint) throws Exception {
		String result = null;

		// Send a GET request to the servlet

		// Send data
		String urlStr = endpoint;

		URL url = new URL(urlStr);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(1000);
		conn.setReadTimeout(2000);

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		result = sb.toString();

		return result;
	}
	
	public boolean localhost = true;
	
	public String getAllphones(String input) throws Exception {
		String url = "";
		String allophone = "";
		try {
			if (localhost)
				url = getTranscriptionURLLocalhost.replace("#TEXT#", input);
			else
				url = getTranscriptionURL.replace("#TEXT#", input);
			allophone = getURL(url);
		} catch (Throwable e) {
			try {
				if (localhost)
					url = getTranscriptionURLLocalhost.replace("#TEXT#", input);
				else
					url = getTranscriptionURL.replace("#TEXT#", input + ".");
				allophone = getURL(url);

			} catch (Throwable ee) {
				// e.printStackTrace();
				return "";
				// throw new Exception("Error with word: "+input);
			}

		}

		// System.out.println(allophone);
		String phonemes = "";
		String search = "<ph ";
		List<String> foundphonemes = new ArrayList<>();

		while (allophone.contains(search)) {

			allophone = allophone.substring(allophone.indexOf(search) + search.length());
			allophone = allophone.substring(allophone.indexOf("\"") + 1);
			String phoneme = allophone.substring(0, allophone.indexOf("\""));
			// System.out.println("Ph: "+phoneme);
			foundphonemes.add(phoneme);

		}

		List<String> correctphonemes = new ArrayList<>();

		for (String ph : foundphonemes) {
			String mapped = mapPhonemeToSphinx(ph);
			correctphonemes.add(mapped);
			// System.out.println("Ph: "+mapped);
		}

		phonemes = list2Transcription(correctphonemes);

		return phonemes;
	}

	public String list2Transcription(List<String> phonemes) {

		StringBuffer sb = new StringBuffer();
		for (String p : phonemes) {
			sb.append(p + " ");
		}
		String transcription = sb.toString().trim();
		transcription = transcription.replaceAll(" +", " ");
		return transcription.trim();
	}

	public String mapPhonemeToSphinx(String ph) {

		if (ph.equals("tS"))
			return "tSS";
		else if (ph.equals("O1"))
			return "OO";
		else if (ph.equals("O"))
			return "o";
		else if (ph.equals("E1"))
			return "EE";
		else if (ph.equals("tts"))
			return "ts ts";
		else if (ph.equals("NG"))
			return "ng";
		else if (ph.equals("dd"))
			return "d d";
		else if (ph.equals("tt"))
			return "t t";
		else if (ph.equals("ll"))
			return "l l";
		else if (ph.equals("NF"))
			return "nf";
		else if (ph.equals("ss"))
			return "s s";
		else if (ph.equals("rr"))
			return "r r";
		else if (ph.equals("ff"))
			return "f f";
		else if (ph.equals("kk"))
			return "k k";
		else if (ph.equals("dZ"))
			return "dZZ";
		else if (ph.equals("mm"))
			return "m m";
		else if (ph.equals("vv"))
			return "v v";
		else if (ph.equals("ttS"))
			return "tSS tSS";
		else if (ph.equals("ddz"))
			return "dz dz";
		else if (ph.equals("ddZ"))
			return "dZZ dZZ";
		else if (ph.equals("nn"))
			return "n n";
		else if (ph.equals("E"))
			return "EE";
		else if (ph.equals("bb"))
			return "b b";
		else if (ph.equals("pp"))
			return "p p";
		else if (ph.equals("gg"))
			return "g g";
		else if (ph.equals("S"))
			return "SS";
		else if (ph.equals("L"))
			return "LL";
		else if (ph.equals("J"))
			return "JJ";
		else if (ph.equals("ù"))
			return "u";
		else if (ph.equals("í"))
			return "i1";
		else if (ph.contains("ú"))
			return "u1";
		else if (ph.contains("ù"))
			return "u1";
		
		else
			return ph;

	}

	public static List<File> splitDictionary(File dictionary, int nparts) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(dictionary));
		String line = br.readLine();
		long nlines = 0;
		while (line != null) {
			nlines++;
			line = br.readLine();
		}

		br.close();
		int max = (int) (((float) nlines / (float) nparts) + 1);

		int total = 0;
		int increment = 0;
		File newDictionary = null;
		BufferedWriter bw = null;

		newDictionary = new File(dictionary.getAbsolutePath() + "_" + increment + ".dic");
		bw = new BufferedWriter(new FileWriter(newDictionary));
		List<File> dictionaries = new ArrayList<>();
		br = new BufferedReader(new FileReader(dictionary));
		line = br.readLine();
		while (line != null) {

			bw.write(line + "\n");
			line = br.readLine();

			total++;

			if (total == max) {
				total = 0;
				increment++;
				bw.close();
				dictionaries.add(newDictionary);
				newDictionary = new File(dictionary.getAbsolutePath() + "_" + increment + ".dic");
				bw = new BufferedWriter(new FileWriter(newDictionary));

			}

		}

		br.close();
		bw.close();
		if (total<max)
			dictionaries.add(newDictionary);
		
		return dictionaries;
	}

	public class RunTranscription implements Runnable {
		File dictionary;
		File newDictionary;

		public RunTranscription(File dictionary, File newDictionary) {
			this.dictionary = dictionary;
			this.newDictionary = newDictionary;
		}

		@Override
		public void run() {
			try {
				PhoneTranscriberWithMary ptm = new PhoneTranscriberWithMary();
				ptm.reviseDictionary(dictionary, newDictionary);
			} catch (Exception e) {
				e.printStackTrace();
				nFailed++;
			}

			nTranscribed++;
		}

	}

	static int nTranscribed = 0;
	static int nFailed = 0;

	public static void reviseDictionaryParallel(File dictionary, File newDictionary) throws Exception {
		nTranscribed = 0;
		long t0 = System.currentTimeMillis();
		int nCores = Runtime.getRuntime().availableProcessors();
		List<File> dictionaries = splitDictionary(dictionary, nCores);
		ExecutorService executorService = Executors.newFixedThreadPool(nCores);

		int counter = 0;
		System.out.println("Mapping...");
		List<File> newDictionaries = new ArrayList<>();
		for (File f : dictionaries) {

			File newDict = new File(f.getAbsolutePath() + ".rev.dic");
			System.out.println(counter + "->" + f.getName() + " -> " + newDict.getName());
			newDictionaries.add(newDict);
			RunTranscription runner = new PhoneTranscriberWithMary().new RunTranscription(f, newDict);
			executorService.submit(runner);
			counter++;

		}

		int prevNTrans = 0;
		while (nTranscribed < counter) {
			Thread.sleep(1000);
			if (nTranscribed != prevNTrans) {
				System.out.println("Finished " + nTranscribed + " subdocs");
				prevNTrans = nTranscribed;
			}
		}
		executorService.shutdown();
		System.out.println("Reducing...");
		if (newDictionary.exists())
			newDictionary.delete();
		
		if (!newDictionary.exists())
			newDictionary.createNewFile();
		
		for (File f : newDictionaries) {
			System.out.println("Appending "+f.getName());
			Files.write(newDictionary.toPath(), Files.readAllBytes(f.toPath()), StandardOpenOption.APPEND);
		}
		System.out.println("Done.");
		long t1 = System.currentTimeMillis();
		System.out.println("Dictionary revised in " + ((t1 - t0) / (60 * 1000)) + " min");
	}

	public void reviseDictionary(File dictionary, File newDictionary) throws Exception {

		long t0 = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader(dictionary));
		String line = br.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(newDictionary));
		int nOriginal = 0;
		int total = 0;
		while (line != null) {
			String word = line.substring(0, line.indexOf("\t"));
			String originalTranscription = line.substring(line.indexOf("\t") + 1);
			String transcription = getAllphones(word.trim());
			if (transcription.length() == 0) {
				transcription = originalTranscription;
				nOriginal++;
			}

			bw.write(word + "\t" + transcription + "\n");
			line = br.readLine();
			total++;
			if (total % 1000 == 0)
				System.out.println(dictionary.getName()+"->"+total * 100f / (float) 16542);

		}

		br.close();
		bw.close();

		long t1 = System.currentTimeMillis();
		System.out.println(dictionary.getName()+"->"+"Dictionary revised in " + (t1 - t0) + "ms");
		System.out.println(dictionary.getName()+"->"+"Transcriptions left original: " + nOriginal + " over " + total + " ("
				+ ((float) nOriginal / (float) total) + ")");

	}

	public static void main(String[] args) throws Exception {

		PhoneTranscriberWithMary ptm = new PhoneTranscriberWithMary();
		String input = "belzebú";
		System.out.println(ptm.getAllphones(input));

		File dictionary = new File("..\\LanguageModelICAB\\icab.dic");
		File newdictionary = new File("..\\LanguageModelICAB\\icabCorr.dic");
		// ptm.reviseDictionary(dictionary, newdictionary);
		//PhoneTranscriberWithMary.reviseDictionaryParallel(dictionary, newdictionary);

	}
}
