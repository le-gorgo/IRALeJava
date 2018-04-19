package learning.actionLearning.irale;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

import learning.ExampleMemory;
import learning.actionLearning.Example_AL;


public class Subs {
	public HashMap<Term,Term> li;
	public HashMap<Term,Term> revLi;
	public HashMap<String,Term> nameLi;
	public HashMap<String,Term> nameRev;
	
	
public Subs(){
	li=new HashMap<Term,Term>();
	revLi=new HashMap<Term,Term>();
	nameLi=new HashMap<String,Term>();
	nameRev=new HashMap<String,Term>();
}
	
public void add(Term t1,Term t2){
		nameLi.put(t1.name, t1);
		nameRev.put(t2.name, t2);
		li.put(t1, t2);
		revLi.put(t2, t1);

}

public Term find(Term t){
	if (li.containsKey(t)) {
		return li.get(t);
	}else{return null;}
}

public Term find(String t){
	if (nameLi.containsKey(t)) {
		return li.get(nameLi.get(t));
	}else{return null;}
}
public Term revfind(Term t){
	if (revLi.containsKey(t)) {
		return revLi.get(t);
	}else{return null;}
}

public Term revfind(String t){
	//System.out.println(nameRev+" revLi:"+revLi+" t:"+t);
	if (nameRev.containsKey(t)) {
		return revLi.get(nameRev.get(t));
	}else{return null;}
}


public Term in(Term t){
	Term ttt=null;
	Term tt=this.find(t);
	if (tt!=null){
		ttt=this.revLi.get(tt);}
return ttt;}

public int couv(Subs s){
	//incomp 0, tCs&sCt=1, tCs=2, sCt=3, vide=4
	Iterator<Entry<Term,Term>> itS=s.li.entrySet().iterator();
	int nbT=this.li.size();
	boolean sCt=true,tCs=true;
	while (itS.hasNext()){
		Entry<Term,Term> sE1=itS.next();
		Term tsk=sE1.getKey();
		
		if (!this.nameLi.containsKey(tsk.name)){
			tCs=false;}
		else{nbT--;
			if(!s.find(tsk.name).name.equals(this.find(tsk.name).name) || !s.find(tsk.name).type.equals(this.find(tsk.name).type)){
				return 0;}
		}		
	}
	if (nbT>0){sCt=false;}
	if (tCs && sCt){return 1;}
	if (tCs){return 2;}
	if (sCt){return 3;}
return 4;}

public void print(){
	Iterator<Entry<Term,Term>> itS=this.li.entrySet().iterator();
	System.out.print(" {");
	while(itS.hasNext()){
		Entry<Term,Term> e=itS.next();
		System.out.print(" "+e.getKey().name+" ("+e.getKey().type+") / "+e.getValue().name+" ("+e.getValue().type+") ");
	}
	System.out.print(" }");	}

public static void llprint(LinkedList<Subs> s){
	System.out.println();
	System.out.println("Substitutions :");
	if(s==null){return;}
	ListIterator<Subs> ss=s.listIterator();
	
	while (ss.hasNext()){
		ss.next().print();
		
	}
}

public Subs copie(){
	Subs ret=new Subs();
	ret.li.putAll(this.li);
	ret.revLi.putAll(this.revLi);
	ret.nameLi.putAll(this.nameLi);
	ret.nameRev.putAll(this.nameRev);
	return ret;}


public Subs inter(Subs s){
	Subs ret=new Subs();
	Iterator<Entry<Term,Term>> itts=this.li.entrySet().iterator();
	while (itts.hasNext()){
		Entry<Term,Term> en=itts.next();
		Term tt1=en.getKey();
		Term st1=s.find(tt1.name);
		if(st1==null){continue;}
		Term tt2=en.getValue();
		if(!st1.equals(tt2)){continue;}
		ret.add(tt1, tt2);}
return ret;}

public boolean egal2(Subs s){
	Iterator<Entry<Term, Term>> it=this.li.entrySet().iterator();
	HashMap<Term,Term> s2=(HashMap<Term, Term>) s.li.clone();
	while(it.hasNext()){
		Entry<Term,Term> e=it.next();
		this.print();
		s.print();
		if (!s2.containsKey(e.getKey())){return false;}
		if (!s2.get(e.getKey().name).name.equals(e.getValue().name) ||!s2.get(e.getKey().name).type.equals(e.getValue().type))
		{return false;}
		s2.remove(e.getKey());}
	if(s2.isEmpty()){return true;}
	return false;}

public boolean egal(Subs s){
	if(s==null){return false;}
	Iterator<Entry<Term, Term>> it=this.li.entrySet().iterator();
	HashMap<Term,Term> s2=(HashMap<Term, Term>) s.li;
	int elemVerifS2=0;
	while(it.hasNext()){
		Entry<Term,Term> e=it.next();
	//
	//	System.out.println("Subs - egal - les deux Subs ");s.print();		System.out.println("");this.print();		System.out.println("");
	//
		if (!s2.containsKey(e.getKey())){return false;}
	//	System.out.println(s2.get(e.getKey()).name);
	//	System.out.println(e.getValue().name);
		if (!s2.get(e.getKey()).name.equals(e.getValue().name)){return false;}
		if (!s2.get(e.getKey()).type.equals(e.getValue().type)){return false;}
		elemVerifS2++;
	//	System.out.println("élément vérifier dans S2 :"+elemVerifS2);
		}
	if(elemVerifS2==s2.size()){return true;}
	return false;}

public boolean aToa(){
	Iterator<Entry<Term, Term>> it=this.li.entrySet().iterator();
	//System.out.println();
	while(it.hasNext()){
		Entry<Term,Term> t=it.next();
		
	//	System.out.print(" "+t.getKey().name+" et "+t.getValue().name);
		if(!t.getKey().name.equals(t.getValue().name) ||(t.getKey().type!=t.getValue().type)){return false;}
	}
return true;}

public AtomSet check(LinkedList<Example_AL> contr_ex,AtomSet ind, Example_AL ex,Rule ru) {
	Iterator<Entry<String, LinkedList<Atom>>> r=ru.s.atoms.entrySet().iterator();
	AtomSet newAT=new AtomSet();
	while(r.hasNext()){
		Entry<String, LinkedList<Atom>> enr=r.next();
		ListIterator<Atom> arl = enr.getValue().listIterator();
		if(ex.s.atoms.containsKey(enr.getKey())){
		while(arl.hasNext()){
			Atom ar=arl.next();
			Atom aa=ar.toVarPru(this);
				ListIterator<Atom> aexl = ex.s.atoms.get(enr.getKey()).listIterator();
				while(aexl.hasNext()){
					Atom ae=aexl.next().toVarP(this);
					if (aa!=null && ae!=null && aa.egal(ae)){
						newAT.addAtom(aa);break;}
				}
			}
		}
	}
	ListIterator<Example_AL> itEx;Example_AL exc =null;
	if (contr_ex.isEmpty()){itEx=new LinkedList<Example_AL>().listIterator();}
	else {itEx=((AbstractList<Example_AL>) contr_ex.clone()).listIterator();}
	Subs aSig;
	while(itEx.hasNext()){
		exc=itEx.next();
		aSig=ru.aCouv(exc, new Subs());
		if(aSig != null && ru.e.couv(exc.e, aSig)==null){
			if (newAT.couvPart(exc.s, aSig)!=null){
				return null;}}
	}
	if(ind.couvPart(newAT, new Subs())==null){return null;}
	
	return newAT;
}


@Override
public String toString(){
	if (this==null){return "null";}
	String res="{";
	Iterator<Entry<Term, Term>> it=this.li.entrySet().iterator();
	while(it.hasNext()){
		Entry<Term, Term> e=it.next();
		res=res.concat("("+e.getKey().name+"/"+e.getValue().name+")");
	}
	return res.concat("}");
}

}
