package learning.actionLearning.irale;

import java.util.LinkedList;
import java.util.ListIterator;

public class ActScore {
	public Atom act;
	public int[] score;
	
	public ActScore(Atom act,int nbF){
		this.act=act;
		score=new int[nbF];
	}
	
	public static void llprint(LinkedList<ActScore> ll){
		ListIterator<ActScore> li=ll.listIterator();
		System.out.println();
		while(li.hasNext()){
			ActScore acs=li.next();
			System.out.print("Action :");acs.act.print();System.out.print(" Score :");
			int i=1,j=acs.score.length;
			System.out.print("[");
			while(i<=j){
				if(i==j){System.out.print(acs.score[i-1]+"]");}
				else{System.out.print(acs.score[i-1]+",");}
				i++;}
			System.out.println();
			
		}
	}

}
