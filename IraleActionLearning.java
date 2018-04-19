package learning.actionLearning.irale;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import smileagents.experimenter.MemorySelection;

import learning.ExampleMemory;
import learning.actionLearning.Example_AL;
import learning.actionLearning.environnement.Envi;

public class IraleActionLearning extends IraleClassic{
	
	public boolean modif;
	public boolean genCheck=false;
	public ExampleMemory<Example_AL> contr_ex1;
	public HashMap<Rule,LinkedList<Rule[]>> rules1;
	//public HashMap<Atom, >;
	public GenRule genR;
	public boolean neg;
	public LinkedList<ActScore> actS;
	public int[] val;
	
	public IraleActionLearning(Envi env, int owner, String typeGen,String typeGenPoss) {
		super(env, owner, typeGen);
		// TODO Auto-generated constructor stub
		genR=new GenRule(this);
		if(typeGenPoss.equals("vFull")){
			genCheck=true;}
		actS=new LinkedList<ActScore>();
		val=new int[3];
		Arrays.fill(val,10);
	}

	@Override
	public synchronized LinkedList<Rule> irale(Example_AL ex, int ag,boolean neg) {
		this.neg=neg;
		super.irale(ex, ag,this.neg);
		this.majRuDec(ex);
		//this.print();
		
	//	this.print();
	//	this.print2();
	//	this.env.etat.print();
	//	ActScore.llprint(actS);
		return null;
		}
	
	public void majRuDec(Example_AL ex){
		
		//Premiere partie recopiage des règles.
		HashMap<String,RuleDec> ruD=new HashMap<String,RuleDec>();
		HashSet<String> ruMod=new HashSet<String>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> it=this.getRule().entrySet().iterator();
		while(it.hasNext()){
			Entry<Rule, LinkedList<Rule[]>> ent=it.next();
			if(ruDec.containsKey(ent.getKey().name)){
				ruD.put(ent.getKey().name, ruDec.get(ent.getKey().name));}
			else{
				RuleDec nRuD=new RuleDec(ent.getKey());
				if(ent.getValue().getFirst()[1].name.equals(ex.name))
					{String nameLR=null;
					if(ent.getValue().size()>1){
						nameLR=ent.getValue().get(1)[0].name;}
					if(nameLR!=null && ruDec.containsKey(nameLR)){
						nRuD.possind=ruDec.get(nameLR).possind;}
					ruMod.add(ent.getKey().name);}
				ruD.put(nRuD.name, nRuD);}
		}
		
		//Deuxième parties mise a jour des règles modifiées.
		Iterator<Entry<String,RuleDec>> it2=ruD.entrySet().iterator();
		ruD=new HashMap<String,RuleDec>();
		while(it2.hasNext()){
			Entry<String, RuleDec> ent=it2.next();
			RuleDec nRu=ent.getValue();
			if(nRu.e.add.atoms.isEmpty() && nRu.e.del.atoms.isEmpty()){continue;}
			if(ruMod.contains(ent.getKey())){
	//			System.out.println("IraleActionLearning - majRuDec - memeRegle");
	//			ex.s.print();System.out.println();
	//			ent.getValue().s.print();System.out.println();
				LinkedList<Atom> nind=new LinkedList<Atom>();
				LinkedList<Atom> oind=ent.getValue().ind;
				
				while(!oind.isEmpty()){
					Atom a=oind.removeFirst();
					AtomSet as=ent.getValue().s;
					Atom na=as.FindNew(a);
					if(na!=null){if(!nind.contains(a)){nind.add(na);}}}
				nRu.ind=nind;
				
				if(!ent.getValue().possind.isEmpty()){
					ListIterator<AtomSet> ll=ent.getValue().possind.listIterator();
					LinkedList<AtomSet> newPoss=new LinkedList<AtomSet>();
					while(ll.hasNext()){
						AtomSet pind=ll.next();
						AtomSet newPind=pind.maj(ent.getValue().s);
	//					System.out.println("IraleActionLearning - majRuDec - newpind");
	//					newPind.print();
						if (newPind.atoms.size()==1 && newPind.atoms.values().iterator().next().size()==1){
							Atom a=newPind.atoms.values().iterator().next().getFirst();
							if (!nRu.ind.contains(a)){nRu.ind.add(a);}}
						else{newPoss.add(newPind);}}
					nRu.possind=newPoss;
					majPossInd(nRu);}
			}else{
				
			//Troisième partie création des possinds sur les règles non modifiées.	
	//			ex.print();
	//			ent.getValue().print();
				LinkedList<AtomSet> newP=this.findPossind(ex, ent.getValue());
	//			System.out.println("IraleDuo - majRuDec - findPoss");
	//			System.out.println("Rule name :"+ent.getValue().name);AtomSet.llprintS(newP);
				LinkedList<AtomSet> temp=new LinkedList<AtomSet>(); 
				while(!newP.isEmpty()){
					AtomSet as=newP.removeFirst();
					if(as.atoms.size()==1 && as.atoms.values().iterator().next().size()==1){
						Atom a=as.atoms.values().iterator().next().getFirst();
						if (!nRu.ind.contains(a)){nRu.ind.add(a);}}
					else{temp.add(as);}}
				ListIterator<AtomSet> li=temp.listIterator();
				while(li.hasNext()){
					nRu.possind.add(li.next());}
				majPossInd(ent.getValue());}
		ruD.put(ent.getValue().name, nRu);}
	ruDec=ruD;}
	
