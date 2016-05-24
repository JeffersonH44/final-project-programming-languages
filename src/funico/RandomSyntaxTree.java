package funico;

import java.util.*;
import java.util.Queue;
import java.util.regex.Pattern;

import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
import unalcol.types.collection.array.ArrayUtil;
import unalcol.types.collection.list.*;
import unalcol.types.collection.vector.Vector;

public class RandomSyntaxTree {
    public RandomSyntaxTree(String[][] examples, String[] variables, String[] listVariables, String[] functor,
                            Integer[] functorRetType, Map<String, Integer[]> arityFun, String[] terminals,
                            int levels, boolean recursive) {
        this.arityFun = arityFun;
        this.functor = functor;
        this.functorRetType = functorRetType;
        this.terminals = terminals;
        this.levels = levels;
        this.examples = examples;
        this.variables = variables;
        this.listVariables = listVariables;

        generateRandomTree(recursive);
    }

    /**
     * For clone purposes (small copy)
     */
    public RandomSyntaxTree(RandomSyntaxTree toClone) {
        this.root = new Equal((Equal) toClone.root);
        this.terminals = toClone.terminals;
    }

    public String getPhenotype() {
        return root.getPhenotype();
    }

    private Node generateNode(Integer currentArg, int index) {
        Vector<Node> terminals = new Vector<>();

        terminals.add(new Node(this.variables[index], Node.TERMINAL));
        switch (currentArg) {
            case EquationSystem.BOOLEAN:
                terminals.add(new Node("false", Node.TERMINAL));
                terminals.add(new Node("true", Node.TERMINAL));
                break;
            case EquationSystem.INTEGER:
                terminals.add(new Node("1", Node.TERMINAL));
                terminals.add(new Node("0", Node.TERMINAL));
                break;
            case EquationSystem.LIST:
                terminals.add(new List("", "")); // empty list
                IntUniform r = new IntUniform(listVariables.length);

                // tail of the list
                Node chosenVariable = new RandBool(0.5).generate() ? new List("", "") : new Node(listVariables[r.generate()], Node.TERMINAL);
                terminals.add(new List(new Node(this.variables[index], Node.TERMINAL), chosenVariable)); // with probability of empty list at end
                break;
        }
        IntUniform r1 = new IntUniform(terminals.size());
        int pos = r1.generate();
        Node newChildren, currElement = terminals.get(pos);
        if(currElement instanceof List) {
            newChildren = new List(currElement.children[0], currElement.children[1]);
        } else {
            newChildren = new Node(currElement);
        }
        return newChildren;
    }

    private void generateRecursiveTreeEq(Node currentNode, int level) {
        if(level == this.levels - 1) {
            generateBaseTreeEq(currentNode);
            return;
        }

        String currentFunction = currentNode.getName();
        Integer[] currentArguments = this.arityFun.get(currentFunction);

        for(int i = 0; i < currentArguments.length; ++i) {
            // go through all functions to see if there is one that match
            Vector<String> matchedFunctions = new Vector<>();
            for(int j = 0; j < this.functorRetType.length; ++j) {
                if(functorRetType[j].equals(currentArguments[i])) matchedFunctions.add(functor[j]); // be careful with the indices
            }

            if(matchedFunctions.size() != 0 && new RandBool(0.5).generate()) {
                IntUniform r = new IntUniform(matchedFunctions.size());
                String functionName = matchedFunctions.get(r.generate());
                currentNode.children[i] = new Node(functionName, arityFun.get(functionName).length);
                generateRecursiveTreeEq(currentNode.children[i], level + 1);
            } else {
                currentNode.children[i] = generateNode(currentArguments[i], i);
            }
        }

    }

    private void generateBaseTreeEq(Node currentNode) {
        String currentFunction = currentNode.getName();
        Integer[] currentArguments = this.arityFun.get(currentFunction);

        for(int i = 0; i < currentArguments.length; ++i) {
            currentNode.children[i] = generateNode(currentArguments[i], i);
        }
    }

    private void addNodeByType(Integer type, Node node, Vector<Node> integerElements,
                               Vector<Node> booleanElements, Vector<Node> listElements) {
        if(type == EquationSystem.BOOLEAN) booleanElements.add(node);
        else if(type == EquationSystem.INTEGER) integerElements.add(node);
        else listElements.add(node);
    }

