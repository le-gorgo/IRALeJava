package learning.actionLearning.irale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;
import learning.actionLearning.Example_AL;


public class AtomSet {
	public String name;
	public HashMap<String,LinkedList<Atom>> atoms=new HashMap<String,LinkedList<Atom>>();
	private Global go=new Global();
	
	public AtomSet(){
		
	}
	
	public AtomSet(String name){
		this.name=name;
	}
	
	//si contient au moins un element de la liste.
	public boolean contient(LinkedList<Atom> la){
		ListIterator<Atom> lia=la.listIterator();
		while(lia.hasNext()){
			Atom a=lia.next();
			if(this.atoms.containsKey(a.name)){
				LinkedList<Atom> llia=this.atoms.get(a.name);
				ListIterator<Atom> li=llia.listIterator();
				while(li.hasNext()){
					if(a.egal(li.next())){
						return true;}
				}
			}
		}
	return false;}
	
	public boolean egal(AtomSet as){
		Subs s=this.genParf(as, new Subs());
		if (s==null){return false;}
		return s.aToa();}
	
	public AtomSet(AtomSet s){
		Iterator<Entry<String, LinkedList<Atom>>> ita=s.atoms.entrySet().iterator();
		this.name=s.name;
		while(ita.hasNext()){
			Entry<String, LinkedList<Atom>> eas=ita.next();
			ListIterator<Atom> lia=eas.getValue().listIterator();
			while(lia.hasNext()){
				Atom a=new Atom(lia.next());
				this.addAtom(a);
			}
		}
	}
	
	public AtomSet rev(Subs sig){
		Iterator<Entry<String, LinkedList<Atom>>> itR=this.atoms.entrySet().iterator();
		AtomSet newAs=new AtomSet(this.name);
		while(itR.hasNext()){
			ListIterator<Atom> aRL=itR.next().getValue().listIterator();
			while(aRL.hasNext()){
				Atom newAEx=aRL.next().rev(sig);
				newAs.addAtom(newAEx);}}
		return newAs;}
	
	
	
	public AtomSet rev(Term[] v,Term[] c){
		Iterator<Entry<String, LinkedList<Atom>>> itR=this.atoms.entrySet().iterator();
		AtomSet newAs=new AtomSet(this.name);
		while(itR.hasNext()){
			ListIterator<Atom> aRL=itR.next().getValue().listIterator();
			while(aRL.hasNext()){
				Atom newAEx=aRL.next().rev(v,c);
				newAs.addAtom(newAEx);}}
		return newAs;}
	
	
	public void addAtom(Atom a){
		LinkedList<Atom> as;
		if(atoms.containsKey(a.name)){
			as=atoms.get(a.name);
			as.add(a);}
		else{
			as= new LinkedList<Atom>();
			as.add(a);}
		atoms.put(a.name, as);
	}
	
	
	public AtomSet genVquick(Example_AL ex,Rule ru, LinkedList<Example_AL> contrex,AtomSet ind){
		LinkedList<Atom> liEx=new LinkedList<Atom>();
		ru.s=this;
		Iterator<Entry<String, LinkedList<Atom>>> it=ex.s.atoms.entrySet().iterator();
		while(it.hasNext()){
			liEx.addAll(it.next().getValue());}
		LinkedList<Atom> liR=new LinkedList<Atom>();
		it=ru.s.atoms.entrySet().iterator();
		while(it.hasNext()){
			liR.addAll((LinkedList<Atom>)it.next().getValue());}
		Subs sa=ru.aCouv(ex, new Subs());
		return genRecVquick(liEx,liR,ex,ru,contrex,ind,ru.e.couv(ex.e, sa),true);}
	
	public AtomSet genVquickS(Example_AL ex,Rule ru, Subs sa,LinkedList<Example_AL> contrex,AtomSet ind,boolean b){
		LinkedList<Atom> liEx=new LinkedList<Atom>();
		Iterator<Entry<String, LinkedList<Atom>>> it=ex.s.atoms.entrySet().iterator();
		while(it.hasNext()){
			liEx.addAll(it.next().getValue());}
		LinkedList<Atom> liR=new LinkedList<Atom>();
		it=ru.s.atoms.entrySet().iterator();
		while(it.hasNext()){
			liR.addAll((LinkedList<Atom>)it.next().getValue());}
		return genRecVquick(liEx,liR,ex,ru,contrex,ind,sa,b);}

