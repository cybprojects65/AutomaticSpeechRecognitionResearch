package it.cnr.speech.sphinxtraining;

import java.io.IOException;

public class CorpusPreparatorApasci2 extends CorpusPreparatorSphinx {



	public static void main(String[] args) throws IOException, InterruptedException {
		 String nomeprogetto = "apasci_it";
		 boolean dizionario_flag = true;
		 boolean prepare_flag = true;
		 boolean train_flag = false;
		 boolean setupmodel_flag = true;
		 boolean up_to_dic_creation_flag = false;
		 int nSenones = 500;
		 int nGMs = 32;
		
		// dataset downloaded at https://data.d4science.net/bSQ3
		 String inizio = "D:\\WorkFolder\\Experiments\\Sphinx\\ApasciForKALDI\\";
		 String fine = "D:\\WorkFolder\\Experiments\\Sphinx\\ApasciForKALDI_Sphinx_Auto\\";
		
		new CorpusPreparatorApasci2().run( default_sphinx_aligner_location, 
				defaukt_sphinxtrainScript, 
				nomeprogetto ,
				dizionario_flag, 
				prepare_flag, 
				train_flag ,
				setupmodel_flag ,
				up_to_dic_creation_flag, 
				nSenones ,
				nGMs,
				inizio,
				fine);
	
	}
}
