package funico;

import fplearning.interpreter.Evaluator;
import fplearning.interpreter.GoalException;
import fplearning.interpreter.ProgramException;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import unalcol.optimization.OptimizationFunction;

public class EquationSystemFitness extends OptimizationFunction<EquationSystem>{
    public EquationSystemFitness(String[][] examples) {
        this.examples = examples;
    }

    @Override
    public Double apply(EquationSystem equationSystem) {
        double count = 0.0;
        String es = equationSystem.toString();

        for(int i = 0; i < this.examples.length; ++i) {
            try {
                System.out.println("evaluating...");
                String answer = Evaluator.evalue(es, this.examples[i][0], 500);
                System.out.println("complete.");
                if(answer.equals(this.examples[i][1])) count++;
            } catch (ProgramException | GoalException | LexicalException | SyntacticalException ex) {
                return 0.0;
            }
        }

        return count / this.examples.length;
    }

    private String[][] examples;
}
