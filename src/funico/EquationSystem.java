package funico;

import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class EquationSystem implements Cloneable {

    public static final int INTEGER = 1;
    public static final int LIST = 2;
    public static final int BOOLEAN = 3;

    public EquationSystem(int numberOfEquations, String[][] examples, String[] variables, String[] listVariables,
                          String[] functor, Integer[] functorRetType, Map<String, Integer[]> arityFun,
                          String[] terminals, int levels) {
        this.syntaxTree = new RandomSyntaxTree[numberOfEquations];
        this.numberOfEquations = numberOfEquations;
        this.inductionFunArgs = arityFun.get(functor[0]);

        RandBool r = new RandBool(0.5);

        boolean[] bool = r.generate(numberOfEquations);

        // first base equations then recursive equations
        boolean recursive = false, base = false;
        Boolean[] bools = new Boolean[bool.length];
        for(int i = 0; i < bool.length; ++i) {
            bools[i] = bool[i];

            if(bool[i]) {
                recursive = true;
            } else {
                base = true;
            }
        }

        if(!recursive) bools[bools.length - 1] = true;
        if(!base) bools[0] = false;

        Arrays.sort(bools);
        IntUniform randomLevels = new IntUniform(3, levels + 1);

        this.baseEquations = 0;
        for(int i = 0; i < numberOfEquations; ++i) {
            this.syntaxTree[i] = new RandomSyntaxTree(examples, variables, listVariables, functor, functorRetType,
                                                      arityFun, terminals, levels, bools[i]);
            this.baseEquations += !bools[i] ? 1 : 0;
        }
    }

    /**
     * for clone purposes
     */
    public EquationSystem(EquationSystem toClone) {
        this.numberOfEquations = toClone.numberOfEquations;
        this.syntaxTree = new RandomSyntaxTree[this.numberOfEquations];
        this.baseEquations = toClone.baseEquations;

        for(int i = 0; i < this.numberOfEquations; ++i) {
            this.syntaxTree[i] = new RandomSyntaxTree(toClone.syntaxTree[i]);
        }

        this.inductionFunArgs = new Integer[toClone.inductionFunArgs.length];
        System.arraycopy(toClone.inductionFunArgs, 0, this.inductionFunArgs, 0, this.inductionFunArgs.length);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        for(int i = 0; i < this.numberOfEquations; ++i) {
            s.append(this.syntaxTree[i].getPhenotype());
            s.append(";");
        }

        return s.toString().substring(0, s.length() - 1);
    }

    public int getNumberOfEquations() {
        return numberOfEquations;
    }

    public void repair() {
        for(int i = 0; i < this.getNumberOfEquations(); ++i) {
            this.syntaxTree[i].repair();
        }
    }

    /**
    * Function that counts the nodes that have an arity greater or equal.
    * */
    public int countArityBoundedTree(int indexSyntaxTree, int arityMin, int arityMax) {
        RandomSyntaxTree selected = this.syntaxTree[indexSyntaxTree];
        int count = 0;

        Stack<Node> s = new Stack<>();
        // we don't want the root(equal) node
        s.push(selected.getRoot().children[0]);
        s.push(selected.getRoot().children[1]);

        while(!s.empty()) {
            Node current = s.pop();
            for(int i = 0; i < current.children.length; ++i) {
                s.push(current.children[i]);
            }

            int currentArity = current.getArity();
            if(arityMin <= currentArity && currentArity <= arityMax) count++;
        }

        return count;
    }

    public int countByArityTree(int indexSyntaxTree, int arity) {
        return countArityBoundedTree(indexSyntaxTree, arity, arity);
    }

    public Node retrieveNodeBoundedTree(int indexSyntaxTree, int arityMin, int arityMax, int index) {
        RandomSyntaxTree selected = this.syntaxTree[indexSyntaxTree];
        int count = -1;

        LinkedList<Node> queue = new LinkedList<>();
        // we don't want the root(equal) node
        queue.push(selected.getRoot().children[0]);
        queue.push(selected.getRoot().children[1]);

        while(queue.size() != 0) {
            Node current = queue.pop();
            for(int i = 0; i < current.children.length; ++i) {
                queue.push(current.children[i]);
            }

            int currentArity = current.getArity();
            if(arityMin <= currentArity && currentArity <= arityMax) count++;
            if(count == index) return current;
        }

        return null;
    }

    public Node retrieveByArityTree(int indexSyntaxTree, int arity, int index) {
        return retrieveNodeBoundedTree(indexSyntaxTree, arity, arity, index);
    }

    public RandomSyntaxTree getSyntaxTree(int index) {
        return this.syntaxTree[index];
    }

    public void setSyntaxTree(int index, RandomSyntaxTree put) {
        this.syntaxTree[index] = put;
    }

    public int getBaseEquations() {
        return this.baseEquations;
    }

    public Integer[] getInductionFunArgs() {
        return inductionFunArgs;
    }

    private RandomSyntaxTree[] syntaxTree;
    private int numberOfEquations;
    private int baseEquations;
    private Integer[] inductionFunArgs;
}
