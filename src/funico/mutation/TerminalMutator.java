package funico.mutation;

import funico.EquationSystem;
import funico.Node;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.ArityOne;

import java.util.*;

/**
 * Mutación que modifica un terminal de un árbol sintáctico del sistema de
 * ecuaciones, se modifica por otro terminal dentro del árbol.
 */
public class TerminalMutator extends ArityOne<EquationSystem>{

    @Override
    public EquationSystem apply(EquationSystem equationSystem) {
        EquationSystem eq = new EquationSystem(equationSystem);

        // choosing a tree
        IntUniform r = new IntUniform(eq.getNumberOfEquations());
        RandomSyntaxTree selectedTree = eq.getSyntaxTree(r.generate());

        Set<Node> terminals = new HashSet<>();
        Queue<Node> q = new LinkedList<>();
        q.add(selectedTree.getRoot());
        ArrayList<Node> allTerminals = new ArrayList<>();

        // TODO: take care of the lists expresions
        while(!q.isEmpty()) {
            Node n = q.remove();
            if(n.getArity() == Node.TERMINAL) {
                terminals.add(n);
            } else {
                for(Node child : n.children) {
                    if(n.getName().equals("list") && !n.children[0].equals("")) {
                        // put head
                        q.add(child.children[0]);
                        allTerminals.add(child.children[0]);
                    } else {
                        q.add(child);
                        allTerminals.add(child);
                    }
                }
            }
        }

        ArrayList<Node> elements = new ArrayList<>(terminals);
        r = new IntUniform(elements.size());
        Node toChange = elements.get(r.generate());
        Node with = toChange;
        while (toChange == with) {
            with = elements.get(r.generate());
        }

        String nameToChange = toChange.getName();
        String nameWith = with.getName();

        for(Node terminal : allTerminals) {
            if(terminal.getName().equals(nameToChange)) {
                terminal.setName(nameWith);
            }
        }

        return eq;
    }
}
