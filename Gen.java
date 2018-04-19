package learning.actionLearning.irale;

import java.util.HashMap;
import java.util.LinkedList;

import learning.ExampleMemory;
import learning.actionLearning.Example_AL;

public abstract class Gen {

	abstract LinkedList<Rule> gen(Example_AL ex,int ag, HashMap<Rule,LinkedList<Rule[]>> rules,ExampleMemory<Example_AL> contr_ex,AtomSet ind);

}
