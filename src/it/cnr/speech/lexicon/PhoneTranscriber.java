package it.cnr.speech.lexicon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Properties;

public class PhoneTranscriber {

	public static void main(String[] args) throws Exception {
		File missingDic = new File("D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\LanguageModelICAB\\missing.dic");
		File missingDicOutput = new File("D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\LanguageModelICAB\\missing_transcr.dic");
		LinkedHashMap<String, String> tl = wordlistToPhoneticTranscription(missingDic,missingDicOutput);
		File officialDictionary = new File("D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\sphinx4-samples\\src\\main\\resources\\edu\\cmu\\sphinx\\models\\it\\icab.dic");
		FileWriter fw = new FileWriter(officialDictionary,false);
		fw.write(new String(Files.readAllBytes(missingDicOutput.toPath())));
		fw.close();
		System.out.println(tl);
	}

	public static LinkedHashMap<String, String> wordlistToPhoneticTranscription(File missingDic,File missingDicOutput) throws Exception {
		BufferedReader br2 = new BufferedReader(new FileReader(missingDic));
		String line2 = br2.readLine();

		LinkedHashMap<String, String> allnewtranscriptions = new LinkedHashMap<>();
		while (line2 != null) {
			String word = line2.trim();
			String transcrip = transcribe(word);
			if (transcrip!=null)
				allnewtranscriptions.put(word, transcrip.trim());
			
			line2 = br2.readLine();
		}

		br2.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(missingDicOutput));
		for (String key:allnewtranscriptions.keySet()) {
			bw.write(key+"\t"+allnewtranscriptions.get(key)+"\n");
		}
		bw.close();
		return allnewtranscriptions;
	}

	public static String compressPhones(String word) {
		word = word.replace("ggi", "Gi");
		word = word.replace("gge", "Ge");
		word = word.replace("ge", "Ge");
		word = word.replace("gi", "Gi");
		word = word.replace("cci", "Ci");
		word = word.replace("ci", "Ci");
		word = word.replace("cce", "Ce");
		word = word.replace("ce", "Ce");
		word = word.replace("zz", "ZZ");
		word = word.replace("ch", "k");
		word = word.replace("c", "k");
		word = word.replace("q", "k");
		word = word.replace("sci", "Si");
		word = word.replace("sce", "Se");
		word = word.replace("x", "ks");
		word = word.replace("sh", "S");
		
		return word;
	}

	public static String transcribe(String word) {

		word = compressPhones(word);
		char[] chars = word.toCharArray();
		String transcr = "";
		for (char c : chars) {
			String cs = "" + c;
			if (c == 'C')
				cs = "tSS";
			else if (c == 'G')
				cs = "dZZ";
			else if (c == 'Z')
				cs = "tSS";
			else if (c == 'S')
				cs = "SS";
			else if (c == 'h')
				cs = "";
			else if (c == '??')
				cs = "EE";
			else if (c == '??')
				cs = "i";
			else if (c == '??')
				cs = "a1";
			else if (c == '??')
				cs = "o";
			else if (c == '??')
				cs = "e";
			else if (c == '??')
				cs = "u1";
			else if (c == '??')
				cs = "u1";
			else if (c == 'y')
				cs = "i";
			else if (c == '??')
				cs = "i1";
			else if (c == '??')
				cs = "e";
			else if (c == '??')
				cs = "e";
			else if (c == '??')
				cs = "i";
			else if (c == '??')
				cs = "j u";
			else if (c == '??')
				cs = "e";
			else if (c == '??')
				cs = "a1";
			else if (c == '??')
				cs = "a";
			else if (c == '???')
				cs = "";
			else if (c == '??')
				cs = "s";
			else if (c == '??')
				cs = "e";
			else if (c == '??')
				cs = "o";
			else if (c == '??')
				cs = "o";
			else if (c == '??')
				cs = "o";
			else if (c == '??')
				cs = "i";
			else if (c == '??')
				cs = "JJ";
			else if (c == '??')
				cs = "a1";
			else if (c == '??')
				cs = "u";
			else if (c == '??')
				cs = "a";
			else if (c == '??')
				cs = "o";
			else if (c == '??')
				cs = "i";
			else if (c == '??')
				cs = "i";
			
			else if (!(Character.isAlphabetic(c)))
				cs = "";
			transcr += cs + " ";
		}

		transcr = transcr.replaceAll(" +" , " ");
		if (transcr.trim().length()==0)
			transcr = null;
		return transcr;
	}

}
