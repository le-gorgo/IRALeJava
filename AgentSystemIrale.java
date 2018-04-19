package learning.actionLearning.irale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import learning.Hypothesis;
import learning.TaggableExample;
import learning.actionLearning.MessageAgAuto;
import learning.classification.atomic.HypWithHistory;
import smileagents.Agent;
import smileagents.AgentLearner;
import smileagents.AgentSystem;
import smileagents.CanalComm;
import smileagents.CommunicationModule;
import smileagents.Message;
import smileagents.Network;
import smileagents.AgentSystem.TimedAgentSystem;
import smileagents.protocols.ProtocoleMBLocalComplete;
import smileagents.stats.Counter;

public class AgentSystemIrale<ExampleRepr extends TaggableExample, Hyp extends Hypothesis<ExampleRepr>> 
			implements Runnable, Agent {
	
	
	private volatile Thread systemThread = null;
	
	public CommunicationModule<ExampleRepr, Hyp> cSyst;
	public Network netw;
	public CanalComm commSystem;

	public boolean tBTurn = false;
	public static int nbExTurn = 1;
	public List<ExampleRepr> allExemples;
	
	public String expname;
	public boolean enCours;
	
	public int date =0;

	public int exToConfirm = 0;
	public int exConfirmed = 0;
	public int nbEnvoyes = 0;
	
	public static int verbose = 1;
	public static boolean timed = false;
	public static int periodms = 500;
	
	public int pendingStart = 0;
	public int nbAg;
	public int nbAgentsActive = 0;// agents � attendre en tour par tour
	public int nbAgentsFinish=0;
	public boolean waitingToSend = false;
	public IraleSystem is;
	public int numSys;
	
	
	public Timer timer;
	
	public int nbRun,run=0;
	
	public AgentSystemIrale(String expname, Network commBC, boolean turnByTurn,int nbAg,int nbRun,int numSys, IraleSystem is) {
		this.nbAg=nbAg;
		this.nbRun=nbRun;
		this.numSys=numSys;
		this.is=is;
		commSystem = new CanalComm(this);
		commSystem.verbose=false;
		tBTurn=turnByTurn;
		netw = commBC;
		cSyst = new CommunicationModule(commSystem, commSystem, commBC, null);
		this.expname = expname;
		enCours = false;
		allExemples = new ArrayList<ExampleRepr>();//pour verif uniquement
		if (!tBTurn) nbExTurn=1;
	}


	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		if (systemThread == null) {
			getComm().die();
		}
		return systemThread != null;
	}


	@Override
	public CanalComm getComm() {
		// TODO Auto-generated method stub
		return commSystem;
	}


	public void run() {
		Thread myThread = Thread.currentThread();
		date=1; //tour par tour
		CanalComm agReady=null;
		while (systemThread == myThread) {
			Message m = (Message) commSystem.get(this);
	//		System.out.println("AgentSystemIrale - run - codeMessage : "+commSystem.owner+" <- "+m.getCode()+" <- "+m.getSender().owner);
			if (m != null) {
				int c=m.getCode();
				switch (c) {
				case Message.SYS_FINISH:
					finish();
					break;
				case Message.SYS_AGENT_ACTIVE: 
					nbAgentsActive++;
					break;
				case Message.SYS_STARTED:
					pendingStart--;
					if (!waitingToSend)
						break;
				case MessageAgAuto.AG_FINISH:
					nbAgentsFinish++;
	//				System.out.println("AgentSystemIrale - run - count agent finish - "+nbAgentsFinish+"/"+nbAg);
					if(nbAgentsFinish==nbAg){
						run++;
						if(this.nbRun==run){
							cSyst.groupSending(new Message(Message.SYS_FINISH, null, commSystem),
									netw.getAllAgents());
							this.cSyst.send(Message.SYS_FINISH, null,
									this.commSystem);}
						else{cSyst.groupSending(new Message(MessageAgAuto.AG_RESET, null, commSystem),
								netw.getAllAgents());nbAgentsFinish=0;Global.resTemp();
								Global.salog("#",this.numSys);Global.selog("#",this.numSys);Global.splog("#",this.numSys);
								System.out.println("Nb_Agents :"+this.nbAg+" Run :"+(this.run+1)+"/"+this.nbRun);
								}}
					break;
				case Message.SYS_AGENT_COMPLETE:
					if (m.getCode() == Message.SYS_AGENT_COMPLETE) {nbAgentsActive--;}
					if (nbAgentsActive > 0) {break;}
				//case Message.SYS_SEND_NEXT_EXEMPLE :
					//c=MessageAgAuto.AG_WANT_WORK;
				case MessageAgAuto.AG_WANT_WORK:
					if (tBTurn)
						{break;}
					else {agReady=m.getSender();}
					if (timed)
						break;
					if (pendingStart <= 0) {
						if (nbEnvoyes > 0) {netw.Evolve();}
						CheckConsistence();
						sendNextGo(agReady);
						agReady=null;
						date++;// increment tour par tour.
						waitingToSend = false;
					} else
						waitingToSend = true;
					break;
				}
				m = null;
			}
		}

	}
	
	public void sendNextGo(CanalComm ag) {

			if (verbose > 1) {
				System.out.println();
				System.out.print("   Envoi Exemple ");
				System.out.println(nbEnvoyes);
			}

			if(ag==null){
			Vector<CanalComm> v = netw.getNeighbours(commSystem);
			int nbAgents = v.size();
			int i=0;
			while (i<nbAgents){
				this.cSyst.send(MessageAgAuto.AG_CAN_WORK, null,
					v.get(i));
				i++;}
			nbEnvoyes = nbEnvoyes + 1;}
			else{
	//			System.out.println("AgentSystemIrale - sendNextGo - send to "+ag.owner);
				this.cSyst.send(MessageAgAuto.AG_CAN_WORK, null,
						ag);}
	}
	
	public boolean CheckConsistence () {
		boolean isConsistent = true;
		Counter<Long> NbOp = new Counter<Long>((long)0);
		// récupérer les hypothèses.
		Vector<CanalComm> listeAgents = netw.getAllAgents();
		for (int k=0;k<listeAgents.size();k++){
			CanalComm cAg = listeAgents.get(k);
			if(cAg.owner instanceof AgentLearner){
				AgentLearner AgL = (AgentLearner)cAg.owner;
				if (AgL.gbProtocol instanceof ProtocoleMBLocalComplete){
					ProtocoleMBLocalComplete AgPro = (ProtocoleMBLocalComplete)AgL.gbProtocol;
					List<HypWithHistory<TaggableExample>> HypothesesFilles = (List<HypWithHistory<TaggableExample>>) AgPro.hFilles;
					for (int i=0;i<HypothesesFilles.size();i++){
							HypWithHistory<TaggableExample> HypoTest = HypothesesFilles.get(i);
						for (int j=0;j<allExemples.size();j++){
							ExampleRepr exem = allExemples.get(j);
							boolean test = HypoTest.predictsCorrectly(exem, NbOp);
							if (!test) {
		//						System.out.println("date = " +date+ ", agent : " + AgL + ", inconsistent.)");
		//						System.out.println("exemple "+ j + ": " + exem.toString() + ";\n hypothèse "+ HypoTest.getID()+ " : " + HypoTest.toString());
							}
							
							isConsistent=(isConsistent && test);	
						}
					}
				}
			}
		}
		return isConsistent;
	}

	public synchronized void finish() {
		if (timed) {
			timer.cancel();
		}
		
		if (verbose > 0) {
			// System.out.println();
			System.out.println(new Date().toString() + " - Fin Etape "
					+ expname);
		}

			 System.out.println("FIN :"+this.numSys+" Run");
			 System.out.println();
		this.is.res++;
		enCours = false;
		notifyAll();
		// commBC.enqueue(this,new
		// Message(Message.MESS_FINISH,null,commSystem));
		
		Global.closeLogFile();
		systemThread=null;
		System.exit(0);
	}
	
	public synchronized void start() {

		netw.Init();
		System.out.println("Nb_Agents :"+this.nbAg+" Run :"+(this.run+1)+"/"+this.nbRun);

			Vector<CanalComm> ags = netw.getAllAgents();
			// System.out.println(ags);

			for (int i = 0; i < ags.size(); i++) {
				CanalComm ag = ags.get(i);
				cSyst.send(Message.SYS_START, null, ag);
			}
			pendingStart = 0;
			waitingToSend = false;
			cSyst.send(Message.SYS_SEND_NEXT_EXEMPLE, null, cSyst.getComm());

		if (systemThread == null) {
			systemThread = new Thread(this, "System");
			systemThread.start();
			enCours = true;

			if (timed) {
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimedAgentSystem(), 0, periodms);
			}
			notifyAll();
		}
		if (verbose > 1) {
			System.out.println();
			System.out.println("DEBUT ETAPE");
			// System.out.println();
		}
		Global.salog("#",this.numSys);Global.selog("#",this.numSys);Global.splog("#",this.numSys);
		sendNextGo(null);
	}
	
	class TimedAgentSystem extends TimerTask {
		public void run() {
			sendNextGo(null);
		}
	}
	
	@Override
	public String toString() {
		return ("System");
	}
}
