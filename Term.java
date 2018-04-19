package learning.actionLearning.irale;

public class Term {
	public String name;
	public Boolean type; //true si variable, false si constante.
	
	public Term(boolean type){
		if(type){
			this.name="V_"+Global.getContV();
			this.type=type;}
		else{
			this.name="C_"+Global.getContC();
			this.type=type;}
			
	}
	
	public Term(String name,boolean type){
		if(type){
			this.name=name;
			this.type=type;}
		else{
			this.name=name;
			this.type=type;}
			
	}

}
