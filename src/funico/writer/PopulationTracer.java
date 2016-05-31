package funico.writer;

import unalcol.tracer.OutputStreamTracer;
import unalcol.tracer.Tracer;


public class PopulationTracer extends OutputStreamTracer {
    private int index;
    private int n;
    private String[][] reference;

    public PopulationTracer(String[][] reference) {
        super();
        this.index = 0;
        this.n = reference.length;
        this.reference = reference;
    }

    @Override
    public void write(String s) {
        if(s.equals("\n")) return;

        int i = s.lastIndexOf(',');
        reference[index][0] = s.substring(0, i);
        reference[index][1] = s.substring(i + 1);

        index++;
        if(index == n) index = 0;
    }

    @Override
    public void close() {}

    @Override
    public void clean() {}
}
