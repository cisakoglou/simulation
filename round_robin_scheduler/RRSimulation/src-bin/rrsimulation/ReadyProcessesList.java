package rrsimulation;

public class ReadyProcessesList {
	// kukliki lista poy periexei tis etoimes diergasies
	private CyclicLinkedList processList;
	public ReadyProcessesList() {
		processList = new CyclicLinkedList();
	}
	public int getSize(){
		return processList.getSize();
	}
	public void addProcess(Process item) {
		processList.addNodeProcess(item);
		item.setProcessState(1);
	}
	 // otan pairnei diergasia apo kefali ouras automata tha ti diagrafei apo autin
	 // giati thelw ekei na paramenoun mono diergasies me status ready kai oxi running
	 // sti sinexeia i diergasia mporei na epanerxetai sti lista alla sto telos tis
	public Process getProcessToRunInCPU() {
		return processList.getHead();
	}
	public void printList() {
		for (Process p : processList){
			System.out.println(p);
		}
	}
	public boolean isEmpty(){
		return (processList.getSize()==0);
	}
	public void increaseWaitingTimes(){
		for (Process p : processList){
			p.increaseWaitingTime();
		}
	}
}
