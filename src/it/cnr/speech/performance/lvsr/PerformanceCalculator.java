package it.cnr.speech.performance.lvsr;

import java.io.File;
import java.nio.file.Files;

import it.cnr.speech.lvsr.ILVSR;
import it.cnr.speech.lvsr.SphinxAPASCIPaisa;
import it.cnr.speech.lvsr.SphinxAPASCIVOXFORGEPaisa;
import it.cnr.speech.lvsr.SphinxVFPaisa;
import it.cnr.speech.performance.RecognitionDistance;

public class PerformanceCalculator {

	public static void main(String[] args) throws Exception{
		
		
		File[] evaluationFolders = {
				new File("D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\testset_clean_speech\\"),
				new File("D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\testset_noisy_speech\\")
		};
		
		//ILVSR recognizer = new SphinxVFPaisa();
		ILVSR recognizer = new SphinxAPASCIPaisa();
		
		//ILVSR recognizer = new SphinxAPASCIVOXFORGEPaisa();
		PerformanceCalculator pc = new PerformanceCalculator();
		pc.evaluate(recognizer, evaluationFolders);
		
	}
	
	public void evaluate(ILVSR recognizer, File [] eFolders) throws Exception{
		
		for (File eFolder:eFolders) {
			
			System.out.println("Evaluating "+eFolder.getName());
			File allFiles []=eFolder.listFiles();
			int werTot = 0;
			int serTot = 0;
			int counter = 0;
			int wcounter = 0;
			
			for (File file:allFiles) {
				
				if (file.getName().endsWith(".wav")) {
					File txt = new File (file.getAbsolutePath().replace(".wav", ".txt"));
					String sentence = new String(Files.readAllBytes(txt.toPath()),"UTF-8").toLowerCase().trim();
					String [] words = sentence.split(" ");
					
					String recognized = recognizer.recognize(file).toLowerCase().trim();
					String [] recogWords = recognized.split(" ");
					
					System.out.println("Ref:"+sentence);
					System.out.println("Rec:"+recognized);
					int wer = RecognitionDistance.calcWER(words, recogWords);
					
					String sentGold = recognized.replaceAll("[^a-z]", "");
					String sentRec = recognized.replaceAll("[^a-z]", "");
					if (!sentGold.equals(sentRec))
						serTot++;
					
					werTot+=wer;
					System.out.println("Dist:"+wer);
					wcounter+=words.length;
					
					counter++;
				}
				
			}
			
			float wer = (float) werTot*100f/(float)wcounter;
			float ser = (float) serTot*100f/(float)counter;
			
			float wacc = 100f-(float) wer;
			float sacc = 100f-(float) ser;
			
			System.out.println("\tWER:"+wer+"%");
			System.out.println("\tWORD ACCURACY:"+wacc+"%");
			System.out.println("\tSER:"+ser+"%");
			System.out.println("\tSENTENCE ACCURACY:"+sacc+"%");
			
		}
	
	}

}
