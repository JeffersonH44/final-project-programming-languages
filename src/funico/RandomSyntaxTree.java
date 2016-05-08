package funico;

import java.util.*;
import java.util.Queue;
import java.util.regex.Pattern;

import unalcol.random.integer.IntUniform;
import unalcol.random.util.RandBool;
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
    }

    public String getPhenotype() {
        return root.getPhenotype();
    }

    private class VoidNode {
        public Node prevNode;
        public int level;
        public int index;

        public VoidNode(Node prevNode, int level, int index) {
            this.prevNode = prevNode;
            this.level = level;
            this.index = index;
        }
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
                generateRecursiveTreeEq(new Node(functionName, arityFun.get(functionName).length), level + 1);
            } else {
                Vector<Node> terminals = new Vector<>();

                terminals.add(new Node(this.variables[i], Node.TERMINAL));
                switch (currentArguments[i]) {
                    case EquationSystem.BOOLEAN:
                        terminals.add(new Node("false", Node.TERMINAL));
                        terminals.add(new Node("true", Node.TERMINAL));
                        break;
                    case EquationSystem.INTEGER:
                        terminals.add(new Node("1", Node.TERMINAL));
                        terminals.add(new Node("0", Node.TERMINAL));
                        break;
                    case EquationSystem.LIST:
                        terminals.add(new List("", ""));
                        IntUniform r = new IntUniform(listVariables.length);

                        Node chosenVariable = new RandBool(0.5).generate() ? new List("", "") : new Node(listVariables[r.generate()], Node.TERMINAL);
                        terminals.add(new List(new Node(this.variables[i], Node.TERMINAL), chosenVariable)); // with probability of empty list at end
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
                currentNode.children[i] = newChildren;
            }
        }

    }

    private void generateBaseTreeEq(Node currentNode) {
        String currentFunction = currentNode.getName();
        Integer[] currentArguments = this.arityFun.get(currentFunction);

        for(int i = 0; i < currentArguments.length; ++i) {
            Vector<Node> terminals = new Vector<>();

            terminals.add(new Node(this.variables[i], Node.TERMINAL));
            switch (currentArguments[i]) {
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

                    Node chosenVariable = new RandBool(0.5).generate() ? new List("", "") : new Node(listVariables[r.generate()], Node.TERMINAL);
                    terminals.add(new List(new Node(this.variables[i], Node.TERMINAL), chosenVariable)); // with probability of empty list at end
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
            currentNode.children[i] = newChildren;
        }
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
                Integer[] args = arityFun.get(currentNode.getName());

                for(int i = 0; i < args.length; ++i) {
                    if(args[i].equals(EquationSystem.BOOLEAN))
                        booleanElements.add(currentNode.children[i]);
                    else if(args[i].equals(EquationSystem.INTEGER))
                        integerElements.add(currentNode.children[i]);
                    else if(args[i].equals(EquationSystem.LIST)) {
                        System.out.println(currentNode.getName());
                        if(currentNode.children[i].getArity() == Node.TERMINAL) { // check if is not a variable containing a hole list
                            listElements.add(currentNode);
                        } else {
                            if(!currentNode.children[i].children[1].equals("")) // only if is not an empty list
                                listElements.add(currentNode.children[i].children[1]); // second element is the real list
                            // TODO: boolean or integer list?
                            integerElements.add(currentNode.children[i]);
                            booleanElements.add(currentNode.children[i]);
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

            for(int i = 0; i < toDeduceArgs.length; ++i) {
                if(toDeduceArgs[i].equals(EquationSystem.BOOLEAN)) {
                    root.children[1].children[i] = new Node(booleanElements.get(rBoolean.generate()));
                } else if(toDeduceArgs[i].equals(EquationSystem.INTEGER)) {
                    root.children[1].children[i] = new Node(integerElements.get(rInteger.generate()));
                } else { // List case
                    Node currNode  = listElements.get(rList.generate());
                    if(currNode instanceof List)
                        root.children[1].children[i] = new List(currNode.children[0], currNode.children[1]);
                    else
                        root.children[1].children[i] = new Node(currNode);
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

    public Node getRoot() {
        return root;
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
