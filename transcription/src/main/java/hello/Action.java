package hello;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.HashMap;


public class Action extends ODRLElement {
    //Is Included in really needed in our case??

    private ODRLElement refinements;
    protected String action; // TODO: change to private with a setAction function.
    protected String actionDef;
    private HashMap<String,String > definitions = new HashMap<>();

    public Action(String id, HashMap instances){
        super(id, instances);
    }

    @Override
    public ODRLElement parse(Statement o) {
        Resource s = o.getSubject();
        ODRLElement object = this;

        if (s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Action")){
            parseAction(o);

        } else if (s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Constraint") ||
                s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){
            // Parse refinement.=================================================================
            object = refinements.parse(o);
            if(refinements.toString() != null){
                instances.putAll(refinements.instances);
            }

        }

        // put strings together.
        if (action != null){
             updateInstances(object);
        }

        return object;
    }

    private void parseAction(Statement o) {
        Property p = o.getPredicate();

        if (p.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#value")) {
            action = getNaturalLanguage(o.getObject().toString());
            actionDef = getDefinition(o.getObject().toString());
            definitions.put(getNaturalLanguage(o.getObject().toString()), getDefinition(o.getObject().toString()));
            System.out.println("Definition of " + getIdentifier(o.getObject().toString()) + " is: " + actionDef);

        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/refinement")) {

            //determine the class of the object, and add it to refinement.
            String newObj = o.getObject().asResource().getProperty(a).getObject().toString();
            if (newObj.equals("http://www.w3.org/ns/odrl/2/Constraint")) {
                refinements = new Constraint(o.getObject().toString(), instances);
                addChild(refinements);

            } else if (newObj.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")) {
                refinements = new LogicalConstraint(o.getObject().toString(), instances);
                addChild(refinements);

            }
        }

    }

    public void initialiseAction(String url){
        this.action = getNaturalLanguage(url);
        actionDef = getDefinition(url);
        definitions.put(getNaturalLanguage(url), getDefinition(url));
    }

    @Override
    public String toString(){
        if (action == null){
            return null;
        }
        if(refinements != null) {
            return action.replace("$", refinements.toString());
        }
        return action;
    }

    @Override
    public HashMap<String,String> toDefinition(){
        if(refinements != null) {
            definitions.putAll(refinements.toDefinition());
        }
        return definitions;
    }

}
