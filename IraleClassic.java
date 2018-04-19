package learning.actionLearning.irale;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import learning.actionLearning.Example_AL;
import learning.actionLearning.environnement.Envi;

public class IraleClassic extends Model{

	public IraleClassic(Envi env, int owner, String typeGen) {
		super(env, owner, typeGen);
		// TODO Auto-generated constructor stub
	}
	
	public LinkedList<Rule> irale(Example_AL ex,int ag,boolean neg){
		super.exLearn=0;
		LinkedList<Rule> res1=new LinkedList<Rule>();
		HashMap<Rule,LinkedList<Rule[]>> rules=this.getRule();
		HashMap<Rule,LinkedList<Rule[]>> newRules=new HashMap<Rule,LinkedList<Rule[]>>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> liru =rules.entrySet().iterator();
		LinkedList<Rule> lx=new LinkedList<Rule>();
		while (liru.hasNext()){
			Entry<Rule, LinkedList<Rule[]>> enr=liru.next();
			Rule ru= new Rule(enr.getKey());
			LinkedList<Rule[]> lru=enr.getValue();
			Subs newSig=ru.saCouv(ex, new Subs());
			if (newSig !=null){
				newSig=ru.e.couv(ex.e, newSig);
				if (newSig==null){lx.addAll(spec(ex,ru,lru,newSig,newRules));}}
			newRules.put(lru.getFirst()[0],lru);}
		
		if(!lx.isEmpty()){contrexAdd(ex,contr_ex);super.exLearn=1;}
		ListIterator<Rule> llx=lx.listIterator();
		LinkedList<Rule> res;
		while(llx.hasNext()){
			res=gen.gen(llx.next(),ag, newRules, contr_ex,new AtomSet());
			if(res!=null){res1.addAll(res);}}
//		ex.print();
		if(!neg && ex.e.add.atoms.isEmpty() && ex.e.del.atoms.isEmpty()){
			exEmpty=1;
			return res1;}
		if(ex.e.add.atoms.isEmpty() && ex.e.del.atoms.isEmpty()){exEmpty=1;}else{exEmpty=0;}
		boolean b=modifHyp;
		modifHyp=false;
		res=gen.gen(ex,ag, newRules, contr_ex,new AtomSet());
		if (modifHyp){super.exLearn=1;}
		modifHyp=(b || modifHyp);
		if(res!=null){res1.addAll(res);}
		this.putRule(newRules);
		majHypo();
	return res1;}
	
	public LinkedList<Rule> spec(Example_AL ex,Rule ru,LinkedList<Rule[]> lru,Subs sig,HashMap<Rule,LinkedList<Rule[]>> rules){
		//	System.out.println("Model - spec - name rule "+enr.getKey().name);
		boolean b;
		LinkedList<Rule> lx=new LinkedList<Rule>();
		Rule[] ruP=null;
			do{if(lru.isEmpty()){b=false;lru.add(ruP);}
				else{
				ruP=lru.removeFirst();
				b=ruP[0].contrNeg(ex);
				if (b){lx.add(ruP[1]);}else{lru.addFirst(ruP);}
				}}while(b);
	return lx;}
	
	public int pl(AtomSet init,Effect goal, int ag,int act,int maxAct){
		//0 si planner echou, -1 si la règle utilisé n'a pas les bons effets
		//1 si tous va bien, 2 si le goal est atteint. 
		//goal.print();System.out.println();
		//init.print();System.out.println();
		Subs s=new Subs();
		s=goal.add.couvPart(init, s);
		if (s!=null && s.aToa()){
			if(goal.del.atoms.isEmpty() || goal.del.couvPart(init, s)==null){
				return 2;}
			}
		this.pl=new ModPlanner(env.objs,this.getRule(),init,goal,maxAct);
		RuSub plan=this.pl.usePlan();
		if (plan==null || plan.ru==null){return 0;}
		Example_AL retEnv=this.env.resolv(plan.ru.a);
		retEnv.name="agent"+this.owner+"_"+act+"_"+globalTemp;
		this.irale(retEnv,ag,false);
		if(!retEnv.e.egal(plan.ru.e)){
			Iterator<Entry<Rule, LinkedList<Rule[]>>> it=this.getRule().entrySet().iterator();
			while(it.hasNext()){
				Rule r=it.next().getKey();
				if(r.name.equals(plan.name))
					{this.removeRule(r);break;}
				}
			this.exPredic=0;
			return -1;}
	this.exPredic=1;
	return 1;}
	
	public Atom selectActionNoEmpty(AtomSet etat){
		AtomSet init=new AtomSet(this.env.etat);
		boolean b=true;
		Atom act=null;
		while(b){
			act=this.env.selectAction();
			Example_AL r=this.env.resolv(act);
			if(!r.e.add.atoms.isEmpty()||!r.e.del.atoms.isEmpty()){b=false;}}
		this.env.etat=init;
		return act;}

	public Atom selectAction(){
		AtomSet init=new AtomSet(this.env.etat);
		Atom act=null;
		act=this.env.selectAction();
		this.env.etat=init;
		return act;}
}
