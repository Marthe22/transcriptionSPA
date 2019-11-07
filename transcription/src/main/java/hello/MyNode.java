package hello;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MyNode {
    private String id;
    MyNode parent; // package private
    Set<MyNode> children;

    public MyNode(String id){
        this.id = id;
    }

    public String getId(){return id;}

    public MyNode addChild(MyNode child) { //TODO: decide whether to return child or not.
        if (children == null) {
            children = new LinkedHashSet<>();
        }

        child.parent = this;
        children.add(child);
        return child;
    }

    public Set<? extends MyNode> getChildren(){
        return children;

    }

    public <E extends MyNode> E DFsearch(String id){
        //System.out.println("Calling DFsearch from node: " + this.getId());
      E foundNode =  DFSUtil((E)this, new HashSet<E>() , id);
      if( foundNode == null){
          System.out.println("Node was not found. ");
      }
      return foundNode;
    }

    private <E extends MyNode> E DFSUtil(E node, Set<E> visited,String id){
        if (node != null) {
            if (visited.contains(node)) {
                return null;
            } else {
              //  System.out.println("visiting node " + node.getId());
                visited.add(node);
                if (node.getId().equals(id)) {
                    return node;
                }
                if (node.getChildren()!= null) {
                    Set<E> children = (Set<E>)node.getChildren();
                    for (E child : children) {
                        E isFound = DFSUtil(child, visited, id);
                        if(isFound != null){
                            return isFound;
                        }
                    }
                }
            }
        }
        return null;
    }

}
