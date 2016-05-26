package funico.mutation;

import funico.EquationSystem;
import funico.Node;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.ArityOne;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Dado un sistemas de ecuaciones, de toma uno de los árboles sintácticos que lo componen
 * y se corta una de las ramas que tenga (esto se aplica sobretodo cuando se hacen llamado
 * a funciones)
 */
public class ArityOneCutter extends ArityOne<EquationSystem> {
    @Override
    public EquationSystem apply(EquationSystem equationSystem) {
        EquationSystem eq = new EquationSystem(equationSystem);

        IntUniform r = new IntUniform(eq.getNumberOfEquations());
        RandomSyntaxTree selectedTree = eq.getSyntaxTree(r.generate());

        Queue<Node> q = new LinkedList<>();
        q.add(selectedTree.getRoot());
        while(!q.isEmpty()) {
            Node current = q.remove();
            if(current.getArity() == Node.TERMINAL) {
                continue;
            }

            for(int i = 0; i < current.children.length; ++i) {
                if(current.children[i].getArity() == 1) {
                    current.children[i] = current.children[i].children[0];
                    return eq;
                }
            }
        }

        return eq;
    }
}
