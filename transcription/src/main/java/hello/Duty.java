package hello;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.HashMap;

public class Duty extends Rule {
    public Duty(String id, HashMap instances) {
        super(id, instances);
    }
    protected String nextPolicy;
    private HashMap<String, String> definitions = new HashMap<>();

    @Override
    public ODRLElement parse(Statement o) {
        Resource s = o.getSubject();
        String subjectType = s.getProperty(a).getObject().toString();
        ODRLElement object = this;

        if(subjectType.equals("http://www.w3.org/ns/odrl/2/Duty")){
            parseDuty(o);
        } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Action")){
            int i = find(actions, o.getSubject().toString());
            object = actions.get(i).parse(o);
            if (actions.get(i).toString() != null) {
                instances.putAll(actions.get(i).instances);
            }
        } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Constraint")
                || subjectType.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint" )){
            int i = find(constraints, o.getSubject().toString());
            object = constraints.get(i).parse(o);
            if (constraints.get(i).toString() != null) { //TODO: maybe no if, put the object in instances straight away and it will update.
                instances.putAll(constraints.get(i).instances);
            }
        } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Set")
        || subjectType.equals("http://www.w3.org/ns/odrl/2/Offer")
        || subjectType.equals("http://www.w3.org/ns/odrl/2/Agreement")){
            int i = find(target, o.getSubject().toString());
            object = target.get(i).parse(o);

           // if(target.get(i).toString() != null){
                instances.putAll(target.get(i).instances);
            //}
        }

        updateInstances(object);

        return object;

    }

    private void parseDuty(Statement o) {
        Property p = o.getPredicate();
        if (p.toString().equals("http://www.w3.org/ns/odrl/2/action")) {
            parseAction(o);
        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/constraint")){
            // Can either be a Constraint or a LogicalConstraint
            if (instances.containsKey(o.getObject().toString())){
                constraints.add(instances.get(o.getObject().toString()));
                addChild(getLast(constraints));
            } else {

                if (o.getObject().asResource().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")) {
                    constraints.add(new LogicalConstraint(o.getObject().toString(), instances));
                    addChild(getLast(constraints));

                } else {
                    constraints.add(new Constraint(o.getObject().toString(), instances));
                    addChild(getLast(constraints));

                }
            }
        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/target")){
            //create policy and add to target.
            String targetType = o.getObject().asResource().getProperty(a).getObject().toString();
            if(instances.containsKey(o.getObject().toString())){
                target.add(instances.get(o.getObject().toString()));
                addChild(instances.get(o.getObject().toString()));
                nextPolicy = getIdentifier( o.getObject().toString());

            } else if(targetType.equals("http://www.w3.org/ns/odrl/2/Set")){ // Not sure if any of the following ever happens.
                target.add(new Policy(o.getObject().toString(), instances));
                addChild(getLast(target));
                nextPolicy = getIdentifier( o.getObject().toString());

            } else if(targetType.equals("http://www.w3.org/ns/odrl/2/Offer")){
                target.add(new Offer(o.getObject().toString(), instances));
                addChild(getLast(target));
                nextPolicy = getIdentifier( o.getObject().toString());

            } else if(targetType.equals("http://www.w3.org/ns/odrl/2/Agreement")){
                target.add(new Agreement(o.getObject().toString(), instances));
                addChild(getLast(target));
                nextPolicy = getIdentifier( o.getObject().toString());

            }
        }
    }

    //================================ PARSE ACTIONS ==============================
    protected void parseAction(Statement o){
        //the object can either be the name of an actions or a pointer to an instance of actions.
        // the object can also indicate next policy.

        if(o.getObject().asResource().hasProperty(a)){ // check that it has a property subjectType. If yes, check that subjectType is Action.
            String objectType = o.getObject().asResource().getProperty(a).getObject().toString();
            if (instances.containsKey(o.getObject().toString())){
                actions.add(instances.get(o.getObject().toString()));
                addChild(getLast(actions));
            } else if (objectType.equals("http://www.w3.org/ns/odrl/2/Action")) {
                actions.add(new Action(o.getObject().toString(), instances));
                addChild(getLast(actions));

                //could potentially remove this if statement.
            } else if(objectType.equals("http://www.w3.org/ns/odrl/2/Set")
                    || objectType.equals("http://www.w3.org/ns/odrl/2/Offer")
                    || objectType.equals("http://www.w3.org/ns/odrl/2/Agreement")){
                // then it s a next Policy
                //TODO: NOT SURE WHAT TO DO HERE.
            }
        } else {
            Action act = new Action( this.getId(), instances);
            act.initialiseAction(o.getObject().toString());
            actions.add(act);
            addChild(getLast(actions));

        }
    }

    @Override
    protected String assembleTargets() {
        // if a duty has a target, then it s a next policy duty.
        if (target.size() == 0){
            return "";
        }
        String targetString = "Next Policy is: " + nextPolicy + "\n";
        for(ODRLElement t :target){
            targetString = targetString + t.toString();
        }
        targetString = targetString.replace("\n", "\n \t\t");
        return targetString;
    }


    @Override
    public String toString(){
        String out = assembleActions().replace("$", assembleConstraints()).replace("\n", "\n\t")
                + assembleTargets();
        return out;
    }

    @Override
    public HashMap<String, String> toDefinition(){
        for (ODRLElement a : actions){
            definitions.putAll(a.toDefinition());
        }
        for (ODRLElement c : constraints){
            definitions.putAll(c.toDefinition());
        }
        return definitions;
    }

}