	public void majPossInd(RuleDec ru){
		LinkedList<AtomSet> newAs=new LinkedList<AtomSet>();
		ListIterator<AtomSet> li=ru.possind.listIterator();
		while(li.hasNext()){
			boolean b=true;
			AtomSet as=li.next();
			if(as.contient(ru.ind)){
				continue;}
			ListIterator<AtomSet> las=newAs.listIterator();
			LinkedList<AtomSet> temp=new LinkedList<AtomSet>();
			while(las.hasNext()){
				AtomSet ast=las.next();
				if(as.couvPart(ast, new Subs())!=null){
					continue;}
				if(ast.couvPart(as, new Subs())!=null){
					b=false;
					temp.add(ast);
					while(las.hasNext()){temp.add(las.next());}
					break;}
				temp.add(ast);}
			if(b){temp.add(as);}
			newAs=(LinkedList<AtomSet>) temp.clone();}
	ru.possind=newAs;}
	
	
	public Subs choixSubs(LinkedList<Rule> lru,Example_AL ex){
		Subs ret=null;
		ListIterator<Rule>it=lru.listIterator();
		while(it.hasNext()){
			Rule ru=it.next();
			Subs s=ru.saCouv(ex, new Subs());
			if(ret==null){ret=s.copie();}
			else{ret=ret.inter(s);}}
	return ret;}
	
	public LinkedList<AtomSet> findPossind(Example_AL ex, RuleDec ru){
		LinkedList<AtomSet> liGen=new LinkedList<AtomSet>();
		Subs sa=null;
		if(ex.a.name.equals(ru.a.name)){sa=ex.a.gen2Vcheck(ru.a, new Subs());}
		if(sa!=null){
			if(genCheck){liGen.addAll(ru.s.gen(ex.s, sa,super.owner, false));}
			else{liGen.add(ru.s.genVquickS(ex,ru,sa,new LinkedList<Example_AL>(),new AtomSet(),false));}}
		LinkedList<AtomSet> res = new LinkedList<AtomSet>();
		ListIterator<AtomSet> gl=liGen.listIterator();
	//	System.out.println("IraleActionLearning - findPossind - listgen");
	//	AtomSet.llprintS(liGen);
		while(gl.hasNext()){
			AtomSet newAT=new AtomSet("possind");
			AtomSet g=gl.next();
			Iterator<Entry<String, LinkedList<Atom>>> r=ru.s.atoms.entrySet().iterator();
			while(r.hasNext()){
				Entry<String, LinkedList<Atom>> enr=r.next();
				ListIterator<Atom> arl = enr.getValue().listIterator();
				while(arl.hasNext()){
					Atom a=arl.next();
					Atom b=g.FindNew(a);
					if(b==null)
						{newAT.addAtom(a);}
					}
				}
			res.add(newAT);}
	return res;}
	
