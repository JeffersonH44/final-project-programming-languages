package funico;

import unalcol.search.space.Space;

import java.util.Map;

public class EquationsSpace extends Space<EquationSystem> {

    public EquationsSpace(int numberOfEquations, String[][] examples, String[] variables, String[] listVariables,
                          String[] functionsName, Integer[] functionsRetType, Map<String, Integer[]> arityFun,
                          String[] terminals, int levels) {

        this.numberOfEquations = numberOfEquations;
        this.examples = examples;
        this.variables = variables;
        this.listVariables = listVariables;
        this.functionsName = functionsName;
        this.functionsRetType = functionsRetType;
        this.arityFun = arityFun;
        this.terminals = terminals;
        this.levels = levels;
    }
    @Override
    public boolean feasible(EquationSystem x) {
        return true;
    }

    @Override
    public double feasibility(EquationSystem x) {
        return 0.0;
    }

    @Override
    public EquationSystem repair(EquationSystem x) {
        // take right node
        x.repair();

        return x;
    }

    @Override
    public EquationSystem get() {
        EquationSystem es = new EquationSystem(numberOfEquations, examples, variables, listVariables, functionsName,
                                                functionsRetType, arityFun, terminals, levels);
        return es;
    }

    private int numberOfEquations;
    private String[][] examples;
    private String[] variables;
    private String[] listVariables;
    private String[] functionsName;
    private Integer[] functionsRetType;
    private Map<String, Integer[]> arityFun;
    private String[] terminals;
    private int levels;
}
