package learning.actionLearning.irale;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import smileagents.stats.Counter;
import learning.Duplicable;
import learning.ExampleMemory;
import learning.Hypothesis;
import learning.actionLearning.Example_AL;

public class Rule extends Example_AL implements Hypothesis<Example_AL> {
	
public Rule(String name,AtomSet s,Atom a,Effect e){
	super(name, s, a, e);
	}


public Rule(Example_AL ex) {
	// TODO Auto-generated constructor stub
	super(ex.name, ex.s, ex.a, ex.e);
}


public Subs aCouv(Example_AL ex,Subs sig){
	if(!this.a.name.equals(ex.a.name)){return null;}
	Subs newSig=ex.a.genVgen(this.a, sig);
return newSig;}



public Subs aCouvVc(Example_AL ex,Subs sig){
	if(!this.a.name.equals(ex.a.name)){return null;}
	Subs newSig=ex.a.genVcheck(this.a, sig);
return newSig;}

public boolean contrNeg(Example_AL ex){
	Subs sig=new Subs();
	Subs newsig=this.saCouv(ex,sig);
	if (newsig==null){return false;}
	sig=this.e.couv(ex.e, newsig);
	if(sig==null){return true;}
return false;}

public Rule rev(Subs sig){
	AtomSet exS=this.s.rev(sig);
	Atom exA=this.a.rev(sig);
	Effect exE=this.e.rev(sig);
	return new Rule("resol",exS,exA,exE);}

public Rule rev(Term[] v,Term[] c){
	AtomSet exS=this.s.rev(v,c);
	Atom exA=this.a.rev(v,c);
	Effect exE=this.e.rev(v,c);
	return new Rule("resol",exS,exA,exE);}

public Subs aeCouv(Example_AL ex,Subs sig){
	Subs newSig=this.aCouv(ex,sig);
	if (newSig!=null){
		newSig=this.e.couv(ex.e,newSig);}
return newSig;}

public Subs sCouv(Example_AL ex,Subs sig){
	Subs newSig=this.s.couvPart(ex.s, sig);
return newSig;}

public Subs saCouv(Example_AL ex,Subs sig){
	Subs newSig=aCouvVc(ex,sig);
	Subs neSig;
	if(newSig==null){return null;}
	else{neSig=sCouv(ex,newSig);}
return neSig;}



@Override
public int size() {
	// TODO Auto-generated method stub
	return 0;
}


@Override
public boolean isContradictedBy(Example_AL example, Counter<Long> cl) {
		if(this.contrNeg((Example_AL) example)){
			return true;}
	return false;
}

public boolean getCritique(ExampleMemory<Example_AL> exMem,
		List<Example_AL> resCounterExamples, boolean firstCEonly, Counter<Long> cl) {
	for (Example_AL ex : exMem.getAllStoredExamples()) {
		if (isContradictedBy(ex,cl)) {
			resCounterExamples.add(ex);
			if (firstCEonly)
				return false;
		}
	}
	return resCounterExamples.isEmpty();
}


@Override
public boolean predictsCorrectly(Example_AL example,
		Counter<Long> statsNbOperations) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public Duplicable copy() {
	// TODO Auto-generated method stub
	Effect e=new Effect(new AtomSet(this.e.add),new AtomSet(this.e.del));
	return new Rule(this.name,new AtomSet(this.s),new Atom(this.a),e);
}


public static void llprint(LinkedList<Rule> rules) {
	// TODO Auto-generated method stub
	ListIterator lit=rules.listIterator();
	while(lit.hasNext()){
		((Example_AL) lit.next()).print();
	}
}

}
