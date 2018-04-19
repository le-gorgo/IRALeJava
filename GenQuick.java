package learning.actionLearning.irale;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import learning.ExampleMemory;
import learning.actionLearning.Example_AL;

public class GenQuick extends Gen{
	
	//generalisation d'irale qui se sert de la generalisation version quick d'atomset.
	//La generalisation version quick d'atomset cherche la première generalisation qui ne couvre pas de contre-exemple négatif.
	//C'est une réccurcion proche de celle de prolog qui limite les recopiages inutils.
	Model mod;
	
	public GenQuick(Model mod){
		this.mod=mod;
	}
	
	public LinkedList<Rule> gen(Example_AL ex,int ag, HashMap<Rule,LinkedList<Rule[]>> rules,ExampleMemory<Example_AL> contr_ex,AtomSet ind){
		int c=0;
		LinkedList<Rule> res=new LinkedList<Rule>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> liru = ((AbstractMap<Rule,LinkedList<Rule[]>>) rules.clone()).entrySet().iterator();
		Rule ru=null;
		Subs sig = null;
		Rule newR = null;
		while (liru.hasNext()){
			sig=new Subs();
			Entry<Rule, LinkedList<Rule[]>> enr=liru.next();
			ru=enr.getKey();
			sig=ru.aeCouv(ex, sig);
			if (sig!=null){
				Effect e=new Effect(ru.e.add.toVarPru(sig),ru.e.del.toVarPru(sig));
				newR=new Rule("R_"+Global.getContR(),new AtomSet(),ru.a.toVarPru(sig),e);
				
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
	

}
