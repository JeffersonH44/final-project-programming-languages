package funico;

public class Equal extends Node {

    public Equal() {
        super("=", 2);
    }

    public Equal(Equal toClone) {
        this();

        this.children[0] = new Node(toClone.children[0]);
        this.children[1] = toClone.children[1].getName().equals("list") ?
                new List(toClone.children[1]) : new Node(toClone.children[1]);
    }

    @Override
    public String getPhenotype() {
        return children[0].getPhenotype() + this.name + children[1].getPhenotype();
    }
}