	public Example_AL conserv1step(Example_AL ex){
		HashSet<String> hs=new HashSet<String>();
		AtomSet as=new AtomSet();
		int i=0,j=ex.a.terms.length;
		while(i<j){
			hs.add(ex.a.terms[i].name);i++;}
		Iterator<Entry<String, LinkedList<Atom>>> it=ex.s.atoms.entrySet().iterator();
		while(it.hasNext()){
			ListIterator<Atom> li=it.next().getValue().listIterator();
			while(li.hasNext()){
				Atom a=li.next();
				i=0;j=a.terms.length;
				while(i<j){
					if(hs.contains(a.terms[i].name)){as.addAtom(a);break;}i++;}
			}
		}
		ex.s=as;
		return ex;}

	
	public LinkedList<Rule> irale1(Rule ruE,int ag,boolean neg){
	//	System.out.println("IraleActionLearning - irale1 - ex :");exE.print();
	//	System.out.println();
		Example_AL ex=(Example_AL) ruE.copy();
		LinkedList<Rule> ruIrale1=new LinkedList<Rule>();
		if(!(ex.e.add.atoms.isEmpty() && ex.e.del.atoms.isEmpty())){
			ex.e.add=new AtomSet();ex.e.del=new AtomSet();
			Term[] tt=new Term[0];Atom a=new Atom("Eff_Pos",tt);
			ex.e.add.addAtom(a);}
		//ex=this.conserv1step(ex);
	//	ex.print();
		LinkedList<Rule> res1=new LinkedList<Rule>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> liru = this.rules1.entrySet().iterator();
		LinkedList<Rule> lx=new LinkedList<Rule>();
		while (liru.hasNext()){
			Entry<Rule, LinkedList<Rule[]>> enr=liru.next();
			Rule ru=enr.getKey();
			Subs newSig=ru.saCouv(ex, new Subs());
			if (newSig !=null){
				newSig=ru.e.couv(ex.e, newSig);
				if (newSig==null){lx.addAll(this.spec1(ex,ru,enr,newSig));liru = this.rules1.entrySet().iterator();}}}
		if(!lx.isEmpty()){contrexAdd(ex,this.contr_ex1);}
		ListIterator<Rule> llx=lx.listIterator();
		LinkedList<Rule> res;
		while(llx.hasNext()){
			res=genR.gen(llx.next(),ag, this.rules1, this.contr_ex1,new AtomSet());
			if(res!=null){res1.addAll(res);}}
		res=null;
		if(neg){res=genR.gen(ex,ag, this.rules1, this.contr_ex1,new AtomSet());}
		else{if(!ex.e.add.atoms.isEmpty() || !ex.e.del.atoms.isEmpty())
				{res=genR.gen(ex,ag, this.rules1, this.contr_ex1,new AtomSet());}}
		if(res!=null){
			res1.addAll(res);
			ListIterator<Rule> itR=res.listIterator();
			while(itR.hasNext()){
				Rule ru=itR.next();
				if(ru.e.add!=null)
				{ruIrale1.add(ru);}}}
	return ruIrale1;}
	
	
	public LinkedList<Rule> spec1(Example_AL ex,Rule ru,Entry<Rule, LinkedList<Rule[]>> enr,Subs sig){
	//	System.out.println("Model - spec - name rule "+enr.getKey().name);
		boolean b;
		LinkedList<Rule[]> lru= enr.getValue();
		this.rules1.remove(enr.getKey());
		LinkedList<Rule> lx=new LinkedList<Rule>();
			do{if(lru.isEmpty()){b=false;}
				else{
				Rule[] ruP=lru.removeFirst();
				b=ruP[0].contrNeg(ex);
				if (b){lx.add(ruP[1]);}else{lru.addFirst(ruP);this.rules1.put(lru.getFirst()[0],lru);}
				}}while(b);
	return lx;}
	
