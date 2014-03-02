package rrsimulation;

import java.util.ArrayList;
import java.util.List;

public class NewProcessTemporaryList {
	// lista pou periexei tis nees diergasies
	private List<Process> processList;
	int counter; // tha metra poses diergasies exoun dimiourgithei mexri stigmis
	public NewProcessTemporaryList() {
		counter = 0;
		processList = new ArrayList<Process>();
	}
	public int getCounter(){
		return counter;
	}
	public void increaseCounter(){
		counter++;
	}
	public boolean isEmpty(){
		return processList.isEmpty();
	}
	public void addNewProcess(Process process) {
		processList.add(process);
	}
	// epistrefei tin prwto stoixeio apo ti lista
	public Process getFirst() {
		return processList.remove(0);
	}
	public void printList() {
		for (Process p : processList){
			System.out.println(p);
		}
	}
	/* tha vriskei diergasies tis ouras pou exoun xrono afiksis (se xtupous rologiou)
	 * ton xrono pou me endiaferei
	 */
	public List<Process> getProcessesAtCurrentTime(int time){
		List<Process> list = new ArrayList<Process>();
		for (Process p : processList){
			if(p.getArrivalTime()==time) {
				list.add(p);
			}
		}
		processList.removeAll(list);
		return list;
	}
}