    private void generateRandomTree(boolean recursive) {

        // common elements in both trees (recursive and base equation)
        String toDeduce = functor[0];
        Integer returnType = functorRetType[0];

        root = new Equal();
        root.children[0] = new Node(toDeduce, arityFun.get(toDeduce).length);

        if(recursive) {
            generateRecursiveTreeEq(root.children[0], 2);

            Vector<Node> integerElements = new Vector<>();
            Vector<Node> booleanElements = new Vector<>();
            Vector<Node> listElements = new Vector<>();

            Queue<Node> functions = new LinkedList<>();
            functions.add(root.children[0]);
            while(!functions.isEmpty()) {
                Node currentNode = functions.remove();
                int type = functorRetType[Arrays.asList(functor).indexOf(currentNode.getName())];

                addNodeByType(type, currentNode, integerElements, booleanElements, listElements);
                /*if(type == EquationSystem.BOOLEAN) booleanElements.add(currentNode);
                else if(type == EquationSystem.INTEGER) integerElements.add(currentNode);
                else listElements.add(currentNode);*/

                Integer[] args = arityFun.get(currentNode.getName());

                for(int i = 0; i < args.length; ++i) {
                    if(args[i].equals(EquationSystem.BOOLEAN))
                        booleanElements.add(currentNode.children[i]);
                    else if(args[i].equals(EquationSystem.INTEGER))
                        integerElements.add(currentNode.children[i]);
                    else if(args[i].equals(EquationSystem.LIST)) {
                        if(currentNode.children[i].getArity() == Node.TERMINAL) { // check if is not a variable containing a hole list
                            listElements.add(currentNode.children[i]);
                        } else {
                            if(!currentNode.children[i].children[1].getName().equals("")) { // only if is not an empty list
                                listElements.add(currentNode.children[i].children[1]); // second element is the real list
                                // TODO: boolean or integer list?
                                integerElements.add(currentNode.children[i].children[0]);
                                booleanElements.add(currentNode.children[i].children[0]);
                            } else {
                                listElements.add(new List("", ""));
                            }
                        }
                    }

                    if(currentNode.children[i].getArity() != Node.TERMINAL && !currentNode.children[i].getName().equals("list")) {
                        functions.add(currentNode.children[i]);
                    }
                }
            }

            Integer[] toDeduceArgs = arityFun.get(toDeduce);
            root.children[1] = new Node(toDeduce, toDeduceArgs.length);
            IntUniform rBoolean = new IntUniform(booleanElements.size());
            IntUniform rInteger = new IntUniform(integerElements.size());
            IntUniform rList = new IntUniform(listElements.size());

            // add possible functions calls
            for(int i = 1; i < functor.length; ++i) {
                String currentFunction = functor[i];
                Integer currRetType = functorRetType[i];
                Integer[] args = arityFun.get(currentFunction);
                Node currentNode = new Node(currentFunction, args.length);

                for(int j = 0; j < currentNode.getArity(); ++j) {
                    if(args[j] == EquationSystem.INTEGER)
                        currentNode.children[j] = integerElements.get(rInteger.generate());
                    else if(args[j] == EquationSystem.BOOLEAN)
                        currentNode.children[j] = booleanElements.get(rBoolean.generate());
                    else
                        currentNode.children[j] = listElements.get(rList.generate());
                }

                addNodeByType(currRetType, currentNode, integerElements, booleanElements, listElements);
                /*if(currRetType == EquationSystem.INTEGER) integerElements.add(currentNode);
                else if(currRetType == EquationSystem.BOOLEAN) booleanElements.add(currentNode);
                else listElements.add(currentNode);*/
            }

            rBoolean = new IntUniform(booleanElements.size());
            rInteger = new IntUniform(integerElements.size());
            rList = new IntUniform(listElements.size());

            for(int i = 0; i < toDeduceArgs.length; ++i) {

                if(toDeduceArgs[i].equals(EquationSystem.BOOLEAN)) {
                    root.children[1].children[i] = new Node(booleanElements.get(rBoolean.generate()));
                } else if(toDeduceArgs[i].equals(EquationSystem.INTEGER)) {
                    root.children[1].children[i] = new Node(integerElements.get(rInteger.generate()));
                } else if (toDeduceArgs[i].equals((EquationSystem.LIST))) { // List case
                    Node currNode  = listElements.get(rList.generate());
                    if(currNode.getName().equals("list"))
                        root.children[1].children[i] = new List(currNode.children[0], currNode.children[1]);
                    else
                        root.children[1].children[i] = new Node(currNode);
                } else {
                    throw new IllegalStateException("boom!!");
                }
            }

        } else {
            Node leftSide = root.children[0];
            generateBaseTreeEq(leftSide);
            Integer[] argsType = arityFun.get(toDeduce);
            Vector<Node> possible = new Vector<>();
            for(int i = 0; i < argsType.length; ++i) {
                if(argsType[i].equals(returnType)) possible.add(leftSide.children[i]); // TODO: not working with list
            }

            if(returnType.equals(EquationSystem.BOOLEAN)) {
                possible.add(new Node("false", Node.TERMINAL));
                possible.add(new Node("true", Node.TERMINAL));
            } else if(returnType.equals(EquationSystem.INTEGER)) {
                possible.add(new Node("0", Node.TERMINAL));
                possible.add(new Node("1", Node.TERMINAL));
            } else {
                possible.add(new List("", ""));
            }

            IntUniform r = new IntUniform(possible.size());
            root.children[1] = new Node(possible.get(r.generate()));
        }
    }

