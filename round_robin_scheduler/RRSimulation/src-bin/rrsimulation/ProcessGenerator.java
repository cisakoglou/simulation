package rrsimulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * dimiourgei tis kainouries diergasies pou mpainoun sti new
 * mia ti fora (oli i klasi vlepei ti diergasia pou dimiourgeitai)
 * pou simainei oti tha kaleitai apo main toses fores oses kai oi diergasies pou thelw na dimiourgisw
 * oi diergasies pou dimiourgountai exoun pseudotuxaious xronous
 * arrival - me orio tin epomeni stigmi pou tha klithei o generator
 * cpu - me orio mia stathera
 */
public class ProcessGenerator {
	// -- constants
	final static int INTERVAL = 2; // diastima ana to opoio tha dimiourgountai nees diergasies - antistoixei se xtupous rologiou
	final static int CPUEXCLUSIVE = 20;
	// gia eisagwgi pliroforiwn gia diergasies i eksagwgi gia autes analoga me to mode tou process generator
	private File inputFile;
	BufferedWriter bw;
	BufferedReader br;
	Process newProcess;
	int nextGen;// epomenos xtupos rologiou pou tha energopoihthei o generator gia na dimiourgisei tis epomenes diergasies
	boolean flag; // true otan tha dimourgei ana xtupous / false otan tha dimourgei new apo arxeio
	// an readFile == false dimiourgei to arxeio inputFile gia na apothikeusei
	// alliws gia anagnwsi
	public ProcessGenerator(String filename, boolean readFile) {
		nextGen = 0;
		newProcess = null; bw = null; br = null;
		if (!readFile){ // tha grapsei sto arxeio 
			flag = true;
		try{
			 inputFile = new File(filename);
			 if (inputFile.exists()) {  // an uparxei to arxeio to svinei kai dimourgei kainourio
					inputFile.delete();
					inputFile.createNewFile();
			 }
			 else inputFile.createNewFile(); // an den uparxei to dimiourgei
		} catch (IOException e){
				e.printStackTrace();
		}
		}
		else {  // tha kanei parse to arxeio kai tha exei apeutheias tis diergasies pou tha treksoun kata ti diarkeia tis prosomoiwsis
			flag = false;
			inputFile = new File(filename);
			try {
				parseProcessFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	public boolean getFlag(){
		return flag;
	}
	public int getNextGen(){
		return nextGen;
	}
	public void setNextGen(){
		nextGen += INTERVAL;
	}

	// dimiourgia tis diergasias 
	public Process createProcess() {
		int pid = Main.queueNew.getCounter()+1;
		int cpuTime = 1+(new Random()).nextInt(CPUEXCLUSIVE); // 0<=cpuTime<INTERVAL
		int arrivalTime = Main.clock.ShowTime()+(new Random()).nextInt(INTERVAL); 
		newProcess = new Process(pid,arrivalTime,cpuTime);
		// to status antikeimenwn tis klasis Process outws i allws arxikopoieitai se new
		// tin pernaw stin oura new
		Main.queueNew.addNewProcess(newProcess);
		Main.queueNew.increaseCounter();
		// ti grafw sto arxeio
		storeProcessToFile();
		return newProcess;
	}

	// apothikeusi tis diergasias pou dimiourgeitai sto arxeio - mia ti fora 
	// gi auto kai oli i klasi vlepei ti diergasia pou dimiourgeitai
	private void storeProcessToFile() {
		try{
			bw = new BufferedWriter(new FileWriter(inputFile,true)); // to true einai gia na kanei append kai na mi diagrafei tis uparxouses diergasies
			bw.write(newProcess.toString()+"\r\n");
			bw.flush();
			bw.close();
		}catch(IOException e){			
			e.printStackTrace();
		}

	}

	/* anagnwsi twn diergasiwn apo arxeio
	 * etsi pws tha ta grafei etsi tha ta kanei kai parse gia na mporw na ksanatreksw to idio arxeio pou
	 * paragetai apo vasiki roi
	 * kai na vgazei ta idia apotelesmata sti deutereuousa
	 * */
	public List<Process> parseProcessFile() throws IOException {
		List<Process> parsedProcesses = new ArrayList<Process>();
		String s;
		String delims = "[, ]";
		String[] tokens; 
		Process temp;
		try {
			br = new BufferedReader(new FileReader(inputFile));
			while ((s=br.readLine()) != null){
				tokens = s.split(delims);
				temp = new Process(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[3]),Integer.parseInt(tokens[5]));
				parsedProcesses.add(temp);
				Main.queueNew.addNewProcess(temp);
				Main.queueNew.increaseCounter();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		return parsedProcesses;
		
	}

}
