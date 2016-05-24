package funico.xover;

import funico.EquationSystem;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.random.rngpack.RanMT;
import unalcol.random.util.RandBool;
import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.vector.Vector;

public class EquationXOver extends ArityTwo<EquationSystem> {

    @Override
    public Vector<EquationSystem> apply(EquationSystem one, EquationSystem two) {
        EquationSystem childOne = new EquationSystem(one);
        EquationSystem childTwo = new EquationSystem(two);

        int baseOne = childOne.getBaseEquations(), baseTwo = childTwo.getBaseEquations();

        boolean recursive = new RandBool(0.5).generate();
        IntUniform rOne, rTwo;

        if(recursive) {
            rOne = new IntUniform(baseOne, childOne.getNumberOfEquations());
            rTwo = new IntUniform(baseTwo, childTwo.getNumberOfEquations());
        } else {
            rOne = new IntUniform(0, baseOne);
            rTwo = new IntUniform(0, baseTwo);
        }

        int indexOne = rOne.generate(), indexTwo = rTwo.generate();

        RandomSyntaxTree eqOne = childOne.getSyntaxTree(indexOne);
        RandomSyntaxTree eqTwo = childTwo.getSyntaxTree(indexTwo);

        childOne.setSyntaxTree(indexOne, eqTwo);
        childTwo.setSyntaxTree(indexTwo, eqOne);

        Vector<EquationSystem> toReturn = new Vector<>();
        toReturn.add(childOne);
        toReturn.add(childTwo);

        return toReturn;
    }
}