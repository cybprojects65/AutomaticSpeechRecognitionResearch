package it.cnr.speech.fhmm.matlab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class OSCommands {

	public static void executeCommandForce(String command) {
		System.out.println(">" + command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			
			while ((line = reader.readLine()) != null)
			    System.out.println(line);
			
			process.waitFor();
			System.out.println("Executed");
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
	public static String executeCommandForceAndGetResult(String command) {
		System.out.println(">" + command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			StringBuffer sb = new StringBuffer();
			
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			    System.out.println(line);
			}
			
			
			process.waitFor();
			System.out.println("Executed");
			process.destroy();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
			
		}
		
		
	}	
	
	public static void executeCommandForce2(String command) {
		
	}
	
	public static void executeCommandForce2(String command,boolean display) {
		System.out.println(">" + command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				if (display)
					System.out.println(line);
			}
			
			process.waitFor();
			System.out.println("Executed");
			//process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void executeCommandForceIS(String command) {
		System.out.println(">" + command);
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			
			while ((line = reader.readLine()) != null)
			    System.out.println(line);
			
			process.waitFor();
			System.out.println("Executed");
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static String printConsole(Process process) {
		StringBuffer uberbuffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			System.out.println("------");
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			System.out.println(sb);
			uberbuffer.append(sb.toString());

			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			line = br.readLine();
			sb = new StringBuffer();

			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			System.out.println(sb);
			uberbuffer.append(sb.toString());
			System.out.println("------");
		} catch (Exception e) {
			System.out.println("---END BY PROCESS INTERRUPTION---");
			e.printStackTrace();
		}

		return uberbuffer.toString();
	}

	public static String getConsole(Process process) {
		StringBuffer uberbuffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			//System.out.println("------");
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			//System.out.println(sb);
			uberbuffer.append(sb.toString());

			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			line = br.readLine();
			sb = new StringBuffer();

			while (line != null) {
				line = br.readLine();
				sb.append(line + "\n");
			}
			//System.out.println(sb);
			uberbuffer.append(sb.toString());
			//System.out.println("------");
		} catch (Exception e) {
			//System.out.println("---END BY PROCESS INTERRUPTION---");
			e.printStackTrace();
		}

		return uberbuffer.toString();
	}
	

	public static String executeOSCommands(String[] commands)
			{

		try {
			Process process = Runtime.getRuntime().exec(commands[0]);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			for (String command : commands) {
				//System.out.println(">>" + command);
				bw.write(command + "\n");
			}
			bw.close();
			//return printConsole(process);
			return getConsole(process);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	


}
