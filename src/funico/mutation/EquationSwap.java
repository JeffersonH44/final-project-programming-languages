package funico.mutation;

import funico.EquationSystem;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.random.integer.RandInt;
import unalcol.random.util.RandBool;
import unalcol.search.space.ArityOne;

/**
 * Mutación que cambia la prioridad de una ecuación en el sistema de ecuaciones,
 * se aplica sobre ecuaciones base o ecuaciones recursivas.
 */
public class EquationSwap extends ArityOne<EquationSystem> {

    @Override
    public EquationSystem apply(EquationSystem x) {
        EquationSystem eq = new EquationSystem(x);

        int base = eq.getBaseEquations();
        int recursive = eq.getNumberOfEquations() - base;
        int index, otherIndex;

        if(base == 1 && recursive == 1) {
            return eq;
        }

        if(base == 1 || recursive == 1) {
            index = (base == 1) ? 1 : 0;
        } else {
            index = new RandBool(0.5).generate() ? base : 0;
        }

        IntUniform r;
        // base cases
        if(index == 0) {
            r = new IntUniform(0, base);
        } else {
            r = new IntUniform(base, eq.getNumberOfEquations());
        }

        index = r.generate();
        otherIndex = index;

        while(index == otherIndex) {
            otherIndex = r.generate();
        }

        RandomSyntaxTree elem = eq.getSyntaxTree(index);
        RandomSyntaxTree otherElem = eq.getSyntaxTree(otherIndex);

        eq.setSyntaxTree(index, otherElem);
        eq.setSyntaxTree(otherIndex, elem);

        return eq;
    }
}
