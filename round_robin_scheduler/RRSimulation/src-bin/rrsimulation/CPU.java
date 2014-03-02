package rrsimulation;

public class CPU {
	private Process runningProcess;
	// xroniki stigmi epomenis diakopis
	private int timeToNextContextSwitch;
	// xroniki stigmi enarksis teleutaias diergasias
	private int lastProcessStartTime;
	public CPU() {
		runningProcess = null; timeToNextContextSwitch = 0; lastProcessStartTime = 0;
	}

	// eisagwgi tis diergasias pros ektelesi sti cpu
	public void addProcess(Process process) {
		runningProcess = process;
		setLastProcessStartTime(Main.clock.ShowTime());
	}
	public int getTimeToNextContextSwitch(){
		return timeToNextContextSwitch;
	}
	public void setTimeToNextContextSwitch(int nextSwitch){
		timeToNextContextSwitch = nextSwitch;
	}
	// se periptwsi pou den uparxei kamia diergasia sti ready 
	// tha prepei na auksanei kata 1
	public void increaseTimeToNextContextSwitch(){
		timeToNextContextSwitch++;
	}
	public int getLastProcessStartTime(){
		return lastProcessStartTime;
	}
	public void setLastProcessStartTime(int lastTime){
		lastProcessStartTime = lastTime;
	}
	// kaleitai apo dromologiti gia na eksagei ti diergasia pou vrisketai sti cpu ekeini ti stigmi
	public Process removeProcessFromCpu() {
		Process temp = runningProcess;
		runningProcess = null;
		return temp;
	}
	public boolean isEmpty(){
		return (runningProcess == null);
	}
	
	 // i cpu remaining time tis diergasias tha meiwnetai kata ena 
	 // i sunartisi tha ekteleitai apo main gia kathe xtupo
	public void execute() {
		// elegxos g=gia tin periptwsi pou den exei dromologithei kamia diergasia
		// (oi mexri twra diergasies oloklirwthikan kai den exei ftasei kapoia kainouria)
		if (!isEmpty()){
			runningProcess.executeProcess();
		}
	}
	public void printProcess(){
		System.out.println(runningProcess);
	}
}
