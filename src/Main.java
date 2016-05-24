/**
 * Created by Jefferson on 23/04/2016.
 */

import funico.EquationSystem;
import funico.EquationSystemFitness;
import funico.EquationsSpace;
import funico.RandomSyntaxTree;
import funico.mutation.EquationSwap;
import funico.xover.BranchXOver;
import funico.xover.EquationXOver;
import unalcol.evolution.haea.HAEA;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.population.variation.ArityTwo;
import unalcol.search.population.variation.Operator;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.space.ArityOne;
import unalcol.search.space.Space;
import unalcol.types.collection.vector.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final int ID = 0, ARGS = 1;

    public static int getType(String element) {
        if(element.matches("[0-9]+")) return EquationSystem.INTEGER;
        else if(element.matches("false|true")) return EquationSystem.BOOLEAN;
        else if(element.startsWith("[")) return EquationSystem.LIST;
        return -1;
    }

    public static void main(String[] args) {
        String[][] examples = {
                {"geq(0,1)", "false"},
                {"geq(0,0)", "true"},
                {"geq(1,0)", "true"},
                {"geq(1,1)", "true"},
                {"geq(1,2)", "false"},
                {"geq(2,1)", "true"},
                {"geq(2,5)", "false"},
                {"geq(5,2)", "true"},
                {"geq(3,3)", "true"}
        };

        /*String[][] examples1 = {
                {"geq(1,1)", "2"},
                {"geq(3,5)", "8"},
                {"geq(10, 1)", "11"},
                {"geq(1,2)", "3"},
                {"geq(18,2)", "20"},
                {"geq(3,8)", "11"},
                {"geq(7,5)", "12"},
                {"geq(12,2)", "14"},
                {"geq(3,3)", "6"}
        };*/

        String a = examples[0][0];
        a = a.replace(")", "");
        a = a.replace(" ", "");
        String splitted[] = a.split("\\(");

        String toDeduce = splitted[0];

        // available functions to use
        String[] functionsName = {toDeduce, "s"};
        Integer[] functionsRetType = {getType(examples[0][1]), EquationSystem.INTEGER};

        String[] arguments = splitted[1].split(",(?![^(\\[\\])]*\\])", -1);

        Map<String, Integer[]> arityFun = new HashMap<>();
        arityFun.put("s", new Integer[]{EquationSystem.INTEGER});

        Integer[] argTypes = new Integer[arguments.length];
        for(int i = 0 ; i < arguments.length; ++i) {
            argTypes[i] = getType(arguments[i]);
        }
        arityFun.put(toDeduce, argTypes);

        String[] variables = new String[arguments.length];
        for(int i = 0; i < variables.length; ++i) {
            variables[i] = "" + (char)('D' + i);
        }

        System.out.println(Arrays.toString(variables));

        String[] listVariables = {"A", "B", "C"};
        String[] terminals = {"0", "1", "true", "false"};
        int maxEquations = 3;
        int levels = 4;

        Space<EquationSystem> space = new EquationsSpace(maxEquations, examples, variables, listVariables,
                functionsName, functionsRetType, arityFun, terminals, levels);

        /*EquationSystem eq = space.get();
        RandomSyntaxTree tree = eq.getSyntaxTree(0);

        tree.doTest();*/

        // Optimization function
        OptimizationFunction<EquationSystem> function = new EquationSystemFitness(examples);
        Goal<EquationSystem> goal = new OptimizationGoal<>(function, false, 1.0);

        ArityTwo<EquationSystem> exo = new EquationXOver();
        ArityOne<EquationSystem> es = new EquationSwap();
        ArityTwo<EquationSystem> bxo = new BranchXOver();

        @SuppressWarnings("unchecked")
        Operator<EquationSystem>[] opers = (Operator<EquationSystem>[])new Operator[3];
        opers[0] = exo;
        opers[1] = es;
        opers[2] = bxo;

        int POPSIZE = 100;
        int MAXITERS = 100;

        HaeaOperators<EquationSystem> operators = new SimpleHaeaOperators<>(opers);

        Selection<EquationSystem> tournament = new Tournament<>(4);
        Selection<EquationSystem> elitism = new Elitism<>(.9, .1);

        HAEA<EquationSystem> search = new HAEA<>(POPSIZE, operators, tournament, MAXITERS );

        Solution<EquationSystem> solution = search.apply(space, goal);

        System.out.println(solution.quality());
        System.out.println(solution.value());
    }
}
