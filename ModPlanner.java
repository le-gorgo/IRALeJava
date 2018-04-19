package learning.actionLearning.irale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;



public class ModPlanner{
	public Planner plannerThread = null;
	public HashMap<Rule,LinkedList<Rule[]>> rules;
	public HashMap<String,Term> objs;
	public HashMap<String,Term[]> varPara;
	public HashSet<String> vCons=new HashSet<String>();
	public AtomSet init;
	public Effect goal;
	public String fileNameDom,fileNameProb,fileNameSol,domain,problem;
	public RuSub resPlan=null;
	public int maxIte;
	public int waiting=2000;
	File fileSol;
	
	public ModPlanner(HashMap<String,Term> objs,HashMap<Rule, LinkedList<Rule[]>> rules,AtomSet init,Effect goal,int maxAct){
		this.objs=objs;
		this.rules=rules;
		this.init=init;
		this.goal=goal;
		this.maxIte=maxAct;
	}
	
	public ModPlanner(LinkedList<Rule> rules,AtomSet init,Effect goal){
		this.objs=objs;
		this.rules=this.toHashMap(rules);
		this.init=init;
		this.goal=goal;
	}

	
	public synchronized RuSub usePlan() {
		this.writeFile(rules, init, goal);
		File fileSol=new File(fileNameSol);
		fileSol.delete();
		run();
		try{
			int t=0;
		//		System.out.println("Waiting for the planner...");
			while(t<=waiting){
				Thread.sleep(250);
				if(!this.plannerThread.isAlive()){
					return readSol();}
			t=t+250;}
			}catch(Exception e){
				e.printStackTrace();
			}
		rmFiles();
		if(plannerThread.isAlive()){plannerThread.interrupt();}
	 return resPlan;}
	
	
	public void rmFiles(){
		File file=new File(fileNameSol);
		file.delete();
		file=new File(fileNameDom);
		file.delete();
		file=new File(fileNameProb);
		file.delete();
		plannerThread.destroyFF();
	}
	
	public RuSub readSol(){
		RuSub nRus=null;
		try (BufferedReader br =new BufferedReader(new FileReader(fileNameSol))){
			String line;
			boolean b=true;int c=0;
			while (true){
				line=br.readLine();
				if(b){
				if(line==null){
			//		System.out.println("ModPlanner - readSol - fichier vide...");
					br.close();
					rmFiles();
					return null;}
	//			System.out.println("readSol "+fileNameSol);
	//			System.out.println("line "+line);
				line=line.substring(11, line.length());
				String[] parts=line.split(" ");
				nRus=new RuSub();
				nRus.name=parts[0];
				int i=1,j=parts.length;
				nRus.var=this.varPara.get(parts[0]);
				nRus.cons=new Term[j-1];
				while(i<j){
				nRus.cons[i-1]=new Term(parts[i],false);
				i++;}
				Iterator<Entry<Rule, LinkedList<Rule[]>>> it=this.rules.entrySet().iterator();
				while (it.hasNext()){
					Rule ru=it.next().getKey();
					if (ru.name.equals(nRus.name)){
			//			System.out.println("finded rule!!!");
						nRus.ru=ru.rev(nRus.var,nRus.cons);
						break;}}
			//	if (!nRus.ru.a.terms[0].name.equals(nRus.ru.a.terms[1].name)){
			//		break;}
				c++;
				b=false;
				}else{if(line==null){break;}else{c++;if(c>maxIte){br.close();rmFiles();return null;}}}
				}
			br.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
	//		System.out.println("ModPlanner - readSol - fichier non existant...");
			e.printStackTrace();}
		resPlan=nRus;
		rmFiles();
		return nRus;}
	
	public void run() {
		plannerThread=new Planner();
		plannerThread.start();
	}
	
	public HashMap<Rule, LinkedList<Rule[]>> toHashMap(LinkedList<Rule>ru){
		this.rules=new HashMap<Rule,LinkedList<Rule[]>>();
		ListIterator<Rule> li=ru.listIterator();
		while(li.hasNext()){
			this.rules.put(li.next(), null);
		}
	return this.rules;}
	
	public void writeFile(HashMap<Rule, LinkedList<Rule[]>> rules,AtomSet init,Effect goal){
		this.rules=rules;
		this.init=init;
		this.varPara=new HashMap<String,Term[]>();
		int contP=Global.getContP();
		fileNameProb=Global.rep+"/irale-"+contP+"-prob.pddl";
		fileNameDom=Global.rep+"/irale-"+contP+"-domain.pddl";
		fileNameSol=Global.rep+"/irale-"+contP+"-sol.ppdl";
		domain="color-blocksworld-"+contP;
		problem=domain+"-prob";
		defDomF();
		defProbF(goal);
		}
	