	public AtomSet genRecVPro(LinkedList<Atom> liEx,LinkedList<Atom> liR,Example_AL ex,Rule ru,LinkedList<Example_AL> contrex,AtomSet ind,Subs s0,boolean b){
		if (liR.isEmpty()){
			return s0.check(contrex,ind,ex,ru);}
	//	System.out.println("AtomSet - genRecVquick - start!");
		LinkedList<Atom> liExf=new LinkedList<Atom>();
		
		Atom r=liR.removeFirst();
		AtomSet resAS=null;
		Subs res=null;
		
		while(!liEx.isEmpty()){
			Atom e=liEx.removeFirst();
			if(r.name.equals(e.name)){
				if(b){res=e.genVgen(r, s0.copie());}else{res=e.genVcheck(r, s0.copie());}
				if (res!=null){
					LinkedList<Atom> liRs=new LinkedList<Atom>();
					LinkedList<Atom> liExs=new LinkedList<Atom>();
					liRs.addAll(liR);
					liExs.addAll(liEx);liExs.addAll(liExf);
					resAS=genRecVPro(liExs,liRs,ex,ru,contrex,ind,res,b);
					if(resAS!=null){return resAS;}}}
			liExf.add(e);}
		
		return genRecVPro(liExf,liR,ex,ru,contrex,ind,s0.copie(),b);}

	public AtomSet genRecVquick(LinkedList<Atom> liEx,LinkedList<Atom> liR,Example_AL ex,Rule ru,LinkedList<Example_AL> contrex,AtomSet ind,Subs s0,boolean b){
		if (liEx.isEmpty()){
			return s0.check(contrex,ind,ex,ru);}
	//	System.out.println("AtomSet - genRecVquick - start!");
		Atom e=liEx.removeFirst();
		LinkedList<Atom> liRf=(LinkedList<Atom>) liR.clone();

			Subs res=null;
			ListIterator<Atom> liit=liR.listIterator();
			liR=new LinkedList<Atom>();
			
			while(liit.hasNext()){
				Atom ar=liit.next();
				if(ar.name.equals(e.name)){
					if(b)
						{res=e.genVgen(ar, s0.copie());}
					else{
						res=e.genVcheck(ar, s0.copie());}
					if (res==null){liR.add(ar);continue;}
					while(liit.hasNext()){
						liR.add(liit.next());}}
				else{liR.add(ar);}
			}
		
		while(res!=null){
			
			AtomSet as2=genRecVquick(liEx,(LinkedList<Atom>) liR.clone(),ex,ru,contrex,ind,res,b);
			if(as2!=null){return as2;}
				res=null;
				liit=liR.listIterator();
				liR=new LinkedList<Atom>();
				
				while(liit.hasNext()){
					Atom ar=liit.next();
					if(ar.name.equals(e.name)){
						if(b){res=e.genVgen(ar, s0.copie());}
						else{res=e.genVcheck(ar, s0.copie());}
					if (res==null){liR.add(ar);continue;}
					while(liit.hasNext()){
						liR.add(liit.next());}}
					else{liR.add(ar);}
				}
		}
	
		return genRecVquick(liEx,liRf,ex,ru,contrex,ind,s0.copie(),b);}

	
	//generalisation sans perte d'atom ni dans la règle, ni dans l'exemple!
	public Subs genParf(AtomSet ex,Subs sig){
	Subs newSig=sig;
	if(ex.atoms.size()!=this.atoms.size()){return null;}
	Iterator<Entry<String, LinkedList<Atom>>> itL=ex.atoms.entrySet().iterator();
	while (itL.hasNext()){
		Entry<String, LinkedList<Atom>> nomAl=itL.next();
			LinkedList<Atom> aEx=ex.atoms.get(nomAl.getKey());
			LinkedList<Atom> aR=this.atoms.get(nomAl.getKey());
			if (aEx==null || aR==null){return null;}
			int nbA=aEx.size();
			if(nbA!=aR.size()){return null;}
			LinkedList<Atom> aRc=(LinkedList<Atom>) aR.clone();
			LinkedList<Atom> aExc=(LinkedList<Atom>) aEx.clone();
			Subs tempSig=genRecParf(aExc,aRc,newSig,nbA,0);
			if(tempSig==null){
				return null;}
			else{newSig=tempSig;}}
	return newSig;}
	
