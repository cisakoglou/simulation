package rrsimulation;

public class Clock {
	// arithmos xtupwn pou exoun parelthei
	protected static int ticks;
	public Clock() {
		ticks = 0;
	}
	public void Time_Run() {
		ticks++;
	}
	public int ShowTime() {
		return ticks;
	}
}
