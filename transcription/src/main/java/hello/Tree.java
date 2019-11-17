package hello;

import javax.swing.tree.DefaultMutableTreeNode;

public class Tree{

    private DefaultMutableTreeNode root;

    public Tree(DefaultMutableTreeNode root){
        this.root = root;
    }

    public  DefaultMutableTreeNode getRoot(){
        return root;
    }

    @Override
    public String toString() {
        return "";
    }

    public <E> E postOrderDFS(){
        return null;
    }

}