	private LinkedList<Atom> concat(LinkedList<Atom> l1,LinkedList<Atom> l2){
		while(!l2.isEmpty()){
			l1.addLast(l2.removeFirst());}
	return l1;}
	
	private LinkedList<Atom> concat2(ListIterator<Atom> l1,LinkedList<Atom> l2){
		while(l1.hasNext()){
			l2.addFirst(l1.next());}
	return l2;}
	
	//regle couvre completement l'exemple.
	public Subs couvPart(AtomSet ex,Subs sig){
		LinkedList<Atom> liEx=new LinkedList<Atom>();
		Iterator<Entry<String, LinkedList<Atom>>> it=ex.atoms.entrySet().iterator();
		while(it.hasNext()){
			liEx.addAll(it.next().getValue());}
		LinkedList<Atom> liR=new LinkedList<Atom>();
		it=this.atoms.entrySet().iterator();
		while(it.hasNext()){
			liR.addAll((LinkedList<Atom>)it.next().getValue());}
		return couvRecPart(liEx,liR,sig);}
	
	public Subs couvRecPart(LinkedList<Atom> liEx,LinkedList<Atom> liR,Subs s0){
		if (liR.isEmpty()){
			return s0;}
		Subs s=s0.copie();
		LinkedList<Atom> liE=(LinkedList<Atom>) liEx.clone();
		LinkedList<Atom> liEr=new LinkedList<Atom>();
		LinkedList<Atom> liRR=(LinkedList<Atom>) liR.clone();
		Atom r=liRR.removeFirst();
		Subs res=null;
		while(!liE.isEmpty()){
			Atom ae=liE.removeFirst();
			if(!ae.name.equals(r.name)){continue;}
			res=ae.genVcheck(r, s);
			s=s0.copie();
			if (res==null){continue;}
			Subs s1=couvRecPart(liEx,liRR,res);
			if(s1!=null){return s1;}
			}
		return null;}
	

//s'arrete a la premiere converture parfaite des atoms de même nom.
	public Subs genRecParf(LinkedList<Atom> liE,LinkedList<Atom> liR,Subs sig,int nbA,int cont){
		if (cont==nbA){return sig;}
		Subs newSig=sig;
		Atom aE=liE.removeFirst();
		LinkedList<Atom> tempLi=new LinkedList<Atom>();
		while (!liR.isEmpty()){
			Atom aR=liR.removeFirst();
			Subs tempSig=aE.genVgen(aR, newSig);
			if (tempSig==null){
				tempLi.add(aR);}
			else{cont++;
				tempSig=genRecParf(liE,concat(liR,tempLi),tempSig,nbA,cont);
				if(tempSig!=null){
					return tempSig;}}}
	return null;}
	
	

/*//couverture partiel de l'example.
	public Subs couvRecPart(LinkedList<Atom> liR,LinkedList<Atom> liE,Subs sig,int nbA,int cont){
		if (cont==nbA){return sig;}
		Subs newSig=sig;
		Atom aR=liR.removeFirst();
		LinkedList<Atom> tempLi=new LinkedList<Atom>();
		while (!liE.isEmpty()){
			Atom aE=liE.removeFirst();
			Subs tempSig=aE.genVcheck(aR, newSig);
			if (tempSig==null){
				tempLi.add(aE);}
			else{cont++;
				tempSig=couvRecPart(liR,concat(liE,tempLi),tempSig,nbA,cont);
				if(tempSig!=null){
					return tempSig;}}}
	return null;}*/
	
	
	public LinkedList<AtomSet> gen(AtomSet ex,Subs sig,int ag,boolean vers){
		Global.susu.remove(ag);
		LinkedList<Subs> liSup= this.genMalin(ex,sig,ag,vers);
	//
	//	System.out.println();System.out.println("AtomSet - gen - liSup");Subs.llprint(liSup);
	//	
		LinkedList<AtomSet> res = new LinkedList<AtomSet>(); int i=0;
		ListIterator<Subs> sl=liSup.listIterator();
		while(sl.hasNext()){
			AtomSet newAT=new AtomSet("gen"+i);i++;
			Subs s=sl.next();
			Iterator<Entry<String, LinkedList<Atom>>> r=this.atoms.entrySet().iterator();
			while(r.hasNext()){
				Entry<String, LinkedList<Atom>> enr=r.next();
				ListIterator<Atom> arl = enr.getValue().listIterator();
				while(arl.hasNext()){
					Atom ar=arl.next();
					Atom aa=ar.toVarPru(s);
					if(ex.atoms.containsKey(enr.getKey())){
						ListIterator<Atom> aexl = ex.atoms.get(enr.getKey()).listIterator();
						while(aexl.hasNext()){
							Atom ae=aexl.next().toVarP(s);
							if (aa!=null && ae!=null && aa.egal(ae)){
								newAT.addAtom(aa);break;}
						}
					}
				}
			}
			res.add(newAT);
		}
	return res;}
		
