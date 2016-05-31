package funico.mutation;

import funico.EquationSystem;
import funico.Node;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.ArityOne;
import unalcol.types.collection.vector.Vector;

import java.util.*;

public class InternalSwap extends ArityOne<EquationSystem> {

    @Override
    public EquationSystem apply(EquationSystem equationSystem) {
        EquationSystem eq = new EquationSystem(equationSystem);

        // equation selection
        IntUniform r = new IntUniform(eq.getBaseEquations(), eq.getNumberOfEquations());
        RandomSyntaxTree selectedTree = eq.getSyntaxTree(r.generate());

        // retrieve all nodes with arity >= 2
        List<Node> nodes = new ArrayList<>();
        Queue<Node> q = new LinkedList<>();

        Node root = selectedTree.getRoot();
        q.add(root.children[0]);
        q.add(root.children[1]);

        while(!q.isEmpty()) {
            Node currentNode = q.remove();
            if(currentNode.getArity() >= 2) {
                nodes.add(currentNode);
                Collections.addAll(q, currentNode.children);
            }
        }

        Node selectedNode = nodes.get(new IntUniform(nodes.size()).generate());

        int one, two;
        IntUniform indexSelector = new IntUniform(selectedNode.getArity());
        one = two = indexSelector.generate();

        while (one == two) {
            two = indexSelector.generate();
        }

        Node temp = selectedNode.children[one];
        selectedNode.children[one] = selectedNode.children[two];
        selectedNode.children[two] = selectedNode.children[one];

        return eq;
    }
}
