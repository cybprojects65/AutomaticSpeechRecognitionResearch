package it.cnr.speech.lexicon;

import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;

public class PhoneticTranscriber {

	
	public static String transcribe(String ortostring) throws Exception{
		
		String rawtranscription = PhoneTranscriber.transcribe(ortostring);
		PhoneTranscriberWithMary mary = new PhoneTranscriberWithMary();
		
		String transcription = mary.getAllphones(URLEncoder.encode(ortostring.trim(), "UTF-8"));
		if (transcription.length() == 0) {
			
			System.out.println("WARNING : connection to Mary TTS in D4Science failed");
			transcription = rawtranscription;
			
		}else {
			System.out.println("Transcription produced through Mary TTS - OK");
		}

		System.out.println("Transcription: "+transcription);
		return transcription;
	}
	
	public static void transcribeToFile(String ortostring, File output) throws Exception{
		
		
		String transcription = transcribe(ortostring);
		FileWriter fw = new FileWriter(output);
		fw.write(transcription);
		fw.close();
	}
	//EXAMPLE: http://text-to-speech.d4science.org:80/process?INPUT_TEXT=ciao amico&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it
	public static void main(String args[]) throws Exception{
		
		String input = args[0];
		String output = args[1];
		input = input.toLowerCase().trim();
		PhoneTranscriberWithMary.getTranscriptionURLLocalhost = "http://text-to-speech.d4science.org:80/process?INPUT_TEXT=#TEXT#&INPUT_TYPE=TEXT&OUTPUT_TYPE=ALLOPHONES&LOCALE=it";
		System.out.println("Orto transcription "+input);
		
		System.out.println("Output file "+output);
		
		File outputFile = new File(output);

		transcribeToFile(input, outputFile);
		
		System.out.println("Done");
		
	}
	
	
}
