import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;


public class EvalTest {

	public static void main(String[] args){
		Evaluator eval = new Evaluator();
		try {
			eval.putVariable("cost.s", "100");
			System.out.println(eval.evaluate("1+1+#{cost.s}"));
			
			Pattern p = Pattern.compile("#\\{\\w*\\}");
			Matcher m = p.matcher("1+1+#{cost.s}+#{usa.vp}+#{defcon}");
			while(m.find()){
				System.out.println(m.group());
			}
			
			System.out.println(eval.evaluate("floor(1.7)"));
			System.out.println(eval.evaluate("floor(0.5)"));
			System.out.println(eval.evaluate("min(3-5,18)"));
			System.out.println(eval.evaluate("max(0,-2)"));
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
	}
}
