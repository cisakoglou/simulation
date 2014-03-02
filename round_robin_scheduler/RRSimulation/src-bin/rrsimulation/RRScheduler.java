package rrsimulation;

/*
 * dromologei diergasies metaksu ready ouras kai cpu
 * kanei tis metatropes apo tin mia katastasi stin alli gia tin kathe diergasia
 * analoga me tin oura stin opoia metaferetai 
 * adeieazei cpu pernwntas ti diergasia pou vgazei eite ek neou sti ready oura eite ti vgazei apo to susthma 
 * kathws exei teleiwsei kai gia xrisi twn statistikwn tis perna sto static antikeimeno ths Statistics sti main 
 */
public class RRScheduler {
	// kvanto
	private int quantum;
	RRScheduler(int quantum) {
		this.quantum = quantum;
	}
	public void emptyCPU(){
		Process temp = Main.cpu.removeProcessFromCpu();
		// elegxw an i diergasia exei oloklirwthei kai an nai thetw to status tis se terimanated
		if (temp.getCpuRemainingTime() == 0) {
			temp.setProcessState(3);
			temp.setTurnaround(Main.clock.ShowTime()); // orizw to xrono epistrofi ths me vasi ton xrono pou teleiwse
			// thn apothikeuw sti lista diergasiwn gia ta statistika
			Main.stats.addTerminatedProcess(temp);
		}
		// diaforetika prepei na epistrepsei sti ready oura
		else{
			addProcessToReadyList(temp);
		}
		
	}
	
	/*
	 * topothetei ti diergasia stin katallili thesi tis ouras (ousiastika to na vrei tin katallili thesi 
	 * ulopoieitai xari ston tropo ulopoihshs tis domis )
	 * trexei otan i diergasia apo ti cpu gurna sti ready epeidi den exei oloklirwthei
	 */
	public void addProcessToReadyList(Process process) {
		// thetw to status tis se ready afou ksanagurna sti ready oura
		process.setProcessState(1);
		Main.queueReady.addProcess(process);
	}

	/*
	 * ektelei to context switching
	 */
	public void RR() {
		// se periptwsi pou duo h parapanw diergasies pou vriskontai sti ready
		// exoun idio xrono afiksis de tha dimiourgithei provlima
		// o dromologitis tha parei auti pou tha vrei prwta kai pali sti cpu tha vrisketai mono mia diergasia
		// kai tha einai auti me mikrotero pid
		Process process = Main.queueReady.getProcessToRunInCPU(); 
		if (process != null) {
			process.setProcessState(2); //pernaei se katastasi running
			// se periptwsi pou o xronos apokrisis tis diergasias den exei allaksei apo tin arxikopoihmenh tou timi 0
			// simainei oti einai i prwti fora pou mpainei sti cpu
			// kai etsi gia ton upologismo xronou apokrisis tha xrisimopoihthei o trexwn xronos rologiou
			if (!process.getCheckedResponseTime()) {
				process.setResponseTime(Main.clock.ShowTime());
				process.checkResponseTime();
			}
			Main.cpu.addProcess(process);
			// pote tha sumvei to epomeno context switch
			// eksartatai apo to xrono cpu tis diergasias pou irthe
			// an einai megaluteros apo to kvanto i enallagi tha ginei otan teleiwsei to kvanto
			if (process.getCpuRemainingTime() > quantum){
				Main.cpu.setTimeToNextContextSwitch(Main.clock.ShowTime()+quantum);
			}
			// an einai mikroteros tha ektelestei oso kai autos o xronos 
			else{
				Main.cpu.setTimeToNextContextSwitch(Main.clock.ShowTime()+process.getCpuRemainingTime());
			}
		}
	}
}
