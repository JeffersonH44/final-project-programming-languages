package funico.xover;

import funico.EquationSystem;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.ArityOne;

/**
 * Created by jefferson on 13/11/15.
 */
public class EquationSwap extends ArityOne<EquationSystem> {

    @Override
    public EquationSystem apply(EquationSystem x) {
        EquationSystem child = new EquationSystem(x);

        IntUniform rand = new IntUniform(child.getNumberOfEquations());
        int a, b;
        a = b = rand.generate();
        while (a == b) b = rand.generate();

        RandomSyntaxTree temp = child.getSyntaxTree(a);
        child.setSyntaxTree(a, child.getSyntaxTree(b));
        child.setSyntaxTree(b, temp);

        return child;
    }
}
