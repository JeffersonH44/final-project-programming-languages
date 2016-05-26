package funico.xover;

import funico.EquationSystem;
import funico.Node;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.vector.Vector;

/**
 * Operador que toma una rama de cada sistema de ecuaciones, escogiendo de manera
 * aleatoria un árbol sintáctico de cada sistema, y aplica el cruce con estas ramas.
 */
public class BranchXOver extends ArityTwo<EquationSystem> {

    @Override
    public Vector<EquationSystem> apply(EquationSystem elem1, EquationSystem elem2) {
        Vector<EquationSystem> toReturn = new Vector<>();
        toReturn.add(new EquationSystem(elem1));
        toReturn.add(new EquationSystem(elem2));

        boolean recursive = new RandBool(0.5).generate();
        Node[] branch = new Node[2];
        Integer[] args = elem1.getInductionFunArgs();

        Vector<Vector<Integer>> indexes = new Vector<>();
        for(int i = 0; i < 4; ++i) indexes.add(new Vector<>());

        for(int i = 0; i < args.length; ++i) {
            if(args[i] == EquationSystem.INTEGER) indexes.get(EquationSystem.INTEGER).add(i);
            else if(args[i] == EquationSystem.BOOLEAN) indexes.get(EquationSystem.BOOLEAN).add(i);
            else indexes.get(EquationSystem.LIST).add(i);
        }

        // arguments with at least 1 possible way
        IntUniform rType = new IntUniform(1, 4);
        int typeIndex;
        do {
            typeIndex = rType.generate();
        } while(indexes.get(typeIndex).size() == 0);

        for(int i = 0; i < branch.length; ++i) {
            EquationSystem currentEq = toReturn.get(i);

            int lowerLimit = recursive ? currentEq.getBaseEquations() : 0;
            int upperLimit = recursive ? currentEq.getNumberOfEquations() : currentEq.getBaseEquations();

            int index = new IntUniform(lowerLimit, upperLimit).generate();
            RandomSyntaxTree selectedTree = currentEq.getSyntaxTree(index);

            index = new IntUniform(2).generate();
            branch[i] = recursive ? selectedTree.getRoot().children[index] : selectedTree.getRoot();
        }

        IntUniform rIndex = new IntUniform(indexes.get(typeIndex).size());
        int indexOne = indexes.get(typeIndex).get(rIndex.generate());
        int indexTwo = indexes.get(typeIndex).get(rIndex.generate());

        if(recursive) {
            swapBranches(branch[0], branch[1], indexOne, indexTwo);
        } else {
            boolean result = new RandBool(0.5).generate();
            if(result) {
                swapBranches(branch[0], branch[1], 1, 1);
            } else {
                branch[0] = branch[0].children[0];
                branch[1] = branch[1].children[0];
                swapBranches(branch[0], branch[1], indexOne, indexTwo);
            }
        }

        return toReturn;
    }

    private void swapBranches(Node one, Node two, int indexOne, int indexTwo) {
        Node temp = one.children[indexOne];
        one.children[indexOne] = two.children[indexTwo];
        two.children[indexTwo] = temp;
    }
}