    private Set<String> retrieveVariablesFromNode(Node start, Set<String> terminals) {
        Set<String> vars = new HashSet<>();
        Queue<Node> q = new LinkedList<>();
        q.add(start);
        while(!q.isEmpty()) {
            Node currentNode = q.remove();
            if(currentNode.getArity() == Node.TERMINAL && !terminals.contains(currentNode.getName())) {
                vars.add(currentNode.getName());
            } else {
                Collections.addAll(q, currentNode.children);
            }
        }

        return vars;
    }

    private Set<String> setDifference(Set<String> a, Set<String> b) {
        Set<String> union = new HashSet<>(b);
        union.removeAll(a);
        return union;
    }

    public void repair() {
        Set<String> changeable = new HashSet<>();
        Collections.addAll(changeable, this.terminals);
        Set<String> rightVariables, leftVariables;

        // go right
        rightVariables = retrieveVariablesFromNode(this.root.children[1], changeable);

        // go left
        leftVariables = retrieveVariablesFromNode(this.root.children[0], changeable);

        // to put in the left part
        Set<String> mand = setDifference(leftVariables, rightVariables);

        Set<String> unusedVariables = setDifference(rightVariables, leftVariables);
        changeable.addAll(unusedVariables);

        Queue<Node> q = new LinkedList<>();
        q.add(this.root.children[0]);
        ArrayList<String> mandatory = new ArrayList<>();
        mandatory.addAll(mand);
        // TODO: change other variables
        Set<String> usedVariables = new HashSet<>();
        int index = 0;
        while(!q.isEmpty() && index < mandatory.size()) {
            Node currentNode = q.remove();
            if(currentNode.getArity() == Node.TERMINAL &&
                    (changeable.contains(currentNode.getName()) ||
                    usedVariables.contains(currentNode.getName()))) {
                currentNode.setName(mandatory.get(index));
                index++;
            } else {
                Collections.addAll(q, currentNode.children);
            }

            usedVariables.add(currentNode.getName());
        }
    }

    public Node getRoot() {
        return root;
    }

    /**
     * que chambonada la forma de hacer testing
     */
    public void doTest() {
        root = new Equal();
        root.children[0] = new Node("geq", 2);
        root.children[1] = new Node("geq", 2);

        root.children[0].children[0] = new Node("A", Node.TERMINAL);
        root.children[0].children[1] = new Node("A", Node.TERMINAL);

        root.children[1].children[0] = new Node("A", Node.TERMINAL);
        root.children[1].children[1] = new Node("s", 1);

        root.children[1].children[1].children[0] = new Node("B", Node.TERMINAL);

        System.out.println(this.getPhenotype());
        this.repair();
        System.out.println(this.getPhenotype());
    }

    private Node root;
    private String[] functor;
    private Integer[] functorRetType;
    private String[] terminals;
    private String[] variables;
    private String[] listVariables;
    private String[][] examples;
    private Map<String, Integer[]> arityFun;
    private int levels;
}
