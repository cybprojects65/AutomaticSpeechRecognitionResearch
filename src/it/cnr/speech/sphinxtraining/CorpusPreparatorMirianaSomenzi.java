package it.cnr.speech.sphinxtraining;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CorpusPreparatorMirianaSomenzi {
	
	public static String sphinx_aligner_location = "D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4_aligner.jar"; 
	public static String sphinxtrainScript = "D:\\WorkFolder\\Experiments\\Sphinx\\sphinx4\\SphinxBinariesForWindows\\sphinxtrain-5prealpha-win32\\sphinxtrain\\scripts\\sphinxtrain.py";
	public static String nomeprogetto = "digit";
	public static boolean dizionario_flag = true;
	public static boolean prepare_flag = false;
	
	public static String inizio = "D:\\WorkFolder\\Experiments\\Sphinx\\TirocinioSomenziCorpusRaw\\";
	public static String fine = "D:\\WorkFolder\\Experiments\\Sphinx\\TirocinioSomenziSphinxAuto\\";
	
	
	public static boolean isWindows() {
		
		String OSName = System.getProperty("os.name");
		if (OSName.contains("Windows"))
			return true;
		else
			return false;
	}
	public static void esegui_dentro_shell(String cmd, String directory) throws IOException, InterruptedException {
		System.out.println("Executing " + cmd);
		
		// metodo per eseguire programmi direttamente da terminale -> creo lista di comandi base per aprire la shell e
		// aggiungo alla fine che deve eseguire il comando specificato in inout nella funzione
		String[] comandi = null;
		if (isWindows()) {
			String[] comandiW = {
					"cmd",
					"/C",
					cmd
			};
			comandi = comandiW;
		}else {
			
			String[] comandiL = {
				"/bin/sh",
				"-c",
				cmd
			};
			comandi = comandiL;
		}
		
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(comandi, null, new File(directory));
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		BufferedReader bufEr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line = "";
		
		// Stampare messaggi normali
		while ((line=buf.readLine())!=null) {
			System.out.println(line);
		}
		line = "";
		
		// Stampare eventuali errori
		while ((line=bufEr.readLine())!=null) {
			System.out.println(line);
		}
		
		buf.close();
		bufEr.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		String directory_comandi_lm = fine+"etc/";
		String directory_comandi_sphinx = fine;
		String textfile = nomeprogetto+".txt";
		String vocabfile = nomeprogetto+".vocab";
		String idngramfile = nomeprogetto+".idngram";
		String lmfile = nomeprogetto+".lm";
		
		if (prepare_flag) {
		//crea directory con cartelle -etc e -wav
		try {
		Files.createDirectory(Paths.get(fine));
		Files.createDirectory(Paths.get(fine + "etc"));
		Files.createDirectory(Paths.get(fine + "wav"));
		System.out.println("[1] Creating directories...");
		} catch(FileAlreadyExistsException e) {
			
			System.out.println("[1] Directories already created");
		}
		
	
		// Prende tutti gli eventuali file .wav e li mette dentro la cartella wav
		File dir = new File(inizio);
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".wav");
		    }
		});

		for (File wavfile : files) {
			String filename = wavfile.getName();
		    System.out.println("[2] Moving " + filename + " to wav directory");
		    Files.copy(wavfile.toPath(),new File(fine + "wav/" + filename).toPath(),StandardCopyOption.REPLACE_EXISTING);
		}
		System.out.println("[2] Every wav moved to its dir");
		
	
		// Dentro la cartella wav abbiamo vari file .wav, con la sintassi nome-sesso-nomefile.wav
		// Per ogni file:
		//     - leggo  nome
		//     - splitto il nome per trattini
		//     - i primi due token sono il nome della cartella, combinandoli in una stringa
		//            - se non esiste la cartella con quel nome, la creo, e inserisco il file
		//            - se esiste, lo inserisco direttamente

		dir = new File(fine + "wav/");
		files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".wav");
		    }
		});

		for (File wavfile : files) {
			String filename = wavfile.getName();
			String[] tokens = filename.split("-");
			String speaker = tokens[0] + "-" + tokens[1];
			
			try {
				Files.createDirectory(Paths.get(fine + "wav/" + speaker));
				System.out.println("[3] New speaker found: " + speaker + ", creating new dir!");
			}catch(FileAlreadyExistsException e) {
			}
			
		    System.out.println("[3] Moving " + filename + " to "+ speaker +" directory");
		    wavfile.renameTo(new File(fine + "wav/" + speaker + "/" + filename));

		}
	
		System.out.println("[3] Every speaker's wav moved to its dir");

		
		
		
		//POI dentro etc
		// creare file di testo {nomeprogetto}.filler con dentro 
		//		<s> SIL
		//		</s> SIL
		//		<sil> SIL 
		try (PrintWriter out = new PrintWriter(fine + "etc/"+ nomeprogetto+".filler")) {
		    System.out.println("[4] Creating filler...");
			out.println("<s> SIL\n</s> SIL\n<sil> SIL");
		}
 
		
		
		// Creare {nomeprogetto}_train.fileids: 
		String[] types = new String[] {"train", "test"};
		String filename_senza_estensione;
		
		ArrayList<String> filename_per_ordinare = new ArrayList<String>();
			
		for (String type : types) {
			
			// Apro il file fileids {nomeprogetto}_train.fileids con un PrintWriter per poter successivamente
			// scriverci dentro
			try (PrintWriter out = new PrintWriter(fine + "etc/"+ nomeprogetto+"_"+type+".fileids")) {
			    System.out.println("[5] Creating "+ nomeprogetto+"_"+type+".fileids...");
			
				// Vado nella cartella wav e creo una lista con gli speakers
				dir = new File(fine + "wav/");
				String[] speakers = dir.list(new FilenameFilter() {
					  @Override
					  public boolean accept(File current, String name) {
					    return new File(current, name).isDirectory();
					  }
					});
		
				// Per ogni speaker:
				for (String speaker : speakers) {
					
					dir = new File(fine + "wav/" + speaker);
					
					// Prendo i relativi file wav
					files = dir.listFiles(new FilenameFilter() {
					    @Override
					    public boolean accept(File dir, String name) {
					        return name.endsWith(".wav");
					    }
					});
					
					// Per ogni wav dello speaker:
					for (File wavfile : files) {
						
						//  Prendo il nome del file (senza estensione)
						filename_senza_estensione = wavfile.getName().substring(0, wavfile.getName().lastIndexOf("."));
						// filename_per_ordinare è una lista a cui aggiungo ogni volta il filename_senza_estensione che uso
						//per fare una corrispondenza tra fileids e filetranscription essendo in comune
						filename_per_ordinare.add(filename_senza_estensione);
						
						// Scrivo {nomespeaker}/{nomefile}
						out.println(speaker + "/"+filename_senza_estensione);
					}
				}			
			}
		}
				
		
		
		// Creo un insieme contenente i valori, uso set perchè elimina in automatico i duplicati
		HashSet<String> parole = new HashSet<String>();
		
