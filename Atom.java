package learning.actionLearning.irale;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.ListIterator;


public class Atom {
	public String name;
	public int nbT;
	public Term[] terms;
	
	public Atom(String name,Term[] terms){
		this.name=name;
		this.terms=terms;
	}
	
	public Atom(Atom a) {
		this.name=a.name;
		int i=0,j=a.terms.length;
		this.terms=new Term[j];
		while(i<j){
			this.terms[i]=a.terms[i];
		i++;}
	}

	
	public Subs gen2Vgen(Atom r,Subs sig){
		int i=0;
		Term t1,t2; //(c/V)
	//
	//	System.out.println();sig.print();
	//	System.out.println();this.print();r.print();
	//
		Subs newSig=new Subs();
		while (i<this.terms.length){
			t1=this.terms[i];
			t2=sig.find(t1.name);
			if (t2==null){
				if (sig.revfind(r.terms[i].name)!=null){
					return null;}
				else{
					Term newT;
					if (r.terms[i].type){newT=r.terms[i];}
					else{
						if (t1.name.equals(r.terms[i].name)){newT=new Term(r.terms[i].name,false);}
						else {newT=new Term(r.terms[i].name,true);}}
					newSig.add(t1,newT);
					}}
			else{
				if (!t2.name.equals(r.terms[i].name)){
					return null;}}
			i++;}
	//
	//	System.out.println();newSig.print();System.out.println();
	//
	return newSig;}
	
	
	public Subs gen2Vcheck(Atom r,Subs sig){
		int i=0;
		Term t1,t2; //(c/V)
		Subs newSig=new Subs();
		
		while (i<this.terms.length){
			if( (!r.terms[i].type) && !r.terms[i].name.equals(this.terms[i].name)){return null;}
			t1=this.terms[i];
			t2=sig.find(t1.name);
			if (t2==null){
					if (sig.revfind(r.terms[i].name)!=null){
						return null;}
					else{
							newSig.add(t1,r.terms[i]);
						}}
				else{
					if (!t2.name.equals(r.terms[i].name)){
						return null;}}
				i++;}
	return newSig;}
	
	
	public Subs genVcheck(Atom r,Subs sig){
		int i=0;
		Term t1,t2; //(c/V)
		Subs newSig=sig;
	/*	System.out.println();
		System.out.println("Atom - genVcheck - r ");r.print();System.out.println();
		System.out.println("Atom - genVcheck - ex ");this.print();System.out.println();
		System.out.println("Atom - genVcheck - sig ");sig.print();System.out.println();*/
		if(!r.name.equals(this.name)){return null;}
		while (i<this.terms.length){
			if( (!r.terms[i].type) && !r.terms[i].name.equals(this.terms[i].name)){return null;}
			t1=this.terms[i];
			t2=sig.find(t1.name);
			if (t2==null){
			//	System.out.println("t2(ru supp) == null");
					if (sig.revfind(r.terms[i].name)!=null){
					//	System.out.println("rev(ru) != null");
						return null;}
					else{
						//	System.out.println("rev(ru) == null => newSig");
							newSig.add(t1,r.terms[i]);
						}}
				else{
				//	System.out.println("t2(ru supp) != null");
					if (!t2.name.equals(r.terms[i].name)){
				//		System.out.println("t2 != t1 => null");
						return null;}}
				i++;}
	return newSig;}
	
	public Subs genVgen(Atom r,Subs sig){

		int i=0;
		Term t1,t2; //(c/V)
		Subs newSig=sig;
		while (i<this.terms.length){
			t1=this.terms[i];
		//	this.print();System.out.println(" "+i+" "+t1.name);sig.print();
			t2=sig.find(
					t1.name);
			if (t2==null){
				if (sig.revfind(r.terms[i].name)!=null){
					return null;}
				else{	Term newT;
					if (r.terms[i].type){newT=r.terms[i];}
					else{
						if (t1.name.equals(r.terms[i].name)){newT=new Term(r.terms[i].name,false);}
						else {newT=new Term(r.terms[i].name,true);}}
						newSig.add(t1,newT);}}
			else{if (!t2.name.equals(r.terms[i].name)){
					return null;}}
			i++;}
	return newSig;}
	
	public Atom toVarP(Subs s){
		int i=0,j=this.terms.length;
		Term[] tt=new Term[j];
		while(i<j){
			if(s.nameLi.containsKey(this.terms[i].name)){
				Term t=s.li.get(s.nameLi.get(this.terms[i].name));
				tt[i]=t;}
			else{return null;}
			i++;}
		return new Atom(this.name,tt);}
	
	public Atom toVarPru(Subs s){
		int i=0,j=this.terms.length;
		Term[] tt=new Term[j];
		while(i<j){
			if(s.nameRev.containsKey(this.terms[i].name)){
				Term t=s.nameRev.get(this.terms[i].name);
				tt[i]=t;}
			else{return null;}
			i++;}
		return new Atom(this.name,tt);}
	
	public Atom toVarPruSoft(Subs s){
		int i=0,j=this.terms.length;
		Term[] tt=new Term[j];
		while(i<j){
			if(s.nameRev.containsKey(this.terms[i].name)){
				Term t=s.nameRev.get(this.terms[i].name);
				tt[i]=t;}
			else{tt[i]=this.terms[i];}
			i++;}
		return new Atom(this.name,tt);}
	
	public Atom rev(Subs sig){
		int i=0,j=this.terms.length;
		Term[] tt=new Term[j];
		while (i<j){
			Term t=sig.revLi.get(sig.nameRev.get(this.terms[i].name));
			if(t==null){tt[i]=this.terms[i];}
			else{tt[i]=t;}
				i++;}
		Atom newAtom=new Atom(this.name,tt);
	return newAtom;}
	
	public Atom rev(Term[] v,Term[] c){
		int i=0,j=this.terms.length;
		Term[] tt=new Term[j];
		while (i<j){
			int ii=0,jj=v.length;
			while(ii<jj){
				if(v[ii].name.equals(this.terms[i].name))
					{tt[i]=c[ii];break;}
				ii++;}
				i++;}
		Atom newAtom=new Atom(this.name,tt);
	return newAtom;}
	
	public boolean egal(Atom a){
		if(!this.name.equals(a.name)){return false;}
		int i=0,j=this.terms.length;
		while(i<j){
			if(!this.terms[i].name.equals(a.terms[i].name)){
				return false;}
			i++;}
		return true;}
	
	
	public void print(){
		System.out.print(" ");
		System.out.print(this.name);
		System.out.print("( ");
		int i=0,j=this.terms.length;
		while(i<j){System.out.print(this.terms[i].name+" '"+this.terms[i].type+"' ");i++;}
		System.out.print(")");
		System.out.print(" ");}
	
	
	@Override
	public String toString(){
		String res=this.name+" ";
		int i=0,j=this.terms.length;
		while(i<j){res=res.concat(""+this.terms[i].name+"="+this.terms[i].type+",");i++;}
		res=res.substring(0, res.length()-1);
	return res;}

	public boolean nouveau(Atom a) {
		// TODO Auto-generated method stub
		if(!this.name.equals(a.name)){return false;}
		int tt=this.terms.length;
		if(tt!=a.terms.length){return false;}
		int i=0;
		while(i<tt){
			if(!this.terms[i].name.equals(a.terms[i].name)){return false;}
			i++;}
	return true;}
}