	public void findPara(Rule r,HashMap<String,Term> para){
		Iterator<Entry<String, LinkedList<Atom>>> itS=r.s.atoms.entrySet().iterator();
		while(itS.hasNext()){
			Entry<String, LinkedList<Atom>>eR=itS.next();
			LinkedList<Atom> la=eR.getValue();
			ListIterator<Atom> ia=la.listIterator();
			Atom a=null;
			while(ia.hasNext()){
				a=ia.next();
				int i=0,j=a.terms.length;
				while(i<j){
					para.put(a.terms[i].name,a.terms[i]);
					if (!a.terms[i].type){this.vCons.add(a.terms[i].name);}
					i++;}}}
		
		itS=r.e.add.atoms.entrySet().iterator();
		while(itS.hasNext()){
			Entry<String, LinkedList<Atom>>eR=itS.next();
			LinkedList<Atom> la=eR.getValue();
			ListIterator<Atom> ia=la.listIterator();
			Atom a=null;
			while(ia.hasNext()){
				a=ia.next();
				int i=0,j=a.terms.length;
				while(i<j){para.put(a.terms[i].name,a.terms[i]);i++;}}}	
	}
	
	public void findPred(HashMap<String,Atom> pred){
		Iterator<Entry<Rule,LinkedList<Rule[]>>> itR =this.rules.entrySet().iterator();
		while (itR.hasNext()){
			Rule r=itR.next().getKey();
			Iterator<Entry<String, LinkedList<Atom>>> itS=r.s.atoms.entrySet().iterator();
			while(itS.hasNext()){
				Entry<String, LinkedList<Atom>>eR=itS.next();
				LinkedList<Atom> la=eR.getValue();
				ListIterator<Atom> ia=la.listIterator();
				Atom a=null;
				if(ia.hasNext()){
					a=ia.next();}
				pred.put(a.name,a);}
			
			itS=r.e.add.atoms.entrySet().iterator();
			while(itS.hasNext()){
				Entry<String, LinkedList<Atom>>eR=itS.next();
				LinkedList<Atom> la=eR.getValue();
				ListIterator<Atom> ia=la.listIterator();
				Atom a=null;
				if(ia.hasNext()){
					a=ia.next();}
				pred.put(a.name,a);}
			
		}
	}
	
	public void writeRule(PrintWriter w,Rule r,HashMap<String,Atom> pred,HashMap<String,Term> para){
		para=new HashMap<String, Term>();
		this.vCons=new HashSet<String>();
		this.findPara(r, para);
		w.println("	(:action "+r.name);
		Term[] ts=new Term[para.size()];
		varPara.put(r.name, ts);
		w.print("		:parameters (");
		Iterator<Entry<String, Term>> itP=para.entrySet().iterator();
		int i=0;
		while(itP.hasNext()){
			Entry<String,Term> ent=itP.next();
			if(ent.getValue().type){
				w.print(" ?"+ent.getKey()+" - var ");
			}else{w.print(" ?"+ent.getKey()+" - c"+ent.getKey());}
			ts[i]=ent.getValue();
			i++;}
		w.println(")");
		w.print("		:precondition (and ");
		Iterator<Entry<String, LinkedList<Atom>>> itS=r.s.atoms.entrySet().iterator();
		while(itS.hasNext()){
			Entry<String, LinkedList<Atom>>eR=itS.next();
			LinkedList<Atom> la=eR.getValue();
			ListIterator<Atom> ia=la.listIterator();
			Atom a=null;
			while(ia.hasNext()){
				a=ia.next();
				w.print("("+a.name+" ");
				i=0;
				int j=a.terms.length;
				while(i<j){
						w.print("?"+a.terms[i].name+" ");
					i++;}
				w.print(")");
				}}
		itP=para.entrySet().iterator();
		w.println(")");
		w.print("		:effect (and");
		itS=r.e.add.atoms.entrySet().iterator();
		while(itS.hasNext()){
			Entry<String, LinkedList<Atom>>eR=itS.next();
			LinkedList<Atom> la=eR.getValue();
			ListIterator<Atom> ia=la.listIterator();
			Atom a=null;
			while(ia.hasNext()){
				a=ia.next();
				w.print("("+a.name+" ");
				int j=a.terms.length;i=0;
				while(i<j){
					 	w.print("?"+a.terms[i].name+" ");
					i++;}w.print(")");}}
		itS=r.e.del.atoms.entrySet().iterator();
		while(itS.hasNext()){
			Entry<String, LinkedList<Atom>>eR=itS.next();
			LinkedList<Atom> la=eR.getValue();
			ListIterator<Atom> ia=la.listIterator();
			while(ia.hasNext()){
				Atom a=ia.next();
				w.print("( not("+a.name+" ");
				int j=a.terms.length;i=0;
				while(i<j){
						w.print("?"+a.terms[i].name+" ");
				i++;}w.print(")) ");}
			}
		w.println("))");
	}
	
