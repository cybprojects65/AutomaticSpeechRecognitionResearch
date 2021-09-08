package it.cnr.speech.sphinxtraining;

import java.io.IOException;

public class CorpusPreparatorApasciVoxForge extends CorpusPreparatorSphinx {

		
	public static void main(String[] args) throws IOException, InterruptedException {
		 
		 String nomeprogetto = "apascivoxforge_it";
		 boolean dizionario_flag = false;
		 boolean prepare_flag = true;
		 boolean train_flag = false;
		 boolean setupmodel_flag = true;
		 boolean up_to_dic_creation_flag = false;
		 int nSenones = 1000;
		 int nGMs = 32;
		
		// dataset downloaded at https://data.d4science.net/bSQ3
		 String inizio = "C:\\Users\\Gianpaolo Coro\\Downloads\\Apasci_VoxForgeForKALDI\\Apasci_VoxForgeForKALDI\\";
		 String fine = "D:\\WorkFolder\\Experiments\\Sphinx\\ApasciVoxForge_Sphinx_Auto\\";
		new CorpusPreparatorApasciVoxForge().run( default_sphinx_aligner_location, 
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
