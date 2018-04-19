package learning.actionLearning.irale;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import learning.actionLearning.Example_AL;
import learning.actionLearning.environnement.Envi;


public class Global {
	
	public static Envi env;
	
	public static String rep;
	
	private static int contV=1;
	private static int contC=1;
	private static int contR=1;
	private static int contP=1;
	private static int contA=1;
	private static int contRover=1;
	private static int contWaypoint=1;
	private static int contCamera=1;
	private static int contObjective=1;
	private static int contRoverStore=1;
	private static int temp=0;
	private static final Object valueMonitor=new Object();
	private static final Object valueMonitorSusu=new Object();
	private static final Object valueMonitorLog=new Object();
	private static final Object valueMonitorTemp=new Object();
	private static final Object valueMonitorHyp=new Object();
	private static final Object valueMonitorHypInd=new Object();
	private static final Object valueMonitorSondeApp=new Object();
	private static final Object valueMonitorSondeEpi=new Object();
	private static final Object valueMonitorSondePla=new Object();
	private static final Object valueMonitorRover=new Object();
	public static final Object valueMonitorAgent=new Object();
	
	
	public static PrintWriter log;
	public static String logFile;
	public static PrintWriter sondeApp;
	public static String sondeAppFile;
	public static PrintWriter sondeEpi;
	public static String sondeEpiFile;
	public static PrintWriter sondePla;
	public static String sondePlaFile;
	public static PrintWriter hyp;
	public static String hypFile;
	public static PrintWriter hypInd;
	public static String hypIndFile;
	public static int nbAr=0;
	public static HashMap<Integer,LinkedList<Subs>> susu=new HashMap<Integer,LinkedList<Subs>>();
	
	public static void defLogFiles(String rep){
		File fb = new File(rep);
		fb.mkdir();
		logFile="log_file.txt";
		sondeAppFile="sonde_apprentissage.txt";
		sondeEpiFile="sonde_episode.txt";
		sondePlaFile="sonde_planif.txt";
		hypFile="hyp_file.txt";
		hypIndFile="hypInd_file.txt";
		File f;
		f=new File(logFile);
		if(f.exists()){System.out.println("Removing "+logFile);
			f.delete();}
		f=new File(sondeAppFile);
		if(f.exists()){System.out.println("Removing "+sondeAppFile);
			f.delete();}
		f=new File(sondeEpiFile);
		if(f.exists()){System.out.println("Removing "+sondeEpiFile);
			f.delete();}
		f=new File(sondePlaFile);
		if(f.exists()){System.out.println("Removing "+sondePlaFile);
			f.delete();}
		f=new File(hypFile);
		if(f.exists()){System.out.println("Removing "+hypFile);
			f.delete();}
		f=new File(hypIndFile);
		if(f.exists()){System.out.println("Removing "+hypIndFile);
			f.delete();}
	}
	
	public static void closeLogFile(){
		synchronized(valueMonitorLog){}
	}
	
	public static int incTemp(){
		synchronized(valueMonitorTemp){return temp++;}
	}
	
	public static void resTemp(){
		synchronized(valueMonitorTemp){temp=0;}
	}
	
	public static void log(String mess,int numR){
		synchronized(valueMonitorLog){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+logFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}}
	}
	
	public static void hlog(String mess,int numR){
		synchronized(valueMonitorHyp){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+hypFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
	}
	
	public static void hilog(String mess,int numR){
		synchronized(valueMonitorHypInd){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+hypIndFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
	}
	
	public static void salog(String mess,int numR){
		synchronized(valueMonitorSondeApp){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+sondeAppFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
	}
	
	public synchronized static void selog(String mess,int numR){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+sondeEpiFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void splog(String mess,int numR){
		synchronized(valueMonitorSondePla){
			try {
				log = new PrintWriter(new FileWriter(rep+"/"+numR+"_"+sondePlaFile, true));
				log.println(mess);
				log.close();} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
	}
	
	public static int getContV(){
		synchronized(valueMonitor){
			return contV++;}
	}
	
	
	public static String noNumber(String nam){
		String name = nam.replaceAll("[0-9]","");
	return name;}
	
	public static String getContTR(String nam){
		String name = Global.noNumber(nam);
		if(name.equals("rover")){
		synchronized(valueMonitorRover){
			return "ROVER"+(contRover++);}}
		if(name.equals("camera")){
			synchronized(valueMonitorRover){
				return "CAMERA"+(contCamera++);}}
		if(name.equals("objective")){
		synchronized(valueMonitorRover){
			return "OBJECTIVE"+(contObjective++);}}
		if(name.equals("roverstore")){
			synchronized(valueMonitorRover){
				return "STORE"+(contRoverStore++);}}
		if(name.equals("waypoint")){
			synchronized(valueMonitorRover){
				return "WAYPOINT"+(contWaypoint++);}}
		if(name.equals("general")){
				return "GENERAL";}
		if(name.equals("low_res")){
			return "MODE1";}
		if(name.equals("colour")){
			return "MODE2";}
		if(name.equals("high_res")){
			return "MODE3";}
	return null;}
	
	
	public static int getContC(){
		synchronized(valueMonitor){
			return contC++;
		}
	}
	
	public static int getContR(){
		synchronized(valueMonitor){
			return contR++;
		}	
	}
	
	public static int getContP(){
		synchronized(valueMonitor){
			return contP++;
		}	
	}
	
	public static int getContA(){
		synchronized(valueMonitor){
			return contA++;
		}	
	}
	
	
	public static void susuAdd(Subs s,int a){
		synchronized(Global.valueMonitorSusu){
		Subs ss=new Subs();
		ss.li=null;
		if(!Global.susu.containsKey(a)){
			Global.susu.put(a, new LinkedList<Subs>());
		}
		ListIterator<Subs> lisu=Global.susu.get(a).listIterator();
		while(lisu.hasNext()){
			if (lisu.next().egal(s)){return;}}
		Global.susu.get(a).add(s);}
	}

	public static void susuAdd2(Subs s,int a){
		synchronized(Global.valueMonitorSusu){
		if(!Global.susu.containsKey(a)){
			Global.susu.put(a, new LinkedList<Subs>());
			Global.susu.get(a).add(new Subs());}
		Iterator<Entry<Term,Term>> lis=s.li.entrySet().iterator();
		while(lis.hasNext()){
			Entry<Term,Term> ens=lis.next();
			Term t1s=ens.getKey(),t2s=ens.getValue();
			ListIterator<Subs> lisu=Global.susu.get(a).listIterator();
			boolean b=true;
			while(lisu.hasNext()){
				Subs su=lisu.next();
				if (su.egal(s)){return;}
				Iterator<Entry<Term,Term>> li=su.li.entrySet().iterator();
				while(li.hasNext()){
					Entry<Term,Term> en=li.next();
					Term t1=en.getKey(),t2=en.getValue();
					if (t1s.name.equals(t1.name)){b=false;break;}
					if (t2s.name.equals(t2.name)){b=false;break;}}
				if (b){
					Subs d=su.copie();
					lisu.add(d);
					su.add(t1s, t2s);}}
			}
		}}
	
	public static int makeRandInt(int min,int max){
		int res=(int) (min+Math.round(Math.random()*(max-min)));
		return res;}

	
}