	public void defProbF(Effect goal){
		try {
			PrintWriter w =new PrintWriter(fileNameProb, "UTF-8");
			w.println(";; "+fileNameProb);
			w.println("(define (problem "+problem+")");
			w.println("	(:domain "+domain+")");
			
			w.print("	(:objects ");
			Iterator<Entry<String,Term>> itO=objs.entrySet().iterator();
			
			while(itO.hasNext()){
				Term t=itO.next().getValue();
				if (this.vCons.contains(t.name)){
					w.write(""+t.name+" - c"+t.name+" ");}}
			itO=objs.entrySet().iterator();
			while(itO.hasNext()){
				Term t=itO.next().getValue();
				if (!this.vCons.contains(t.name)){
					w.write(""+t.name+" ");}}
				w.write("- var");
				
			w.println(")");
			
			w.print("	(:init ");
			Iterator<Entry<String, LinkedList<Atom>>> itA=init.atoms.entrySet().iterator();
			while(itA.hasNext()){
				ListIterator<Atom> liA=itA.next().getValue().listIterator();
				while(liA.hasNext()){
					Atom a=liA.next();
					w.print("("+a.name+" ");
					Term[] ts=a.terms;
					int i=0,j=ts.length;
					while(i<j){
						w.write(ts[i].name+" ");
					i++;}
					w.print(")");}}
				
				
			w.println(")");
			
			
			w.print("	(:goal");
			Iterator<Entry<String, LinkedList<Atom>>> itS=goal.add.atoms.entrySet().iterator();
			if (itS.hasNext()){w.print(" (and ");}
			while(itS.hasNext()){
				Entry<String, LinkedList<Atom>>eR=itS.next();
				LinkedList<Atom> la=eR.getValue();
				ListIterator<Atom> ia=la.listIterator();
				Atom a=null;
				while(ia.hasNext()){
					a=ia.next();
					w.print("("+a.name+" ");
					int i=0,j=a.terms.length;
					while(i<j){w.print(a.terms[i].name+" ");i++;}w.print(")");}}	
			itS=goal.del.atoms.entrySet().iterator();
			w.println(")))");
			
			w.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	
	public void defDomF(){
		HashMap<String,Atom> pred=new HashMap<String,Atom>();
		HashMap<String,Term> para=new HashMap<String, Term>();
		Iterator<Entry<Rule,LinkedList<Rule[]>>> itR =this.rules.entrySet().iterator();
		this.vCons=new HashSet<String>();
		while (itR.hasNext()){
			Rule r=itR.next().getKey();
			if(!r.e.add.atoms.isEmpty()||!r.e.del.atoms.isEmpty()){
				this.findPara(r, para);}}
		
		try {
			PrintWriter w =new PrintWriter(fileNameDom, "UTF-8");
			w.println(";; "+fileNameDom);
			w.println();
			w.println("(define (domain "+domain+")");
			w.println("	(:requirements :adl)");
			
			w.println("	(:types var");
			Iterator<Entry<String, Term>> it=para.entrySet().iterator();
			while(it.hasNext()){
				Term t=it.next().getValue();
				if(!t.type){
				w.println("	 c"+t.name);}
			}
			w.print("	)");
			w.println();
			
			w.print("	(:predicates ");
			this.findPred(pred);
			Iterator<Entry<String, Atom>> la=pred.entrySet().iterator();
			while(la.hasNext()){
				Atom a=la.next().getValue();
				w.print("("+a.name+" ");
				int i=0,j=a.terms.length;
				while(i<j){w.print("?"+a.terms[i].name+" ");i++;}w.print(")");}w.print(")");

			itR =this.rules.entrySet().iterator();
			while (itR.hasNext()){
				Rule r=itR.next().getKey();
				if(!r.e.add.atoms.isEmpty()||!r.e.del.atoms.isEmpty())
				{
					w.println();
					this.writeRule(w,r,pred,para);}}
			w.print(")");
			w.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}}

	class Planner extends Thread{
		public Process p;
		public String refPro;
		
		public void run(){
			 String[] comm={"/bin/sh","-c","/home/rioux/FF/ff -o "+fileNameDom+" -f "+fileNameProb+" | grep ': R' > "+fileNameSol};
			refPro="/home/rioux/FF/ff -o "+fileNameDom+" -f "+fileNameProb;
			 //Runtime.getRuntime().exec("/home/rioux/bin/satplan -domain "+fileNameDom+" -problem "+fileNameProb+" -solution "+fileNameSol);
			try {
				p=Runtime.getRuntime().exec(comm);
				p.waitFor();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				destroyFF();
			}
		}
		
		public void destroyFF(){
			// TODO Auto-generated method stub
			String[] comm={"/bin/sh","-c","kill $(ps -aux | grep '"+refPro+"' | awk '{print $2}')"};
			try {
				p=Runtime.getRuntime().exec(comm);
				p.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}