//		  -file train_transcription:
//		  file txt con dentro tutte le trascrizioni dei file wav ma in modo
//		  "<s> uno </s>
//		  -file test_fileids/ test-transcription (uguali a train) 
		
		// Per creare il file transcription, prendo ogni txt nella root del progetto e
		// aggiungo una riga dentro al file transcription:
		//    - <s> {contenuto} </s> (nomefile senza txt)
		
		for (String type : types) {
			try (PrintWriter out = new PrintWriter(fine + "etc/"+ nomeprogetto+"_"+type+".transcription")) {
				try	(PrintWriter testo = new PrintWriter(fine + "etc/"+ nomeprogetto+".txt")){
					System.out.println("[6] Creating "+ nomeprogetto+"_"+type+".transcription");
	
					// Prendo tutti i txt dentro la cartella
					dir = new File(inizio);
					files = dir.listFiles(new FilenameFilter() {
					    @Override
					    public boolean accept(File dir, String name) {
					        return name.endsWith(".txt");
					    }
					});
					
					//creo arraylist di numeri che corrispondono alla posizione (nell'altra lista precedentemente
					// creata per mantenere l'ordine) delle parole chiave in comune (es: miriana-uno-1)
					ArrayList<Integer> posizione_dellachiave_nellaltro = new ArrayList<Integer>();
					
					final ArrayList<String> lista_file_da_ordinare = new ArrayList<String>();
					HashMap<String, File> hashmap_per_stampare_ordinati = new HashMap();
					
					// Itero tutti i file relativi alle transcriptions ed estraggo il filename senza estensione
					// che sarebbe la "parola chiave" comune anche al passaggio precedente. Dopo averla estratta,
					// la inserisco dentro alla lista "lista_file_da_ordinare", e la cerco dentro alla lista creata
					// nel passaggio precedente per poter mantenere l'ordine: facendo così so esattamente la posizione 
					// nell'altra lista (un numero intero) e posso inserire questo valore dentro
					// la lista "posizione_dellachiave_nellaltro". 
					// Inserisco, infine, sia la parola chiave (il filename senza estensione) sia la variabile relativa
					// al file dentro un hashmap, in modo tale poi da poter prendere il file in base alla chiave
					// (viene usato successivamente per stampare ordinato e mantenere una corrispondenza tra chiave-file)
					for (File file : files) {
						filename_senza_estensione = file.getName().substring(0, file.getName().lastIndexOf("."));
						lista_file_da_ordinare.add(filename_senza_estensione);
						posizione_dellachiave_nellaltro.add(filename_per_ordinare.indexOf(filename_senza_estensione));
						hashmap_per_stampare_ordinati.put(filename_senza_estensione, file);
					}
					
					// Vuole l'array classico non l'arraylist, quindi converto
					int[] ints = posizione_dellachiave_nellaltro.stream().mapToInt(i -> i).toArray();
					
					// Converto anche questo in array
					String[] array_filename_per_ordinare = filename_per_ordinare.toArray(new String[filename_per_ordinare.size()]);

					// Copio pari pari la lista dei file da ordinare, prima di ordinarla
				    ArrayList<String> lista_file_ordinata = new ArrayList<String>(lista_file_da_ordinare);
				    
				    // Ordino le stringhe contenute nell'arraylist lista_file_ordinata sulla base delle posizioni
				    // contenute nell'array ints, in modo tale da avere la stessa esatta corrispondenza con la sequenza
				    // generata nel passo precedente (fileids)
				    Collections.sort(lista_file_ordinata, Comparator.comparing(s -> ints[lista_file_da_ordinare.indexOf(s)]));
				    
				    // Adesso per procedere normalmente, proseguo prendendo la chiave dalla lista_file_ordinata, che
				    // conterrà la sequenza finalmente ordinata
					for (String file_name : lista_file_ordinata) {
						
						// Prendo il file in base al nome che ho precedentemente ordinato nella lista
						// attraverso l'hashmap che abbiamo creato prima di ordinare tutto
						File file = hashmap_per_stampare_ordinati.get(file_name);
												
						// Questo è per leggere il contenuto e rimuovere l'andata a capo
						String contenuto = new String(Files.readAllBytes(Paths.get(file.toString())), StandardCharsets.UTF_8).replace("\n", "");
					
						// Dopo che ho letto il contenuto del file, posso ricreare la classica stringa che mi serve per la transcription e scriverla nel relativo file
						out.println("<s> " +  contenuto + " </s> ("+file_name+")");
						
						// Questo per stampare anche nel file .txt - senza il filename tra parentesi
						testo.println("<s> " +  contenuto + " </s>");
						
						// Aggiungo il contenuto al set delle parole per essere trattato successivamente
						// eseguo lo splitting sulla base dello spazio in modo tale da gestire anche frasi
						parole.addAll(Arrays.asList(contenuto.split(" ")));
						
					}
				}
			}
		}
	
		
	// Creo un hashmap per il dic
	HashMap<String, String> dic = new HashMap();
	
	// Creo set per i fonemi e ci aggiungo il SIL
	HashSet<String> phones = new HashSet<>();
	phones.add("SIL"); 
	
	if (dizionario_flag) { //GPC: moved here to avoid calling the transcriptor
	// Per ogni parola estratta dal contenuto, estraggo la sua trascrizione
	for (String parola : parole) {
		
		// Eseguo il comando per fare l'alignment
		System.out.print("[7] Transcripting " + parola + "... ");
		Process ps = Runtime.getRuntime().exec("java -cp "+sphinx_aligner_location+" edu.cmu.sphinx.demo.corpus.isti.PhoneticTranscriber '"+parola+"' "+parola+".txt");
		ps.waitFor();
		
		// Dopo che ho eseguito il jar di sphinx aligner, mi salva l'output in out.txt, quindi leggo l'output e
		// quello diventa il valore associato alla chiave del mio hashmap dic. Quindi dentro dic, aggiungerò,
		//       key: parola, value: risultato di out.txt
		String transcription = new String(Files.readAllBytes(Paths.get(parola + ".txt")), StandardCharsets.UTF_8).replace("\n", "");
		
		System.out.println(transcription);

		// Per salvare i fonemi, prendo la transcription ottenuta e splitto tutti gli elementi sulla base dello spazio
		phones.addAll(Arrays.asList(transcription.split(" ")));
				
		// Per rimuovere il file dopo che ho letto il risultato dell'evaluation di sphinx aligner
		File transcriptionfile = new File(parola + ".txt");
		transcriptionfile.delete();
		
		// Salvo una corrispondenza tra la parola e la trascrizione per poter successivamente stampare il dic
		dic.put(parola, transcription);
		
	}

	
	// Adesso creo il file dic, che consiste nel stampare l'intero hashmap dentro un file chiamato {nomeprogetto}.dic dentro etc
	
		try (PrintWriter out = new PrintWriter(fine + "etc/"+ nomeprogetto+".dic")) {
		    System.out.println("[8] Saving dic file...");
		    
		    for (Map.Entry<String, String> entry : dic.entrySet()) {
		        out.println(entry.getKey() + " " + entry.getValue());
		    }
		}
	}
	
	// Stampo i fonemi su file
	try (PrintWriter out = new PrintWriter(fine + "etc/"+ nomeprogetto+".phone")) {
	    System.out.println("[9] Saving phone file...");
	    
	    for (String phone : phones) {
	        out.println(phone);
	    }
	}
	
	
	System.out.println("[10] Creating LM");

	
	
	// Creo lista di comandi per generare LM
	ArrayList<String> comandi = new ArrayList();
	comandi.add("text2wfreq < "+textfile+" |  wfreq2vocab > "+vocabfile);
	comandi.add("text2idngram -vocab "+vocabfile+" -idngram "+idngramfile+" < "+textfile);
	comandi.add("idngram2lm -vocab_type 0 -idngram " + idngramfile+" -vocab "+vocabfile+" -arpa "+lmfile);

	// Li eseguo
	for (String comando : comandi) {
		esegui_dentro_shell(comando, directory_comandi_lm);
	}
		}
	
	System.out.println("[11] Start training");
	ArrayList<String> comandi = new ArrayList();
	// Pulisco i precedenti comandi e inserisco quelli per avviare il training
	comandi.clear();
	
	if (!isWindows()) {
	comandi.add("cd "+fine);
	comandi.add("export PATH=/usr/local/bin:$PATH");
	comandi.add("export LD_LIBRARY_PATH=/usr/local/lib");
	comandi.add("export PKG_CONFIG_PATH=/usr/local/lib/pkgconfig");
	
	comandi.add("sphinxtrain -t "+nomeprogetto+" setup ");
	comandi.add("sed -i 's/$CFG_QUEUE_TYPE = \"Queue\"/$CFG_QUEUE_TYPE = \"Queue::POSIX\"/g' "+fine+"etc/sphinx_train.cfg");
	comandi.add("sed -i 's/lm\\.DMP\\\"/lm\\\"/g' "+fine+"etc/sphinx_train.cfg");
	comandi.add("sed -i 's/$CFG_N_TIED_STATES = 200/$CFG_N_TIED_STATES = 7/g' "+fine+"etc/sphinx_train.cfg");
	comandi.add("sed -i \"s/$CFG_CD_TRAIN = 'yes'/$CFG_CD_TRAIN = 'no'/g\" "+fine+"etc/sphinx_train.cfg");
	comandi.add("sed -i 's/$DEC_CFG_MODEL_NAME = \"$CFG_EXPTNAME.cd_${CFG_DIRLABEL}_${CFG_N_TIED_STATES}\"/$DEC_CFG_MODEL_NAME = \"$CFG_EXPTNAME.ci_cont\"/g' "+fine+"etc/sphinx_train.cfg");
	comandi.add("sphinxtrain run ");
	}else {
		
		
		comandi.add("cd "+fine);
		comandi.add("python "+sphinxtrainScript+" -t "+nomeprogetto+" setup ");
		comandi.add("sed -i \"s/lm\\.DMP\\\"/lm\\\"/g\" "+fine+"etc/sphinx_train.cfg");
		comandi.add("sed -i \"s/$CFG_N_TIED_STATES = 200/$CFG_N_TIED_STATES = 7/g\" "+fine+"etc/sphinx_train.cfg");
		comandi.add("sed -i \"s/$CFG_CD_TRAIN = 'yes'/$CFG_CD_TRAIN = 'no'/g\" etc/sphinx_train.cfg");
		comandi.add("sed -i \"s/$DEC_CFG_MODEL_NAME = \\\"$CFG_EXPTNAME.cd_${CFG_DIRLABEL}_${CFG_N_TIED_STATES}\\\"/$DEC_CFG_MODEL_NAME = \\\"$CFG_EXPTNAME.ci_cont\\\"/g\" "+fine+"etc/sphinx_train.cfg");
		comandi.add("python "+sphinxtrainScript+" run");
		
	}
		
	// Li eseguo
	for (String comando : comandi) {
		esegui_dentro_shell(comando, directory_comandi_sphinx);
	}
	
	System.out.println("\nEnd of the program.");
	}
	
}
