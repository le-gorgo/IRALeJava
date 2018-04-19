package learning.actionLearning.irale;

public class Effect {
	public AtomSet add;
	public AtomSet del;
	
	public Subs couv(Effect ex,Subs sig){
		if(this.add==null && ex.add==null){return sig;}
		if(this.add==null || ex.add==null){return null;}
		Subs newSig=sig;
		newSig=this.add.genParf(ex.add, newSig);
		if (newSig!=null){
			newSig=this.del.genParf(ex.del, newSig);}
	return newSig;		
}

	public boolean egal(Effect e){
		if(this.add.egal(e.add)){
			if(this.del.egal(e.del)){
				return true;}
			}
	return false;}
	
	public Effect rev(Subs sig){
		AtomSet newAdd=add.rev(sig);
		AtomSet newDel=del.rev(sig);
		Effect ex=new Effect(newAdd,newDel);
	return ex;}
	
	
	
	
	public Effect rev(Term[] v,Term[] c){
		AtomSet newAdd=add.rev(v,c);
		AtomSet newDel=del.rev(v,c);
		Effect ex=new Effect(newAdd,newDel);
	return ex;}
	
	
	
	public Effect(AtomSet add,AtomSet del){
		this.add=add;
		this.del=del;
	}

	public Effect(Effect e) {
		// TODO Auto-generated constructor stub
	this.add=new AtomSet(e.add);
	this.del=new AtomSet(e.del);
	}

	public void print(){
		if(this.add==null){System.out.print("Add=null; ");}
		else{System.out.print("Add{ ");
		this.add.print();
		System.out.print(" }; ");}
		if(this.del==null){System.out.print("Del=null; ");}
		else{System.out.print("Del{ ");
		this.del.print();
		System.out.print(" }");}
	}
	
	@Override
	public String toString(){
		String res=this.add.toString()+"|"+this.del.toString();
	return res;}
	
	
	
}
