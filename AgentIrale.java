package learning.actionLearning.irale;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import smileagents.Agent;
import smileagents.CanalComm;
import smileagents.CommunicationModule;
import smileagents.Learner;
import smileagents.Message;
import smileagents.Network;
import smileagents.Protocole;
import smileagents.stats.StatAgent;

import learning.ExampleMemory;
import learning.Hypothesis;
import learning.TaggableExample;
import learning.actionLearning.Example_AL;
import learning.actionLearning.Hypotheses_AL;
import learning.actionLearning.LearningProcessIRALE;
import learning.actionLearning.MessageAgAuto;
import learning.actionLearning.ProtocoleSelection;

public class AgentIrale<Ex extends TaggableExample, Hypo extends Hypotheses_AL>
	implements Runnable, Agent, Learner<Ex, Hypo>{
	public int name;
	private Thread agentThread = null;

	public int nbChoiPos=0;
	public int nbChoiAct=0;
	public Protocole<Ex> gbProtocol;
	public CanalComm commAgent;
	public CanalComm commSystem;
	public CommunicationModule comm;
	public StatAgent<Hypo> stats;
	public boolean competent = true;
	public LearningProcessIRALE lp;
	public int sys;
	
	
	AgentIrale(int name,int sys,Model mod,AgentIrale[] agents,int codeProt,CanalComm system, Network commbc,int nbAct,int tailleRun,boolean learNeg){
		stats=new StatAgent<Hypo>(Integer.toString(name));
		commSystem = system;
		commAgent = new CanalComm(this);
		this.sys=sys;
		commAgent.verbose=false;
		this.comm = new CommunicationModule(commAgent, commSystem, commbc, stats);
		this.gbProtocol = ProtocoleSelection.ProtocolSelect(codeProt, this,comm,sys);
		comm.setProtocol(gbProtocol);
		System.out.println("AgentIrale - construc - commAgent - "+commAgent.hashCode());
		lp=new LearningProcessIRALE(this,nbAct,tailleRun,system,comm,gbProtocol,mod,name,sys,learNeg);
		
		this.name=name;
		
		stats= new StatAgent<Hypo>(Integer.toString(name));
		}
	
	
	public void run() {
		Thread myThread = Thread.currentThread();
		while (agentThread == myThread) {
			Message m = (Message) commAgent.get(this);
	//		System.out.println("AgentIrale - run - codeMessage : "+this.name+" <- "+m.getCode()+" <- "+m.getSender().owner);
			if (comm.gereParProtocol(m)) {
				comm.receiveMessage(m);
			} else if (m != null) {
				switch (m.getCode()) {
				case MessageAgAuto.AG_CAN_WORK:
					if(lp.learningThread.getState()!=State.NEW){
						commAgent.enqueue(comm, m);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}}
					else{
						lp.start();}
					break;
				case Message.SYS_FINISH:
					agentThread = null;
					break;
				case MessageAgAuto.AG_RESET:
					this.lp.reset();
					break;
				case Message.SYS_START:
					if (gbProtocol.useCompetence() && competent && this.comm.getNbNeighbours()>0) {
						comm.sendBC(Message.GB_NEW_AGENT_COMPETENT, commAgent);
					}
					comm.send(Message.SYS_STARTED, null, commSystem);
					break;
				}
				m = null;
			}
		}
		
}
	

	
	public void start() {
		if (agentThread == null) {
			agentThread = new Thread(this);
			agentThread.start();
		}
	}


	@Override
	public CanalComm getComm() {
		// TODO Auto-generated method stub
		return commAgent;
	}

	@Override
	public void adopteHyp(Hypo hyp) {
		// TODO Auto-generated method stub		
	}

	@Override
	public Hypo getHypotheses() {
		// TODO Auto-generated method stub
		Hypo hyp=(Hypo)this.lp.mod.hyp;
		return hyp; 
	}
	

	@Override
	public int getNbExamples() {
		// TODO Auto-generated method stub
		return lp.mod.contr_ex.getNbExamples();
	}

	@Override
	public void forget(String tag) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeTag(String tag) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean fullMemory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Ex> getExamples(int nb) {
		// TODO Auto-generated method stub
		return (List<Ex>) lp.mod.contr_ex.getAllStoredExamples();
	}

	@Override
	public boolean CheckHypothesis(Hypo Hyp, int nbMaxAnswers,
			List<Ex> resCounterExamples) {
		// TODO Auto-generated method stub
		boolean res=true;
		ArrayList<Rule> hyp=(ArrayList<Rule>) Hyp.hypos;
		LinkedList<Example_AL> liit=(LinkedList<Example_AL>) ((LinkedList<Example_AL>) lp.mod.contr_ex.getAllStoredExamples()).clone();
		ListIterator<Example_AL> lit=liit.listIterator();
		while(lit.hasNext()){
			Example_AL ex=lit.next();
			ListIterator<Rule> li=hyp.listIterator();
			while(li.hasNext()){
				Rule ru=(Rule) li.next();
				if(ru.contrNeg(ex)){
					resCounterExamples.add((Ex)new Example_AL(ex));
					res=false;
					break;
				}	
			}
		}
		lit=liit.listIterator();
		while(lit.hasNext()){
			Example_AL ex=lit.next();
			ListIterator<Rule> li=hyp.listIterator();
			boolean b=true;
			while(li.hasNext()){
				Rule ru=(Rule) li.next();
				Subs s=ru.aeCouv(ex, new Subs());
				if(s!=null){
					Subs sig2=ru.s.couvPart(ex.s, s);
					if(sig2!=null){
						b=false;
						break;
					}
				}
			}
			if(b){resCounterExamples.add((Ex)new Example_AL(ex));}
		}
		return res;}

	@Override
	public boolean learnExamples(List<Ex> examples) {
		return lp.learnExamples((List<Example_AL>)examples);
	}

	@Override
	public void receptionNewExemple(List<Ex> examples) {
		// TODO Auto-generated method stub
	}
	
	public boolean isAlive() {
		if (agentThread == null) {
			getComm().die();
		}
		return agentThread != null;
	}
	
	@Override
	public String toString(){
		return Integer.toString(this.name);
	}
	
}