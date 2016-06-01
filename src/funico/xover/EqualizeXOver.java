/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package funico.xover;

import funico.Equal;
import funico.EquationSystem;
import funico.Node;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.vector.Vector;

/**
 *
 * @author Jefferson
 */
public class EqualizeXOver extends ArityTwo<EquationSystem>{

    @Override
    public Vector<EquationSystem> apply(EquationSystem eq1, EquationSystem eq2) {
        EquationSystem one = new EquationSystem(eq1);
        EquationSystem two = new EquationSystem(eq2);
        
        Vector<EquationSystem> toRet = new Vector<>();
        toRet.add(one);
        toRet.add(two);
        
        int oneBaseEquations = one.getBaseEquations(), twoBaseEquations = two.getBaseEquations();
        IntUniform rBaseOne = new IntUniform(oneBaseEquations), rBaseTwo = new IntUniform(twoBaseEquations);
        
        // do equalization
        RandomSyntaxTree oneTree = one.getSyntaxTree(rBaseOne.generate());
        RandomSyntaxTree twoTree = two.getSyntaxTree(rBaseTwo.generate());
        
        Node rootOne = new Equal(), rootTwo = new Equal();
        rootOne.children[0] = new Node(oneTree.getRoot().children[0]);
        rootOne.children[1] = new Node(twoTree.getRoot().children[0]);
        
        rootTwo.children[0] = new Node(twoTree.getRoot().children[0]);
        rootTwo.children[1] = new Node(oneTree.getRoot().children[0]);
        
        // apply over recursive eq
        IntUniform rRecOne = new IntUniform(oneBaseEquations, one.getNumberOfEquations());
        IntUniform rRecTwo = new IntUniform(twoBaseEquations, two.getNumberOfEquations());
        
        RandomSyntaxTree recOne = one.getSyntaxTree(rRecOne.generate());
        recOne.setRoot(rootOne);
        RandomSyntaxTree recTwo = two.getSyntaxTree(rRecTwo.generate());
        recTwo.setRoot(rootTwo);
        
        return toRet;
    }
    
}
