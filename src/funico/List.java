package funico;

public class List extends Node {
    public List(String elem, String list) {
        super("list", 2);
        this.children[0] = new Node(elem, Node.TERMINAL);
        this.children[1] = new Node(list, Node.TERMINAL);
    }

    public List(Node elem, Node list) {
        super("list", 2);
        this.children[0] = new Node(elem);
        this.children[1] = list.getName().equals("list") ? new List(list.children[0], list.children[1]) : new Node(list);
    }

    public List(Node toClone) {
        this(toClone.children[0], toClone.children[1]);
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
