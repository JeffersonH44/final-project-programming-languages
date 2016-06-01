package funico;

import fplearning.interpreter.Evaluator;
import fplearning.interpreter.GoalException;
import fplearning.interpreter.ProgramException;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import funico.mutation.ArityOneCutter;
import funico.mutation.EquationSwap;
import funico.mutation.InternalSwap;
import funico.mutation.TerminalMutator;
import funico.writer.PopulationTracer;
import funico.writer.TreeWriter;
import funico.xover.BranchXOver;
import funico.xover.EqualizeXOver;
import funico.xover.EquationXOver;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.haea.*;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.population.PopulationSolution;
import unalcol.search.population.PopulationSolutionDescriptors;
import unalcol.search.population.variation.ArityTwo;
import unalcol.search.population.variation.Operator;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.space.ArityOne;
import unalcol.search.space.Space;
import unalcol.tracer.Tracer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Main {
    private String[][] population;
    private EquationSystem solution;
    private boolean tournament;
    private double quality;
    private int iterations;
    private int populationSize;
    private Map<String, Operator<EquationSystem>> ops;
    
    public Main() {
        this.ops = new HashMap<>();
        this.ops.put("aoc", new ArityOneCutter());
        this.ops.put("es", new EquationSwap());
        this.ops.put("is", new InternalSwap());
        this.ops.put("tm", new TerminalMutator());
        this.ops.put("bxo", new BranchXOver());
        this.ops.put("exo", new EquationXOver());
        this.ops.put("eqxo", new EqualizeXOver());
    }
    
    public static String[] evaluateProgram(String program, String[] toEvaluate) {
        String[] ret = new String[toEvaluate.length];
        try {
            for(int i = 0; i < toEvaluate.length; ++i) {
                ret[i] = Evaluator.evalue(program, toEvaluate[i], 500);
            }
        } catch (LexicalException | SyntacticalException | ProgramException | GoalException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return ret;
        }
        return ret;
    }

    public void generateSolution(String[][] examples, Map<String, Boolean> selectedOps) {
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
        String selected = "geq";

        //Map<String, String[][]> map = init();

        //String[][] examples = ex;//map.get(selected);

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

        // Optimization function
        OptimizationFunction<EquationSystem> function = new EquationSystemFitness(examples);
        Goal<EquationSystem> goal = new OptimizationGoal<>(function, false, 1.0);

        /*ArityTwo<EquationSystem> exo = new EquationXOver();
        ArityOne<EquationSystem> es = new EquationSwap();
        ArityTwo<EquationSystem> bxo = new BranchXOver();
        ArityOne<EquationSystem> fcm = new ArityOneCutter();
        ArityOne<EquationSystem> ism = new InternalSwap();*/
        
        int counter = 0;
        for(Boolean b : selectedOps.values()) {
            if(b) counter++;
        }

        @SuppressWarnings("unchecked")
        Operator<EquationSystem>[] opers = (Operator<EquationSystem>[])new Operator[counter];
        
        int index = 0;
        for(String key : selectedOps.keySet()) {
            if(selectedOps.get(key)) {
                System.out.println("selected: " + key);
                opers[index] = this.ops.get(key);
                index++;
            }
        }
        /*
        opers[0] = exo;
        opers[1] = es;
        opers[2] = fcm;
        opers[3] = ism;
        opers[4] = bxo;*/

        int POPSIZE = getPopulationSize();
        int MAXITERS = getIterations();

        HaeaOperators<EquationSystem> operators = new SimpleHaeaOperators<>(opers);

        Selection<EquationSystem> tournament = new Tournament<>(4);
        Selection<EquationSystem> elitism = new Elitism<>(.9, .1);

        HAEA<EquationSystem> search = new HAEA<>(POPSIZE, operators, isTournament() ? tournament : elitism, MAXITERS );

        WriteDescriptors write_desc = new WriteDescriptors();
        Write.set(EquationSystem.class, new TreeWriter());
        Write.set(HaeaStep.class, new WriteHaeaStep<EquationSystem>());
        Descriptors.set(PopulationSolution.class, new PopulationSolutionDescriptors<EquationSystem>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<EquationSystem>());
        Write.set(HaeaOperators.class, write_desc);

        this.setPopulation(new String[POPSIZE][2]);
        Tracer tracer = new PopulationTracer(this.getPopulation());
        Tracer.addTracer(goal, tracer);

        Solution<EquationSystem> solution = search.apply(space, goal);
        
        Arrays.sort(population, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                final Double d1 = Double.valueOf(o1[1]);
                final Double d2 = Double.valueOf(o2[1]);
                return -d1.compareTo(d2);
            }
        });

        //printPopulation(this.population);
        this.setSolution(solution.value());
        this.setQuality(solution.quality());
        //System.out.println(solution.quality());
        //System.out.println(solution.value());
    }

    public void printPopulation(String[][] population) {
        for(String pop[] : population) {
            System.out.println("elem:" + pop[0]);
            System.out.println("fitness: " + pop[1]);
        }
    }

    private final int ID = 0, ARGS = 1;

    public int getType(String element) {
        if(element.matches("[0-9]+")) return EquationSystem.INTEGER;
        else if(element.matches("false|true")) return EquationSystem.BOOLEAN;
        else if(element.startsWith("[")) return EquationSystem.LIST;
        return -1;
    }

    public String[][] getPopulation() {
        return population;
    }

    public void setPopulation(String[][] population) {
        this.population = population;
    }

    public EquationSystem getSolution() {
        return solution;
    }

    public void setSolution(EquationSystem solution) {
        this.solution = solution;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    /**
     * @return the tournament
     */
    public boolean isTournament() {
        return tournament;
    }

    /**
     * @param tournament the tournament to set
     */
    public void setTournament(boolean tournament) {
        this.tournament = tournament;
    }

    /**
     * @return the iterations
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * @param iterations the iterations to set
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * @return the populationSize
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @param populationSize the populationSize to set
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }
}