	public LinkedList<Subs> genMalin(AtomSet ex, Subs sig, int ag,boolean vers) {
		
	//	System.out.println("sig ");sig.print();System.out.println();
	//	System.out.println("rule ");this.print();System.out.println();
	//	System.out.println("ex ");ex.print();
		
		Iterator<Entry<String, LinkedList<Atom>>> itL=this.atoms.entrySet().iterator();
		LinkedList<Subs> subPoss=new LinkedList<Subs>();
		while (itL.hasNext()){
			Entry<String,LinkedList<Atom>> etSet=itL.next();
				LinkedList<Atom> RR=etSet.getValue();
				LinkedList<Atom> itEx=ex.atoms.get(etSet.getKey());
	//			System.out.println(etSet.getKey());
				if(itEx!=null)
					{genRec2((LinkedList<Atom>)RR.clone(),(LinkedList<Atom>)itEx.clone(),sig,ag,vers);}
			}
	if(Global.susu.containsKey(ag)){
		subPoss.addAll(Global.susu.get(ag));}
	ListIterator<Subs> lit=subPoss.listIterator();
	while(lit.hasNext()){
		Subs ss=lit.next();
		Iterator<Entry<Term,Term>> isig=sig.li.entrySet().iterator();
		while(isig.hasNext()){
			Entry<Term,Term> esig=isig.next();
			ss.add(esig.getKey(), esig.getValue());
		}
	}
	subPoss.add(sig);
//
//	System.out.println();System.out.println("AtomSet - genMalin - subPoss no reduc");Subs.llprint(subPoss);
//
	return reduc(subPoss);
	//return subPoss;
	}
	
	public static boolean egal(LinkedList<Atom> l1,LinkedList<Atom> l2){
		HashSet<Atom> h1=new HashSet<Atom>(l1);
		int conth1=0;
		ListIterator li2=l2.listIterator();
		while(li2.hasNext()){
			if(!h1.contains(li2.next())){return false;}
			conth1++;}
		if(conth1==h1.size()){return true;}
		return false;}
	
	public void genRec2(LinkedList<Atom> lR,LinkedList<Atom> lE,Subs sig,int ag,boolean vers){
	/*	System.out.println("AtomSet - genRec2 - traitement");
		sig.print();System.out.println();
		AtomSet.llprint(lE);System.out.println();
		AtomSet.llprint(lR);System.out.println();
		Subs.llprint(Global.susu.get(ag));*/
		Subs newSig=sig.copie();
		if (!lR.isEmpty()){
			Atom aR=lR.removeFirst();
			LinkedList<Atom> tempLi=new LinkedList<Atom>();
			while (!lE.isEmpty()){
				Atom aE=lE.removeFirst();
				Subs tempSig=null;
				if(vers){tempSig=aE.gen2Vgen(aR, sig);}
				else{tempSig=aE.gen2Vcheck(aR, sig);}
				if (tempSig!=null){
					Global.susuAdd2(tempSig,ag);
					genRec2((LinkedList<Atom>)lR.clone(),concat((LinkedList<Atom>)lE.clone(),tempLi),newSig,ag,vers);}
				tempLi.add(aE);
				genRec2((LinkedList<Atom>)lR.clone(),concat((LinkedList<Atom>)lE.clone(),tempLi),newSig,ag,vers);
				}
			}
	}