	@Override
	public Atom selectAction(){
		this.actS=new LinkedList<ActScore>();
		Atom act=this.algo01();
		this.algo2();
		ListIterator<ActScore> lacs=this.actS.listIterator();
		ActScore res=null;
		int scoCur=0;
		while(lacs.hasNext()){
			ActScore lac=lacs.next();
			if(res==null){res=lac;scoCur=this.calcSco(res);}
			else{
				int nSco=this.calcSco(lac);
				if(nSco>=scoCur){scoCur=nSco;res=lac;}
			}
		}
		this.majVal(res);
		
	return res.act;}
	
	private void majVal(ActScore res) {
		// TODO Auto-generated method stub
		int i=0,j=res.score.length,r=0;
		while(i<j){
			if(res.score[i]*this.val[i]>res.score[r]*val[r]){r=i;}
			i++;}
		this.val[r]=0;
		i=0;
		while(i<j){this.val[i]=10+this.val[i];i++;}
	}

	private int calcSco(ActScore acs) {
		// TODO Auto-generated method stub
		int i=0,j=acs.score.length;
		int sc=0;
		while(i<j){
			sc=sc+acs.score[i]*this.val[i];
			i++;}
		return sc;
	}

	public Atom algo01(){
		Iterator<Rule> it=this.getRule().keySet().iterator();
		this.contr_ex1=MemorySelection.selectMemEx(MemorySelection.MEM_BASIC, 0, null);
		this.rules1=new HashMap<Rule,LinkedList<Rule[]>>();
		Atom act = null;
		Atom actR = null;
		while(it.hasNext()){
			this.irale1(it.next(), this.owner,false);}
		int i=0;
		boolean b=true;
		boolean c=true;
		while(((b || c) && i<10) || (b && c)){
			act=super.selectAction();
			//act.print();System.out.println();
		//	env.etat.print();
			Atom res=this.testActionRules(this.rules1, env.getEtat(), new Subs(),(LinkedList<Example_AL>) this.contr_ex1,new AtomSet(), act);
			if(res==null && c){this.addAcs(act, 0);c=false;}
			if(res!=null && b){this.addAcs(act, 1);b=false;}
			i++;}
		
	return act;}
	
	public void addAcs(Atom act, int f){
		ListIterator<ActScore> lacs=this.actS.listIterator();
		while(lacs.hasNext()){
			ActScore acs=lacs.next();
			if(acs.act.egal(act)){acs.score[f]++;return;}
		}
		ActScore acs=new ActScore(act,3);acs.score[f]++;this.actS.add(acs);
	}
	
	public void algo2(){
		Iterator<RuleDec> it=this.ruDec.values().iterator();
		while(it.hasNext()){
			RuleDec rud=it.next();
			if(rud.e.add.atoms.isEmpty() && rud.e.del.atoms.isEmpty()){continue;}
			AtomSet ind=new AtomSet();
			boolean b=true;
			ListIterator<Atom> lia=rud.ind.listIterator();
			while(lia.hasNext()){ind.addAtom(lia.next());}
			ListIterator<AtomSet> lias=rud.possind.listIterator();
			while(lias.hasNext()){
				AtomSet possind=lias.next();
				Rule ru=new Rule("possind",new AtomSet(possind),new Atom(rud.a),new Effect(rud.e));
				Atom act=this.findAction(ru,env.getEtat(), new Subs(),(LinkedList<Example_AL>) this.contr_ex, ind);
				if(act!=null){addAcs(act,2);addAcs(act,2);b=false;}}
			if(b){
				Atom act=this.findAction(new Rule(rud),env.getEtat(), new Subs(),(LinkedList<Example_AL>) this.contr_ex, ind);
				if(act!=null){addAcs(act,2);}}
		}
	}
	
