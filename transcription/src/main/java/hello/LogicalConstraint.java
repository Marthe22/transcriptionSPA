package hello;

import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class LogicalConstraint extends ODRLElement {
    private String operand;
    private String rightOp = "";
    private HashMap<String, String> definitions = new HashMap<>();

    private ArrayList<ODRLElement> constraints = new ArrayList<>();


    public LogicalConstraint(String id, HashMap instances) {
        super(id, instances);
    }

    public ODRLElement parse(Statement o){

        ODRLElement object = this;
       if (o.getSubject().toString().equals(this.getId())){

           parseThisLogicalConstraint(o);

        }else if (o.getSubject().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Constraint")
        || o.getSubject().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){

            if (!o.getSubject().toString().equals(this.getId())) {

                int i = find(constraints, o.getSubject().toString());
                if (i < 0) {
                    System.out.println("Constraint not found.");
                }


                object = constraints.get(i).parse(o);
                if (constraints.get(i).toString() != null) {
                    instances.putAll(constraints.get(i).instances);

                }


            }
        }
        return object;
    }


    private void parseThisLogicalConstraint(Statement o){

        if (o.getPredicate().toString().equals("http://www.w3.org/ns/odrl/2/and") ||
                o.getPredicate().toString().equals("http://www.w3.org/ns/odrl/2/xone" ) ||
                o.getPredicate().toString().equals("http://www.w3.org/ns/odrl/2/or")) {

            operand = getNaturalLanguage(o.getPredicate().toString());

            if (instances.containsKey(o.getObject().toString())){
                this.addChild(instances.get(o.getObject().toString()));
                constraints.add(instances.get(o.getObject().toString()));

            } else {
                String newObj = o.getObject().asResource().getProperty(a).getObject().toString();
                if (newObj.equals("http://www.w3.org/ns/odrl/2/Constraint")) {
                    Constraint constr = new Constraint(o.getObject().toString(), instances);
                    this.addChild(constr);
                    constraints.add(constr);

                } else if (newObj.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")) {
                    LogicalConstraint constr = new LogicalConstraint(o.getObject().toString(), instances);
                    this.addChild(constr);
                    constraints.add(constr);
                }
            }
        }
    }



    @Override
    public String toString(){
        String out = null;

        for(ODRLElement c : constraints) {
            if (out == null) {
                out = c.toString();

            } else {
                rightOp = c.toString();
                out = operand.replace("$1", out);

                if(rightOp!= null) {
                    out = out.replace("$2", rightOp);
                } else {
                    out = out.replace("$2", "");
                }

            }
        }
        return out;
    }

    @Override
    public HashMap<String, String> toDefinition(){
        for (ODRLElement c : constraints){
            definitions.putAll(c.toDefinition());
        }

        return definitions;
    }

}