	public LinkedList<Subs> reduc(LinkedList<Subs> subPoss){
		LinkedList<Subs> res=new LinkedList<Subs>();
		ListIterator<Subs> l=subPoss.listIterator();
		Subs t,s=null;
		if(l.hasNext()){res.add(l.next());}
		while (l.hasNext()){
			t=l.next();
			LinkedList<Subs> lres=new LinkedList<Subs>();
			lres.addAll(res);
			res=new LinkedList<Subs>();
			boolean b=true;
				while(!lres.isEmpty()){
					s=lres.removeFirst();
					int r=t.couv(s);
					if(r==0){res.add(s);}
					if(r==2 || r==1){res.add(t);b=false;}
				}
				if(b && s!=null){res.add(s);}
			}
	return res;}
		
	
	public void print(){
		Iterator<Entry<String, LinkedList<Atom>>> itR=this.atoms.entrySet().iterator();
		while(itR.hasNext()){
			Entry<String, LinkedList<Atom>> ent=itR.next();
			ListIterator<Atom> liR=ent.getValue().listIterator();
			while (liR.hasNext()){
				liR.next().print();}}
	}
	
	@Override
	public String toString(){
		String res="";
		Iterator<Entry<String, LinkedList<Atom>>> itR=this.atoms.entrySet().iterator();
		while(itR.hasNext()){
			Entry<String, LinkedList<Atom>> ent=itR.next();
			ListIterator<Atom> liR=ent.getValue().listIterator();
			while (liR.hasNext()){
				Atom a=liR.next();
				res=res.concat(a.toString()+":");}}
		if(res.length()>2){res=res.substring(0, res.length()-1);}
	return res;}
	
	public AtomSet toVarPru(Subs s){
		AtomSet newAS=new AtomSet();
		Iterator<Entry<String,LinkedList<Atom>>> et=this.atoms.entrySet().iterator();
		while(et.hasNext()){
			Entry<String, LinkedList<Atom>> ent=et.next();
			ListIterator<Atom> liR=ent.getValue().listIterator();
			while(liR.hasNext()){
				newAS.addAtom(liR.next().toVarPru(s));
			}
		}
	return newAS;}
	
	public AtomSet toVarPruSoft(Subs s){
		AtomSet newAS=new AtomSet();
		Iterator<Entry<String,LinkedList<Atom>>> et=this.atoms.entrySet().iterator();
		while(et.hasNext()){
			Entry<String, LinkedList<Atom>> ent=et.next();
			ListIterator<Atom> liR=ent.getValue().listIterator();
			while(liR.hasNext()){
				newAS.addAtom(liR.next().toVarPruSoft(s));
			}
		}
	return newAS;}
	
	/*public AtomSet inter(AtomSet as){
		AtomSet res =new AtomSet();
		Iterator<Entry<String, LinkedList<Atom>>> itA=this.atoms.entrySet().iterator();
		while (itA.hasNext()){
			Entry<String,LinkedList<Atom>> ent=itA.next();
			ListIterator<Atom> liA=ent.getValue().listIterator();
			LinkedList<Atom> lias=as.atoms.get(ent.getKey());
			while (liA.hasNext()){
				Atom a=liA.next();
				boolean b=false;
				ListIterator<Atom> liA2=lias.listIterator();
				while (liA2.hasNext()){
					if (a.egal(liA2.next())){b=true;break;}
				}
				if (b){res.addAtom(a);b=false;}
			}
		}
	return res;}*/
	
	public static void llprint(LinkedList<Atom> s){
		ListIterator<Atom> ss=s.listIterator();
		System.out.println();
		System.out.println("Atoms:");
		while (ss.hasNext()){
			ss.next().print();
			
		}
		System.out.println();
	}
	
	public static void llprintS(LinkedList<AtomSet> s){
		ListIterator<AtomSet> ss=s.listIterator();
		System.out.println();
		System.out.println("Set:");
		while (ss.hasNext()){
			ss.next().print();
			System.out.println();
			
		}
		System.out.println();
	}

	public Atom FindNew(Atom a) {
		// TODO Auto-generated method stub
		if(!this.atoms.containsKey(a.name)){return null;}
			ListIterator<Atom> it=this.atoms.get(a.name).listIterator();
		while(it.hasNext()){
			Atom suspect=it.next();
			if(suspect.nouveau(a))
				{return suspect;}
		}
	return null;}

	public AtomSet maj(AtomSet s) {
		// TODO Auto-generated method stub
		AtomSet as=new AtomSet();
		Iterator<LinkedList<Atom>> it=this.atoms.values().iterator();
		while(it.hasNext()){
			ListIterator<Atom> li=it.next().listIterator();
			while(li.hasNext()){
				Atom ali=li.next();
				Atom newA=s.FindNew(ali);
				if(newA!=null){as.addAtom(newA);}
			}
		}
		return as;
	}

}
