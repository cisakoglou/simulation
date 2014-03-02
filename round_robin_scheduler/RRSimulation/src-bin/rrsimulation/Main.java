package rrsimulation;

import java.util.List;

public class Main {
	// --constants
	final static int QUANTUM = 21;
	final static int SIMTIME = 6; // oi xtupoi arxizoun na metran apo 0 px an einai 6 tha treksei apo 0 mexri kai 5
	final static int NNEW = 2;
	final static boolean READFILE = true;
	// --static antikeimena klasewn mou 
	static Clock clock = new Clock();
	static NewProcessTemporaryList queueNew = new NewProcessTemporaryList();
	static ReadyProcessesList queueReady = new ReadyProcessesList();
	static ProcessGenerator proGen = new ProcessGenerator("input.txt", READFILE);
	static CPU cpu = new CPU();
	static Statistics stats = new Statistics("output.txt");
	static RRScheduler rr = new RRScheduler(QUANTUM);
 
	public static boolean newAllFinished(){
		// tha elegxei an einai adeia i new, ready kai i cpu
		// prokeimenou na dw an oles oi diergasies pou dimiourgithikan 
		// exoun dromologithei kai termatisei
		return ((cpu.isEmpty())&&(queueReady.isEmpty())&&(queueNew.isEmpty()));
	}

	public static void main(String[] args) {
		List<Process> temp; // i sillogi pou tha travaei apo new gia na valei se ready

		// sinthiki etsi wste akoma ki otan oloklirwthoun oi apaitoumenoi xtupoi pou trexei i prosomoiwsi(xtupoi kata tous opoious leitourgei o process generator)
		// na prepei prwta na teleiwsoun oi diergasies pou exoun dimiourgithei
		while((clock.ShowTime()< SIMTIME)||(!newAllFinished())){
			// --- gennitria diergasiwn----
			if ((clock.ShowTime()<SIMTIME)&&(proGen.getFlag())&&(proGen.getNextGen() == clock.ShowTime())){
				// dimourgei toses fores oses diergasies tha paragontai se kathe energopoihsh ths
				for (int i=0;i<NNEW;i++){
					proGen.createProcess();
				}
				proGen.setNextGen(); 
			}
			// ------pseudo-makroprothesmos dromologitis
			temp = queueNew.getProcessesAtCurrentTime(clock.ShowTime()); // travaei opoies diergasies exoun dimiorgithei sti new 
			// kai exoun idio xrono afiksis me ton trexwn xtupo 
			if (temp != null){ // pernaei oses vrike - an vrike - sti ready
				for (Process p : temp) {
					queueReady.addProcess(p);
				}
			}
			// elegxei an einai o xronos gia na pragmatopoihthei context switch i oxi 
			if (clock.ShowTime() == cpu.getTimeToNextContextSwitch()){
				// adeiazei ti cpu 
				if (!cpu.isEmpty()){//((clock.ShowTime() != 0)&&(!cpu.isEmpty())){
					rr.emptyCPU();
				}
				// an i ready oura einai adeia aplws tha auksanei ton xrono pou tha ginei i epomeni enallagi 
				// etsi wste na mi xathei i roi tis dromologisis
				if (queueReady.isEmpty()){
					cpu.increaseTimeToNextContextSwitch();
				}
				// diaforetika dromologei th diergasia poy vrisketai stin arxi ths ready ouras
				else{
					rr.RR();	
				}
			}
			// afou perasw oses xreiazetai apo new sti ready
			// kai afou vgalw apo ti ready auti pou tha dromologithei
			// auksanw to xrono anamonis gia kathe diergasia pou vrisketai sti ready
			queueReady.increaseWaitingTimes();
			// auksana an xreiazetai ton arithmo toy megistou gia tin oura dromologisis
			stats.UpdateMaximumListLength();
			// se kathe periptwsi ekteleitai i diergasia pou uparxei sti cpu
			cpu.execute();
			// epomenos xtupos
			clock.Time_Run();
			// tupwnei se kathe xtupo pou vriskontai oi diergasies 
			System.out.println("[new]");
			queueNew.printList();
			System.out.println("[ready]");
			queueReady.printList();
			System.out.println("[cpu]");
			cpu.printProcess();
			System.out.println("------next clock------"+clock.ShowTime());
		}
		stats.WriteStatistics2File();
	}
}
