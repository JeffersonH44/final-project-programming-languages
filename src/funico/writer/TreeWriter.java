package funico.writer;

import funico.EquationSystem;
import unalcol.io.Write;

import java.io.Writer;

/**
 * Created by Jefferson on 31/05/2016.
 */
public class TreeWriter extends Write<EquationSystem> {
    @Override
    public void write(EquationSystem equationSystem, Writer writer) throws Exception {
        writer.write(equationSystem.toString());
    }
}
