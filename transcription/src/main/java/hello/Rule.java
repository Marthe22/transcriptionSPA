package hello;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public  abstract class Rule extends ODRLElement {
    protected ArrayList<ODRLElement> target = new ArrayList<>();
    protected ArrayList<ODRLElement> constraints = new ArrayList<>();
    protected ArrayList<ODRLElement> actions = new ArrayList<>();

    public Rule(String id, HashMap instances) {
        super(id, instances);
    }

    @Override
    public ODRLElement parse(Statement o) {
        System.out.println("Rule.parse");
        Resource s = o.getSubject();
        ODRLElement object = this;
        String subjectType = s.getProperty(a).getObject().toString();

        if(subjectType.equals("http://www.w3.org/ns/odrl/2/Duty")){
            parseDuty(o);
        } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Action")){
            int i = find(actions, o.getSubject().toString());
            object = actions.get(i).parse(o);
            if (actions.get(i).toString() != null) {
                instances.putAll(actions.get(i).instances);
               // actionString = assembleActions();
            }
        } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Constraint")
        || subjectType.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){
            int i = find(constraints, o.getSubject().toString());
            object = constraints.get(i).parse(o);
            if (constraints.get(i).toString() != null) {
                instances.putAll(constraints.get(i).instances);
                //constraintString = assembleConstraints();
            }
        }
      //  output = actionString + constraintString;
        updateInstances(object);

        return object;

    }

    private void parseDuty(Statement o) {
        Property p = o.getPredicate();

        if (p.toString().equals("http://www.w3.org/ns/odrl/2/action")) {

            //the object can either be the name of an actions or a pointer to an instance of actions.
            if (o.getObject().asResource().hasProperty(a)) { // check that it has a property type. If yes, check that type is Action.

                if (o.getObject().asResource().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Action")) {
                    actions.add(new Action(o.getObject().toString(), instances));
                    addChild(getLast(actions));
                } // could potentially remove this if statement.

            } else {
                Action act = new Action( "action_" + this.getId(), instances);
                act.action = getNaturalLanguage(o.getObject().toString());
                actions.add(act);
                addChild(act);
                if (act.toString() != null) {
                }
            }
        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/constraint")){
            if (o.getObject().asResource().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){
                constraints.add( new LogicalConstraint(o.getObject().toString(), instances));
                addChild(getLast(constraints));
            } else {
                constraints.add( new Constraint(o.getObject().toString(), instances));
                addChild(getLast(constraints));
            }
        }

    }


    protected void parseAction(Statement o){}

    protected String assembleActions(){ //What happens if operand is parsed last?
       String actionString = "";
       for (ODRLElement act : actions) {
           if (actionString == "") {
               actionString = "\n" + act.toString();
           } else {
               actionString = actionString + "\nand " + act.toString();
           }
       }
       return actionString;
    }

    protected String assembleConstraints(){ //What happens if operand is parsed last?
        if (constraints.size() == 0){
            return "";
        }

       String constraintString = "";
        for (ODRLElement c : constraints){
            if (constraintString == ""){
                constraintString = "\n" + c.toString();

            } else {
                constraintString = constraintString + "\nand \n" + c.toString();
            }
        }
        return constraintString;
    }

    protected String assembleTargets() {
       String targetString = "";
        for (ODRLElement t : target){
            if(t.getId().equals("http://www.w3.org/ns/odrl/2/O1")){
            }
            if (targetString == "" ){
                targetString = "\n" + t.toString();
            } else {
                targetString = targetString + "\nand " + t.toString();
            }
        }
        return targetString;
    }

}
