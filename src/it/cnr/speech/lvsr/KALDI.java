package it.cnr.speech.lvsr;

import java.io.File;

import it.cnr.speech.fhmm.matlab.OSCommands;

public class KALDI implements ILVSR {

	@Override
	public String recognize(File wave) {

		String recognized = OSCommands.executeCommandForceAndGetResult("cmd /c sox \"" + wave.getAbsolutePath()
				+ "\" -t raw -c 1 -b 16 -r 16k -e signed-integer --endian little - | nc 146.48.87.169 5050");

		return recognized;
	}

	public static void main(String[] args) {

		KALDI s = new KALDI();
		File wave = new File("D:/WorkFolder/Experiments/Sphinx/sampleUNO16k.wav");
		String reco = s.recognize(wave);
		System.out.println("Recognized: " + reco);

	}

}
