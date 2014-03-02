package rrsimulation;

import java.util.Iterator;

public class CyclicLinkedListIterator implements Iterator<Process>{
	private Process current;
	int totalSize;
	int currentSize;
	
	public CyclicLinkedListIterator(Process head,int s){
		current = head;
		totalSize = s; currentSize = 0;
	}
	@Override
	public boolean hasNext() {
		if (totalSize!=0) return (currentSize<totalSize);
		return false;
	}

	@Override
	public Process next() {
		if (hasNext()){
			currentSize++;
			Process process = current;
			current = current.getNext(); // mporei na einai null
			return process;
		} // den uparxei next process
		throw new java.util.NoSuchElementException("linked list.next");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException
        ("Linked list iterator remove not supported");
		
	}
	

}
