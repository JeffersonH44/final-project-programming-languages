package funico;

/**
 * Created by Jefferson on 06/05/2016.
 */
public class List extends Node {
    public List(String elem, String list) {
        super("list", 2);
        this.children[0] = new Node(elem, Node.TERMINAL);
        this.children[1] = new Node(list, Node.TERMINAL);
    }

    public List(Node elem, Node list) {
        super("list", 2);
        this.children[0] = new Node(elem);
        this.children[1] = list instanceof List ? new List(list.children[0], list.children[1]) : new Node(list);
    }

    public List(List toClone) {
        this(toClone.children[0].getName(), toClone.children[1].getName());
    }

    @Override
    public String getPhenotype() {
        if(this.isEmpty()) return "[]";
        return "[" + this.children[0].getPhenotype() + "|" + this.children[1].getPhenotype() + "]";
    }

    public boolean isEmpty() {
        return this.children[0].getPhenotype().equals("") && this.children[1].getPhenotype().equals("");
    }
}
