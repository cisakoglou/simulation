package rrsimulation;

//antistoixei sto pcb kathe diergasias
public class Process {
	private Process next; // deiktis sto epomeno PCB - xrisimeuei sti dhmiourgia kuklikis listas ready
	private int pid;
	// ----- 2 vasika stoixeia pou paragontai apo gennitria me pseudotuxaio tropo
	// xroniki stigmi afiksis diergasias sto sistima
	private int arrivalTime;
	// xronos cpu
	private int cpuTotalTime;
	// enapomeinantas xronos cpu
	private int cpuRemainingTime;
	// --- 3 stoixeia pou xrisimeuoun apokleistika gia accounting ----
	private int waitingTime;
	private int responseTime;
	private boolean checkedResponseTime; // o xronos apokrisis tha orizetai tin prwti fora pou tha ekteleitai i diergasia kai tote to flag auto tha tithetai true gia na min ksanapeiraxtei o xronos 
	/* xronos epistrofis-oloklirwsis einai to athroisma xronwn pou i diergasia perimenei gia na
	 * mpei sti mnimi,perimenei sti ready,ekteleitai stin kme kai pragmatopoiei i/o
	 * edw i anamoni ws new tha einai 0 (afou akrivws kata to xrono afiksis dromologeitai sti mnimi-
	 * den ulopoiw pio eksipno makroprothesmo dromologiti - oute kai uparxei orio sto vathmo poluprogrammatismou
	 * ) kai i i/o epeksergasia tha einai kai pali 0 afou de dieukrinizetai kati sxetika me auto
	 *
	 */
	private int turnaround; 
	// katastasi diergasias
	 // 0-created/new,1-ready/waiting, 2-running,3-terminated
	private int currentState;
	public Process(int pid, int arrivalTime, int cpuBurstTime) {
		this.arrivalTime = arrivalTime;
		this.cpuTotalTime = cpuBurstTime;
		cpuRemainingTime = cpuBurstTime;
		this.pid=pid;
		currentState = 0; // otan tha dimiourgeitai prwti fora tha mpainei sti new oura ara status:new
		next=null;
		waitingTime=0;
		responseTime=0;
		checkedResponseTime=false;
		turnaround=0;
	}
	public void setNext(Process p){
		next = p;
	}
	public Process getNext(){
		return next;
	}
	public int getPid(){
		return pid;
	}
	public int getArrivalTime(){
		return arrivalTime;
	}
	public int getCpuRemainingTime(){
		return cpuRemainingTime;
	}
	public void checkResponseTime(){
		checkedResponseTime = true;
	}
	public boolean getCheckedResponseTime(){
		return checkedResponseTime;
	}
	// tha trexei se kathe xtupo gia oses diergasies vriskontai stin oura ready
	public void increaseWaitingTime(){
		waitingTime++;
	}
	public int getWaitingTime(){
		return waitingTime;
	}
	// upologizei ton xrono apokrisis tis diergasias
	// tha kaleitai mono mia fora gia kathe diergasia
	// ousiastika upologizetai o xronos pou perna prwti fora stin oura ready
	// kathws o xronos pou perna stin oura new einai amelhteos
	// logw tou tropou ulopoihshs tou "makroprothesmou" dromologiti
	public void setResponseTime(int r){
		responseTime = r - arrivalTime;
	}
	public int getResponseTime(){
		return responseTime;
	}
	// upologizei xrono epistrofis me vasi to xrono ston opoio oloklirvthike i diergasia
	// xronos termatismou - xronos afiksis
	// antistoixei ston synoliko xrono anamonis tis diergasias kai to cpuburst tou
	public void setTurnaround(int end){
		turnaround = end - arrivalTime;
	}
	public int getTurnaround(){
		return turnaround;
	}
	public int setProcessState(int newState) {
		currentState = newState;
		return currentState;
	}
	public void changeCpuRemainingTime(int newCpuRemainingTime) {
		cpuRemainingTime = newCpuRemainingTime;
	}
	@Override
	public String toString(){
		return new String("PID: "+pid+",Arrival_Time: "+arrivalTime+",CPU_Time: "+cpuTotalTime);
	}
	public void executeProcess(){
		cpuRemainingTime--;
	}
}
