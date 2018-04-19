package learning.actionLearning.irale;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import learning.actionLearning.Example_AL;

public class RuleDec extends Rule{
	
	public LinkedList<AtomSet> possind;
	public LinkedList<Atom> ind;

	public RuleDec(String name, AtomSet s, Atom a, Effect e) {
		super(name, s, a, e);
		// TODO Auto-generated constructor stub
	possind=new LinkedList<AtomSet>();
	ind=new LinkedList<Atom>();
	}

	public RuleDec(Example_AL ex){
		super(ex);
		possind=new LinkedList<AtomSet>();
		ind=new LinkedList<Atom>();
	}

	public LinkedList<Atom> priveDe(AtomSet s) {
		// TODO Auto-generated method stub
		LinkedList<Atom> res=new LinkedList<Atom>();
		Iterator<LinkedList<Atom>> it=this.s.atoms.values().iterator();
		while(it.hasNext()){
			ListIterator<Atom> li=it.next().listIterator();
			while(li.hasNext()){
				Atom a=li.next();
				ListIterator<Atom> sli=s.atoms.get(a.name).listIterator();
				boolean b=true;
				while(sli.hasNext()){
					Atom sa=sli.next();
					if(sa.nouveau(a)){
						b=false;}
				}
				if(b){res.add(a);}
			}
		}
	return res;}

}
