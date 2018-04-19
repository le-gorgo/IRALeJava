package learning.actionLearning.irale;

public class RuSub {
	public String name;
	public Term[] var;
	public Term[] cons;
	public Rule ru;

	
	public RuSub(){
		
	}
	
	
	public void print(){
		System.out.println("plan :"+this.name);
		int i=0,j=var.length;
		while(i<j){
			System.out.print(var[i].name+" ");
			i++;}
		System.out.println();
		i=0;
		while(i<j){
			System.out.print(cons[i].name+" ");
			i++;}
		System.out.println();
	}
}
