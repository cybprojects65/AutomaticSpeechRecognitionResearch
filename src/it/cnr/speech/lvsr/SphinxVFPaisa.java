package it.cnr.speech.lvsr;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import it.cnr.speech.fhmm.matlab.OSCommands;

public class SphinxVFPaisa implements ILVSR{
	
	static String sphinxASRLocation = "D:/WorkFolder/Experiments/Sphinx/sphinx4_aligner.jar";
	
	static boolean cache = true;
	
	@Override
	public String recognize(File wave) {
		File transcrFile = new File(wave.getAbsolutePath().replace(".wav", "_transcription.txt"));
		if (!(transcrFile.exists() && cache)) {
			String javaExecution = "java -jar "+sphinxASRLocation+" \""+wave.getAbsolutePath()+"\"";
			System.out.println("Executing: "+javaExecution);
			OSCommands.executeCommandForce2(javaExecution,false);
		}
		
		
		String txt = "";
		try {
			txt = new String(Files.readAllBytes(transcrFile.toPath()),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		
		return txt;
	}
	
	
	public static void main(String[] args) {
		
		SphinxVFPaisa s = new SphinxVFPaisa();
		File wave = new File("D:/WorkFolder/Experiments/Sphinx/sampleUNO16k.wav");
		String reco = s.recognize(wave);
		System.out.println("Recognized: "+reco);
		
	}
	
}
