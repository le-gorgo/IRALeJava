package learning.actionLearning.irale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;
import smileagents.experimenter.MemorySelection;
import learning.ExampleMemory;
import learning.actionLearning.Example_AL;
import learning.actionLearning.Hypotheses_AL;
import learning.actionLearning.environnement.Envi;


public abstract class Model {
	public ExampleMemory<Example_AL> contr_ex;
	private HashMap<Rule,LinkedList<Rule[]>> rules;
	private static final Object valueMonitor=new Object();
	public static final Object valueMonLearn=new Object();
	public ModPlanner pl;
	public Envi env;
	public Gen gen;
	public HashMap<String,HashSet<String>> nat;
	public int owner;
	public boolean modifHyp;
	public int exEmpty,exLearn,exPredic;
	public int globalTemp;
	public Hypotheses_AL hyp=new Hypotheses_AL();
	public HashMap<String,RuleDec> ruDec;
	
	public Model(Envi env,int owner,String typeGen){
		this.owner=owner;
		this.rules=new HashMap<Rule,LinkedList<Rule[]>>();		
		//this.contr_ex=new LinkedList<Example_AL>();
		this.env=env;
		this.contr_ex=MemorySelection.selectMemEx(MemorySelection.MEM_BASIC, 0, null);
		this.nat=new HashMap<String,HashSet<String>>();
		ruDec=new HashMap<String,RuleDec>();
		if(typeGen.equals("genQuick")){this.gen=new GenQuick(this);}
	}
	
	public HashMap<Rule,LinkedList<Rule[]>> getRule(){
		synchronized (this.valueMonitor){return (HashMap<Rule, LinkedList<Rule[]>>) rules.clone();}}
	
	public void resetRule(){
		synchronized (this.valueMonitor){this.rules=new HashMap<Rule, LinkedList<Rule[]>>();}}
	
	public void removeRule(Rule ru){
		synchronized (this.valueMonitor){this.rules.remove(ru);}
	}
	
	public void putRule(HashMap<Rule,LinkedList<Rule[]>> ru){
		synchronized (this.valueMonitor){this.rules=ru;}
	}
	
	public abstract LinkedList<Rule> irale(Example_AL ex,int ag,boolean neg);
	
	public abstract int pl(AtomSet init,Effect goal, int ag,int act,int maxAct);
	
	public abstract Atom selectAction();
	
	
	public int testHyp(Example_AL ex){
		Iterator<Entry<Rule, LinkedList<Rule[]>>> it=this.getRule().entrySet().iterator();
		while(it.hasNext()){
			Rule r=it.next().getKey();
			Subs s=r.saCouv(ex, new Subs());
			if(s!=null){
				if(r.e.couv(ex.e, s)!=null){return 1;}
				else{return 0;}
			}
		}
	return 0;}
	
	public LinkedList<Example_AL> findContre(Rule ru){
		LinkedList<Example_AL> lex=new LinkedList<Example_AL>();
		if (this.contr_ex.getNbExamples()>0){ListIterator<Example_AL> it=contr_ex.getAllStoredExamples().listIterator(); 
		while (it.hasNext()){
			Example_AL ex=it.next();
			if (ru.contrNeg(ex)){
				lex.add(ex);}
		}}
	return lex;}
	
	public void print(){
		System.out.print("Rules:");
		Iterator<Entry<Rule,LinkedList<Rule[]>>> hm=rules.entrySet().iterator();
		while(hm.hasNext()){
			Rule ru=hm.next().getKey();
			if(!ru.e.add.atoms.isEmpty() || !ru.e.del.atoms.isEmpty()){
				ru.print();}}
		System.out.println("");
	}
	
	public void contrexAdd(Example_AL ex, ExampleMemory<Example_AL> contr_ex){
		if (contr_ex!=null)
			{ListIterator<Example_AL> li=contr_ex.getAllStoredExamples().listIterator();
			while (li.hasNext()){
				if(li.next().equals(ex)){return;}
			}}
		contr_ex.add(ex);
	}
	
	public void majHypo(){
		hyp.hypos= new ArrayList<Example_AL>();
		Iterator<Entry<Rule, LinkedList<Rule[]>>> it=rules.entrySet().iterator();
		while(it.hasNext()){
			hyp.hypos.add((Example_AL)it.next().getKey());}
	//	System.out.println("Model - majHypo - ");this.print();
	}
	
	
	public LinkedList<AtomSet> add(LinkedList<AtomSet> entre){
		LinkedList<AtomSet> sort=new LinkedList<AtomSet>();
		while(!entre.isEmpty()){
			ListIterator<AtomSet> ss=sort.listIterator();
			AtomSet e=entre.removeFirst();
			boolean b=true;
			while(ss.hasNext()){
				AtomSet as=ss.next();
				if(as.egal(e)){b=false; break;}}
			if(b){sort.add(e);}}
	return sort;}
	
	
	public Atom selectActionNoEmpty(AtomSet etat){
		Atom act=this.env.selectActionNoEmpty();
		return act;}
	
}
