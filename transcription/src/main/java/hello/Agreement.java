package hello;

import org.apache.jena.rdf.model.Statement;

import java.net.URI;
import java.util.HashMap;

public class Agreement extends Offer {
    private URI assignee;
    public Agreement(String id, HashMap instances) {
        super(id, instances);
    }

    public void parsePolicy(Statement o){
        String predicate = o.getPredicate().toString();

        // Maybe use cases.
        if (predicate.equals("http://www.w3.org/ns/odrl/2/permission")){
            if(instances.containsKey(o.getObject().toString())){
                //highly unlikely.
                Permission perm = new Permission(o.getObject().toString(), instances);
               // perm.output = instances.get(o.getObject().toString()).toString();
            } else {
                rules.add(new Permission(o.getObject().toString(), instances));
            }
        } else if(predicate.equals("http://www.w3.org/ns/odrl/2/assigner")){
            assigner = URI.create(o.getObject().toString());
        } else if(predicate.equals("http://www.w3.org/ns/odrl/2/assignee")){
            assignee = URI.create(o.getObject().toString());
        }

        //output = "An agreement between the assigner " + assigner + " and the assignee " + assignee +
        //        " so that the latter must abide to the following rules: " + rulesString;
    }
}
