package funico;

import funico.*;
import funico.mutation.ArityOneCutter;
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
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.Collection;
import unalcol.types.collection.vector.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        /**
         * Por ahora podemos hacer pruebas con estas funciones.
         * geq - mayor o igual (>=)
         * leq - menor o igual (<=)
         * lt - menor (<)
         * gt - mayor (>)
         * and - (&&)
         * or - (||)
         * xor - (^)
         */
        String selected = "con";

        Map<String, String[][]> map = init();

        String[][] examples = map.get(selected);

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

        String[] listVariables = {"A", "B", "C"};
        String[] terminals = {"0", "1", "true", "false"};
        int maxEquations = 3;
        int levels = 4;

        Space<EquationSystem> space = new EquationsSpace(maxEquations, examples, variables, listVariables,
                functionsName, functionsRetType, arityFun, terminals, levels);

        /*RandomSyntaxTree tree = space.get().getSyntaxTree(0);
        tree.doTest();*/

        /*EquationSystem eq = space.get();
        EquationSystem eq1 = space.get();

        System.out.println(eq);
        System.out.println(eq1);

        ArityTwo<EquationSystem> exo = new EquationXOver();
        ArityOne<EquationSystem> es = new EquationSwap();
        ArityTwo<EquationSystem> bxo = new BranchXOver();

        Vector<EquationSystem> result = bxo.apply(eq, eq1);

        System.out.println("result");
        System.out.println(result.get(0));
        System.out.println(result.get(1));*/


        // Optimization function
        OptimizationFunction<EquationSystem> function = new EquationSystemFitness(examples);
        Goal<EquationSystem> goal = new OptimizationGoal<>(function, false, 1.0);

        ArityTwo<EquationSystem> exo = new EquationXOver();
        ArityOne<EquationSystem> es = new EquationSwap();
        ArityTwo<EquationSystem> bxo = new BranchXOver();
        ArityOne<EquationSystem> fcm = new ArityOneCutter();

        @SuppressWarnings("unchecked")
        Operator<EquationSystem>[] opers = (Operator<EquationSystem>[])new Operator[3];
        opers[0] = exo;
        opers[1] = es;
        opers[2] = bxo;
        //opers[3] = fcm;

        int POPSIZE = 75;
        int MAXITERS = 100;

        HaeaOperators<EquationSystem> operators = new SimpleHaeaOperators<>(opers);

        Selection<EquationSystem> tournament = new Tournament<>(4);
        Selection<EquationSystem> elitism = new Elitism<>(.9, .1);

        HAEA<EquationSystem> search = new HAEA<>(POPSIZE, operators, tournament, MAXITERS );

        Solution<EquationSystem> solution = search.apply(space, goal);

        System.out.println(solution.quality());
        System.out.println(solution.value());
    }

    public static Map<String, String[][]> init() {
        Map<String, String[][]> map = new HashMap<>();

        String[][] test = {
                {"con([1,2,3],1)", "true"},
                {"con([0],0)", "true"},
                {"con([1, 2],0)", "false"},
                {"con([8,7,6,5],5)", "true"},
                {"con([1,4,7],3)", "false"},
                {"con([5],4)", "false"},
                {"con([10],11)", "false"},
                {"con([1,2,3,4,5,6,7,8,9],3)", "true"},
                {"con([2,4,6,8,10],3)", "false"}
        };
        String[][] examplesgeq = {
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
        String[][] examplesleq = {
                {"leq(0,1)", "true"},
                {"leq(0,0)", "true"},
                {"leq(1,0)", "false"},
                {"leq(1,1)", "true"},
                {"leq(1,2)", "true"},
                {"lgeq(2,1)", "false"},
                {"leq(2,5)", "true"},
                {"leq(5,2)", "false"},
                {"leq(3,3)", "true"}
        };
        String[][] exampleslt = {
                {"lt(0,1)", "true"},
                {"lt(0,0)", "false"},
                {"lt(1,0)", "false"},
                {"lt(1,1)", "false"},
                {"lt(1,2)", "true"},
                {"lt(2,1)", "false"},
                {"lt(2,5)", "true"},
                {"lt(5,2)", "flase"},
                {"lt(3,3)", "false"}
        };
        String[][] examplesgt = {
                {"gt(0,1)", "false"},
                {"gt(0,0)", "false"},
                {"gt(1,0)", "true"},
                {"gt(1,1)", "false"},
                {"gt(1,2)", "false"},
                {"gt(2,1)", "true"},
                {"gt(2,5)", "false"},
                {"gt(5,2)", "true"},
                {"gt(3,3)", "false"}
        };
        String[][] examplesand = {
                {"and(0,1)", "false"},
                {"and(0,0)", "false"},
                {"and(1,0)", "flase"},
                {"and(1,1)", "true"}
        };
        String[][] examplesor = {
                {"or(0,1)", "true"},
                {"or(0,0)", "false"},
                {"or(1,0)", "true"},
                {"or(1,1)", "true"}
        };
        String[][] examplesxor = {
                {"xor(0,1)", "true"},
                {"xor(0,0)", "false"},
                {"xor(1,0)", "true"},
                {"xor(1,1)", "false"}
        };
        map.put("geq", examplesgeq);
        map.put("leq", examplesleq);
        map.put("lt", exampleslt);
        map.put("gt", examplesgt);
        map.put("and", examplesand);
        map.put("or", examplesor);
        map.put("xor", examplesxor);
        map.put("con", test);

        return map;
    }

    private static final int ID = 0, ARGS = 1;

    public static int getType(String element) {
        if(element.matches("[0-9]+")) return EquationSystem.INTEGER;
        else if(element.matches("false|true")) return EquationSystem.BOOLEAN;
        else if(element.startsWith("[")) return EquationSystem.LIST;
        return -1;
    }
}
