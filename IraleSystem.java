package learning.actionLearning.irale;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

import smileagents.Agent;
import smileagents.AgentSystem;
import smileagents.CanalComm;
import smileagents.CommunicationModule;
import smileagents.Message;
import smileagents.Network;
import smileagents.grapheliaison.GrapheLiaison;
import smileagents.stats.StatAgent;

import learning.actionLearning.environnement.Envi;
import learning.actionLearning.environnement.EnviSelection;

public class IraleSystem {
	AgentIrale[][] agents;
	Vector<CanalComm>[] agent;
	int nbAg;
	int nbRun;

	public int res;
	public Network[] BC;
	public AgentSystemIrale[] syst;
	public String expName;
	public int waitTime=1000*60*5;
	
	
	public IraleSystem(String expName,int nbAg,int nbRun,int nbAct,int tailleRun, int codEnvi,int nbIte,int codeProt,String grapheName,int codEvolv,boolean turnByTurn,String typeGen,String typeIrale,String typeGenPoss,boolean learNeg,String rep){
		this.expName=expName;
		this.agents=new AgentIrale[nbRun][];
		this.BC=new Network[nbRun];
		this.syst=new AgentSystemIrale[nbRun];
		this.res=0;
		this.nbAg=nbAg;
		this.nbRun=nbRun;
		int r=0;
		while(r<nbRun){
			this.agents[r]=new AgentIrale[nbAg];
			boolean gNull=false;
			GrapheLiaison graphe= new GrapheLiaison();
			gNull=true;
			BC[r] = new Network(graphe, null, codEvolv);
			syst[r] = new AgentSystemIrale(expName, BC[r], turnByTurn,nbAg,1,r,this);
			BC[r].commSys=syst[r].getComm();
			int i=0;
			while (i<nbAg){
				Envi env=EnviSelection.enviSelect(codEnvi,nbIte,rep);
				if (typeIrale.equals("iraleDuo")){
					this.agents[r][i]=new AgentIrale(i,r, new IraleActionLearning(env,i,typeGen,typeGenPoss),this.agents[r],codeProt,BC[r].commSys, BC[r],nbAct,tailleRun,learNeg);}
				else{this.agents[r][i]=new AgentIrale(i,r, new IraleClassic(env,i,typeGen),this.agents[r],codeProt,BC[r].commSys, BC[r],nbAct,tailleRun,learNeg);}
				if(gNull){graphe.addAgent(this.agents[r][i].commAgent);
				if(i!=0){graphe.addLien(this.agents[r][i].commAgent,this.agents[r][i-1].commAgent);}}
				i++;}
			//	System.out.println("SystemIrale - constructeur - clotureTransitive - graphe :"+graphe.ConnectGraph.toString());
			if(gNull){BC[r].graphe=graphe.clotureTransitive(nbAg);}
			r++;}
	}

	public void lance(){
		Global.defLogFiles(expName);
		Global.rep=expName;
		int r=0;
		while(r<nbRun){
			BC[r].Init();
			syst[r].start();
			int ag=0;
			while(ag<nbAg){
				agents[r][ag].start();
				ag++;}
		r++;}
		
	/*	while(res<nbRun){
			try {
				System.out.println("nbRun Fini:"+res+"/"+nbRun);
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("nbRun Fini:"+res+"/"+nbRun);
		Global.inufiLogRun(nbRun);*/
	}
	
	
	
}