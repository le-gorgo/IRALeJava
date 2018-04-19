package learning.actionLearning.irale;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

import learning.ExampleMemory;
import learning.actionLearning.Example_AL;

public class GenRule extends Gen{
	Model mod;
	
	public GenRule(Model mod){
		this.mod=mod;
	}
	
	/*Generalisation sur le même principe que genQuick a l'exeption qu'elle "bloque" l'atom d'action. 
	 * Ici, un contre-exemple positif est un exemple dont il y a une couverture mutuelle de l'atom action.
	 * Les autres exemples que la règle couvre sont des contres-exemples négatifs.
	*/
	public LinkedList<Rule> gen(Example_AL ex,int ag, HashMap<Rule,LinkedList<Rule[]>> rules,ExampleMemory<Example_AL> contr_ex,AtomSet ind){
		int c=0;
		LinkedList<Rule> res=new LinkedList<Rule>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> liru = ((AbstractMap<Rule,LinkedList<Rule[]>>) rules.clone()).entrySet().iterator();
		Rule ru=null;
		Subs sig = null;
		Rule newR = null;
		while (liru.hasNext()){
			sig=null;Subs beta=null;
			Entry<Rule, LinkedList<Rule[]>> enr=liru.next();
			ru=enr.getKey();
			if(ru.a.name.equals(ex.a.name)){
				sig=ru.a.genVcheck(ex.a, new Subs());
				beta=((Rule)ex).a.genVcheck(ru.a,new Subs());}
			if (sig!=null && beta!=null){
				Effect e=new Effect(ru.e.add,ru.e.del);
				newR=new Rule("R_"+Global.getContR(),new AtomSet(),ru.a,e);
				
				AtomSet liAS=ru.s.genVquick(ex,newR, (LinkedList<Example_AL>) contr_ex.getAllStoredExamples(),ind);
				
				
			/*	System.out.println();
				System.out.println("GenQuick - gen -");
				ru.print();
				ex.print();
				System.out.println();
				if(liAS!=null){System.out.println("GenQuick - gen - liAS");
				System.out.println();liAS.print();System.out.println();System.out.println();}
				else{System.out.println();System.out.println("GenQuick - gen - liAS == null");}*/
						
						
						
				if(liAS!=null){
		//			System.out.println();liAS.print();System.out.println();System.out.println();
						newR.s=liAS;
						c=1;
						if (!newR.s.egal(ru.s)){
							mod.contrexAdd(ex,contr_ex);
							Rule[] tru=new Rule[2];tru[0]=newR;tru[1]=new Rule(ex.name,ex.s,ex.a,ex.e);
							LinkedList<Rule[]> lru=(LinkedList<Rule[]>) rules.get(ru);
							lru.addFirst(tru);
							rules.put(newR, lru);
							rules.remove(ru);
							res.add(newR);
							mod.modifHyp=true;
							liru = rules.entrySet().iterator();}
							}
					}
			}
		if(c==0){
			Rule[] tru=new Rule[2]; 
			mod.contrexAdd(ex,contr_ex);
			Effect eRes=new Effect(ex.e.add,ex.e.del);
			Rule exR=new Rule("R_"+Global.getContR(),ex.s,ex.a,eRes);
			tru[0]=exR;tru[1]=new Rule(ex.name,ex.s,ex.a,ex.e);
			LinkedList<Rule[]> lru=new LinkedList<Rule[]>();
			lru.add(tru);
			rules.put(exR, lru);
			res.add(exR);
			mod.modifHyp=true;
			}
	return res;}
	
	
	
	
	
	
	
	
	
	
	
	
/*	@Override
	public LinkedList<Rule> gen(Example_AL ex,int ag, HashMap<Rule,LinkedList<Rule[]>> rules,ExampleMemory<Example_AL> contr_ex){
		int d=0,c=0;
		LinkedList<Rule> res=new LinkedList<Rule>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> liru = ((AbstractMap<Rule,LinkedList<Rule[]>>) rules.clone()).entrySet().iterator();
		Rule ru=null;
		Subs sig = null;
		Rule newR = null;
		Example_AL exc =null;
		while (liru.hasNext()){
			sig=new Subs();
			Entry<Rule, LinkedList<Rule[]>> enr=liru.next();
			ru=enr.getKey();
			sig=ru.a.genVcheck(ex.a, sig);
			Subs beta=((Rule)ex).a.genVcheck(ru.a,new Subs());
			if (sig!=null && beta!=null){
	//			beta.print();sig.print();
				Effect e=new Effect(ru.e.add,ru.e.del);
				newR=new Rule("R_"+Global.getContR(),new AtomSet(),ru.a,e);
				LinkedList<AtomSet> liAS=ru.s.gen(ex.s, sig, ag,true);
				AtomSet.llprintS(liAS);
				ListIterator<Example_AL> itEx;
				if (contr_ex.getNbExamples()>0){itEx=((AbstractList<Example_AL>) ((LinkedList<Example_AL>)contr_ex.getAllStoredExamples()).clone()).listIterator();}
				else {itEx=((AbstractList<Example_AL>) new LinkedList<Example_AL>().clone()).listIterator();}
				while(!liAS.isEmpty()){
					newR.s=liAS.removeFirst();
					if(!itEx.hasNext()){d=1;}
					while(itEx.hasNext()){
						exc=itEx.next();
	//					exc.print();
	//					newR.print();
						Subs aSig=newR.aCouv(exc, new Subs());
						Subs aeSig=newR.e.couv(exc.e, aSig);
						if(aSig != null && newR.s.couvPart(exc.s, aSig)!=null){
							if (aeSig==null){break;}
							LinkedList<AtomSet> liSig=newR.s.gen(exc.s, aeSig,ag,true);
							if(liSig.isEmpty()){
								break;}
							else {
								newR.s=liSig.removeFirst();
								while(!liSig.isEmpty()){liAS.addFirst(liSig.removeFirst());}}
								}
						if(!itEx.hasNext()){d=1;}}
					
					if(d==1){
						c=1;
						res.add(newR);
						if (!newR.s.egal(ru.s)){
							mod.contrexAdd(ex, contr_ex);
							Rule[] tru=new Rule[2];tru[0]=newR;tru[1]=new Rule(ex.name,ex.s,ex.a,ex.e);
							LinkedList<Rule[]> lru=(LinkedList<Rule[]>) rules.get(ru);
							lru.addFirst(tru);
							rules.put(newR, lru);
							rules.remove(ru);
							mod.modifHyp=true;
							liru = rules.entrySet().iterator();}
							d=0;break;}
					}
				}
			}
		if(c==0){
			Rule[] tru=new Rule[2];
			mod.contrexAdd(ex, contr_ex);
			Effect eRes=new Effect(ex.e.add,ex.e.del);
			Rule exR=new Rule("R_"+Global.getContR(),ex.s,ex.a,eRes);
			tru[0]=exR;tru[1]=new Rule(ex.name,ex.s,ex.a,ex.e);
			LinkedList<Rule[]> lru=new LinkedList<Rule[]>();
			lru.add(tru);
			rules.put(exR, lru);
			res.add(exR);
			mod.modifHyp=true;
			}
	return res;}*/
	
}
