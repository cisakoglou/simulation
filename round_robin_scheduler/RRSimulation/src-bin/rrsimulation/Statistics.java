package rrsimulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Statistics {
	// mesos xronos anamonis 
	private float averageWaitingTime;
	// sunolikos xronos anamonis
	private int totalWaitingTime;
	// mesos xronos apokrisis
	private int averageResponseTime;
	// o sunolikos xronos pou perimenan oles oi diergasies gia na mpoun prwti fora sti cpu (sunolikos xronos apokrisis)
	private int totalResponseTime; 
	// mesos xronos epistrofis 
	private float averageTurnaround;
	// to athroisma twn xronwn epistrofis olwn twn diergasiwn
	private int totalTurnaround;
	// trexon megisto mikos ouras ready - vathmos polluprogrammatismou
	private int maximumLengthOfReadyProcessList;
	// trexwn sunolikos arithmos diergasiwn 
	public int totalNumberOfProcesses;
	// arxeio pou apothikeuontai ta statistika dedomena
	private File outputFile;
	private BufferedWriter bw;
	// sullogi opou apothikeuontai oi listes pou termatizoun
	private List<Process> terminatedList;
	public Statistics(String filename) {
		terminatedList = new ArrayList<Process>();
		maximumLengthOfReadyProcessList = 0;
		totalNumberOfProcesses = 0;
		totalWaitingTime=0;totalResponseTime=0;totalTurnaround=0;
		averageWaitingTime=0;averageResponseTime=0;averageTurnaround=0;
		// anoigei arxeio
		try{
			 outputFile = new File(filename);
			 if (!outputFile.exists()) {  // an den uparxei to dimiourgei
					outputFile.createNewFile();
			 }
		} catch (IOException e){
				e.printStackTrace();
		}
		
	}
	public void addTerminatedProcess(Process process){
		terminatedList.add(process);
	}
	// elegxei to mikos ouras kai enimerwnei an einai aparaitito to megisto 
	public void UpdateMaximumListLength() {
		//Main.queueReady.setMaxDegree();
		if (Main.queueReady.getSize() > maximumLengthOfReadyProcessList){
			maximumLengthOfReadyProcessList = Main.queueReady.getSize();
		}
	}
	// upologismos meswn xronwn
	public String CalculateAverageWaitingTime() {
		for (Process p : terminatedList){
			totalWaitingTime += p.getWaitingTime();
		}
		averageWaitingTime = (float)totalWaitingTime/totalNumberOfProcesses;
		return "Average Waiting Time: "+averageWaitingTime;
	}
	public String CalculateAverageResponseTime(){
		for (Process p : terminatedList){
			totalResponseTime += p.getResponseTime();
		}
		averageResponseTime = (int)totalResponseTime/totalNumberOfProcesses;
		return "Average Response Time: "+averageResponseTime;
	}
	public String CalculateAverageTurnaroundTime(){
		for (Process p : terminatedList){
			totalTurnaround += p.getTurnaround();
		}
		averageTurnaround = (float)totalTurnaround/totalNumberOfProcesses;
		return "Average Turnaround: "+averageTurnaround;
	}
	
	// prosthetei mia nea grammi me ta trexonta statistika 
	public void WriteStatistics2File() {
		totalNumberOfProcesses = Main.queueNew.getCounter();
		try{
			bw = new BufferedWriter(new FileWriter(outputFile,true)); // to true einai gia na kanei append kai na mi diagrafei
			bw.write("Max Length of Ready Queue:"+maximumLengthOfReadyProcessList);
			bw.write(","+CalculateAverageWaitingTime());
			bw.write(","+CalculateAverageResponseTime());
			bw.write(","+CalculateAverageTurnaroundTime()+"\r\n");
			bw.flush();
			bw.close();
		}catch(IOException e){			
			e.printStackTrace();
		}
	}
}
