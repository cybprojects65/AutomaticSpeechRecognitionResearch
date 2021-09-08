package it.cnr.speech.lexicon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LexiconGenerator {

	public static void main(String[] args) throws Exception {

		File kaldi_text = new File("text");
		File kaldi_out_folder = new File("./");
		File vocabulary = new File("words.txt");
		int nthreads = 5;
		
		if (args != null && args.length>0) {
			kaldi_text = new File(args[0]);
			kaldi_out_folder = new File(args[1]);
			vocabulary = new File(kaldi_out_folder, "words.txt");
			//PhoneTranscriberWithMary.getTranscriptionURLLocalhost = "http://text-to-speech.d4science.org:80/process?INPUT_TEXT=#TEXT#&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it";
			
			BufferedReader br = new BufferedReader(new FileReader(kaldi_text));
			
			String line = br.readLine();
			HashSet<String> allWords = new HashSet<>();
			while (line!=null) {
				String tokens []= line.split(" ");
				for(String tok:tokens) {
					if (!tok.contains("<"))
						allWords.add(tok.trim());
				}
				line = br.readLine();
			}
			
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(vocabulary));
			for (String w:allWords) {
				bw.write(w+"\n");
			}
			bw.close();
			
			
		} 
		//else PhoneTranscriberWithMary.getTranscriptionURLLocalhost = "http://text-to-speech.d4science.org:80/process?INPUT_TEXT=#TEXT#&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it";
		
		
		System.out.println("MARY TTS SERVICE:\n"+PhoneTranscriberWithMary.getTranscriptionURLLocalhost);
		
		
		LexiconGenerator gen = new LexiconGenerator();
		
		gen.allWords = Files.readAllLines(vocabulary.toPath(), Charset.forName("UTF-8"));

		File lexicon = new File(kaldi_out_folder, "lexicon.txt");
		if (lexicon.exists())
			lexicon.delete();

		FileWriter fw = new FileWriter(lexicon, true);

		fw.write("!SIL SIL\n");
		fw.write("<oov> <oov>\n");

		
		int nWords = gen.allWords.size();
		int chunks = nWords / nthreads;
		if (gen.allWords.size() % nthreads != 0)
			chunks++;

		System.out.println("N Words to transcribe " + nWords);
		System.out.println("Launching...");
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		for (int c = 0; c < nthreads; c++) {

			int i0 = c * chunks;
			int i1 = Math.min(i0 + chunks, nWords);
			System.out.println("Chunk " + (c + 1) + ": " + i0 + ";" + (i1 - 0));
			InvokeMary thread = gen.new InvokeMary(i0, i1);
			executorService.submit(thread);
		}

		System.out.println("Transcribing...");

		while (gen.getTranscribed() < nWords) {
			Thread.sleep(1000);
			int s = gen.getTranscribed()*100/nWords;
			
				System.out.println("Status:"+s);
		}

		executorService.shutdown();
		
		//List<String> transcriptions = new ArrayList<String>(gen.allTranscriptions);
		//Collections.sort(transcriptions);
		
		System.out.println("...Dumping...");
		for (String w : gen.allWords) {
			String transcription = gen.allTranscriptions.get(w);
			fw.write(w+" "+transcription+ "\n");
		}

		fw.close();
		System.out.println("Done.");
	}

	public List<String> allWords;

	public synchronized String getWord(int i) {
		return allWords.get(i);
	}

	public HashMap<String,String> allTranscriptions = new HashMap<>();

	public synchronized void addTranscription(String word,String transcription) {
		allTranscriptions.put(word,transcription);
	}

	public synchronized int getTranscribed() {
		return allTranscriptions.size();
	}

	class InvokeMary implements Runnable {

		int i0 = 0;
		int i1 = 0;

		InvokeMary(int i0, int i1) {
			this.i0 = i0;
			this.i1 = i1;
		}

		@Override
		public void run() {

			for (int i = i0; i < i1; i++) {
				String word = getWord(i);
				String transcription = "";
				try {
					transcription = PhoneticTranscriber.transcribe(word);
				} catch (Exception e) {
					transcription = PhoneTranscriber.transcribe(word);
					e.printStackTrace();
				}
				addTranscription(word,transcription);
			}
		}

	}

}