	public Atom testActionRules(HashMap<Rule,LinkedList<Rule[]>> rules,AtomSet etat,Subs s,LinkedList<Example_AL>  contr_ex,AtomSet ind,Atom act){
		Iterator<Rule> liru = this.rules1.keySet().iterator();
		while(liru.hasNext()){
			Rule ru=liru.next();
			//if(ru.e.add.atoms.isEmpty()){continue;}
			if(this.testAction(ru, etat, s, act, contr_ex,ind)){
				return act;}}
	return null;}
	
	
	
	/*Trouve une action pour la règle ru. La generalisation de la règle doit être en accord avec les contre-example(contrex), les atoms indispenssable (ind)
	  Et doit respecter au moins une des règles de rulesResp.*/
	public Atom findAction(Rule ru,AtomSet etat, Subs s,LinkedList<Example_AL> contrex,AtomSet ind){
		Example_AL ex=new Example_AL("etat",etat,null,null);
		LinkedList<Example_AL> cont=new LinkedList<Example_AL>();
		cont.addAll(contrex);
		boolean b=true;
		AtomSet gen=null;
		while(b){
			gen=ru.s.genVquickS(ex,ru, s, cont,ind,true);
			if(gen==null){return null;}
			if(b){
				AtomSet add=new AtomSet();
				add.addAtom(new Atom("contrexNeg",new Term[0]));
				Effect e=new Effect(add,new AtomSet());
				cont.add(new Example_AL("contrexNeg",gen,ru.a,e));}
		}
		if(!gen.egal(ru.s)){
			Subs sig=gen.couvPart(etat, new Subs());
			return ru.a.rev(sig);}
	return null;}
	
	public boolean testAction(Rule ru,AtomSet etat, Subs s,Atom act,LinkedList<Example_AL> contrex,AtomSet ind){
		Example_AL ex=new Example_AL("etat",etat,null,null);
		Subs sig=null;
		if(act.name.equals(ru.a.name)){sig=act.genVcheck(ru.a, s);}
		if(sig==null){return false;}
		AtomSet gen=ru.s.genVquickS(ex,ru, sig, contrex,ind,true);
		if(gen!=null && !gen.egal(ru.s)){
			Subs si=gen.couvPart(etat, new Subs());
			if(si!=null){return true;}}
	return false;}
	

	@Override
	public void print(){
		System.out.println();
		System.out.println("########################## Model ######################################");
		System.out.print("Rules Irale:");
		Iterator<Entry<Rule,LinkedList<Rule[]>>> hm=this.getRule().entrySet().iterator();
		while(hm.hasNext()){
		hm.next().getKey().print();}
		System.out.println("");
		System.out.print("Rules Irale2:");
		Iterator<Entry<String,RuleDec>> h=ruDec.entrySet().iterator();
		while(h.hasNext()){
			RuleDec rd=h.next().getValue();
			rd.print();
			System.out.print("Ind :");
			AtomSet.llprint(rd.ind);System.out.println();
			System.out.print("Possind :");
			AtomSet.llprintS(rd.possind);System.out.println();
		}
		System.out.println("");
		System.out.println("########################### Fin #######################################");
	}
	
	
	public void print2(){
		System.out.println();
		System.out.println("########################## Model Irale1 ######################################");
		System.out.print("Rules:");
		Iterator<Entry<Rule,LinkedList<Rule[]>>> hm=rules1.entrySet().iterator();
		while(hm.hasNext()){
			hm.next().getKey().print();}
		System.out.println("");
		System.out.println("################################ Fin #########################################");
	}
	
}
