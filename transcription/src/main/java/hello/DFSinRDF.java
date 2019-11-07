package hello;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class DFSinRDF {
    private Model model;
    private String startingNode;
    public Tree tree;
    public Policy policy;
    private HashMap insTances = new HashMap();

    public DFSinRDF(Model model, String startingNode) {
        /* Starting node: for us, policy.*/
        this.model = model;
        this.startingNode = startingNode;
    }

    public Tree rdfDFSCaller(){
        StmtIterator stmts = model.listStatements(null, RDF.type, model.getResource(startingNode));
        // following while loop is ran for each policy node.
        while (stmts.hasNext()) {
            Statement stmt = stmts.next();
            if(stmt.getObject().toString().equals("http://www.w3.org/ns/odrl/2/Set")) {
                policy = new Policy(stmt.getSubject().toString(), new HashMap());
            } else if(stmt.getObject().toString().equals("http://www.w3.org/ns/odrl/2/Offer")) {
                System.out.println("DFS in RDF : This happened ======================================");
                policy = new Offer(stmt.getSubject().toString(), new HashMap<>(insTances));
            } else if(stmt.getObject().toString().equals("http://www.w3.org/ns/odrl/2/Agreement")) {
                policy = new Agreement(stmt.getSubject().toString(),  new HashMap());
            }
            tree = new Tree(new DefaultMutableTreeNode(policy));

            rdfDFS(stmt.getSubject(), new HashSet<RDFNode>(), "", tree.getRoot());
           // insTances.putAll(policy.instances);
            insTances = new HashMap<>(policy.instances);


        }
        return tree;
    }

    private void rdfDFS(RDFNode node, Set<RDFNode> visited, String prefix, DefaultMutableTreeNode parentNode) {
        if (visited.contains(node)) {
            return;
        } else {
            visited.add(node);

            if (node.isResource()) {
                StmtIterator stmts = node.asResource().listProperties();

                while (stmts.hasNext()) {
                    Statement stmt = stmts.next();
                    System.out.println("stmt is: " + stmt);

                    if (insTances.containsKey(stmt.getSubject().toString())){
                        //System.out.println("Instance " + stmt.getSubject().toString() + " already exists: " + insTances.get(stmt.getSubject().toString()));
                        return;
                    }


                    ODRLElement userObj = (ODRLElement) parentNode.getUserObject();
                    ODRLElement parsedObj = userObj.parse(stmt);
                    if(userObj.getId().equals(stmt.getSubject())){
                        rdfDFS(stmt.getObject(), visited, prefix + node + " =[" + stmt.getPredicate() + "]=>", parentNode);
                    } else if(!userObj.getId().equals(stmt.getObject())){
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(parsedObj);
                        parentNode.add(newNode);
                        rdfDFS(stmt.getObject(), visited, prefix + node + " =[" + stmt.getPredicate() + "]=>", newNode);
                    }
                    // determine which is the previous object so that we can use it to parse its child object.


                }
            }
        }
    }
}