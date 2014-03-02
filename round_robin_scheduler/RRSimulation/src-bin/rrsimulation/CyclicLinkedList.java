package rrsimulation;

import java.util.Iterator;

public class CyclicLinkedList implements Iterable<Process>{
	
	private Process head;
	private Process tail;
	int size;
	
	public CyclicLinkedList(){
		head=null;tail=null;size=0;
	}
	/* tha prosthetei neo komvo-diergasia sto telos tis listas-kai to neo telos tha deixnei kathe
	 * fora stin arxi
	 */
	public void addNodeProcess(Process p){
		if(size==0){
			head = p;
			tail=head;
		}else{
			p.setNext(head);
			tail.setNext(p);
			tail=p;
		}
		size++;
	}
	/* tha pairnei tin arxi ths listas kai tautoxrona tha ti diagrafei*/
	public Process getHead(){
		if (size != 0){
			Process temp = head;
			head=head.getNext();
			tail.setNext(head);
			size--;
			return temp;
		}
		return null;
	}
	public int getSize(){
		return size;
	}
	@Override
	public Iterator<Process> iterator() {
		return new CyclicLinkedListIterator(head,size);
	}
}
