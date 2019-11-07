package hello;

import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class AssetCollection extends Asset{
    private ArrayList<ODRLElement> refinements = new ArrayList<>();
    public AssetCollection(String id, HashMap instances) {
        super(id, instances);
    }
    private ODRLElement resource;
    private boolean isOutput = false;
    private ArrayList<ODRLElement> sources = new ArrayList<>();
    private HashMap<String,String> definitions = new HashMap<>();


    @Override
    public ODRLElement parse(Statement o) {
        Resource s = o.getSubject();
        ODRLElement object = this;
        if(s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Asset")){
            parseAssetCollection(o);

        } else if (s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")) {
            int i = find(refinements, o.getSubject().toString());
            object = refinements.get(i).parse(o);
            if (refinements.get(i).toString() != null) {
                instances.putAll(refinements.get(i).instances);
            }

        } else if (s.getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Constraint")){
            int i = find(refinements, o.getSubject().toString());
            object = refinements.get(i).parse(o);
            if (refinements.get(i).toString() != null) {
                instances.putAll(refinements.get(i).instances);
            }

        } else if (s.getProperty(a).getObject().toString().equals("http://example.org/rbim/Resource")){
                resource.parse(o);
                if (resource.toString() != null) {
                    instances.putAll(resource.instances);
                }

        }

        return object;
    }

    private void parseAssetCollection(Statement o){
        Property p = o.getPredicate();
        if (p.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#value")) { // <== Ask Ben/Marko about this, seems like the asset triples are all wrong in the example.
            //Check whether the resource has already been parsed.
            if (instances.containsKey(o.getObject().toString())){
                resource = instances.get(o.getObject().toString());
                addChild(resource);

            } else {
                resource = new Resrce(o.getObject().toString(), instances);
                addChild(resource);

            }

        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/refinement")) {
            //determine the class of the object, and add it to refinement.
            String newObj = o.getObject().asResource().getProperty(a).getObject().toString(); //TODO: check if constraint already exists.
            if (newObj.equals("http://www.w3.org/ns/odrl/2/Constraint")) {
                refinements.add(new Constraint(o.getObject().toString(), instances)); // will be added at the end of refinements list. Hence the following line.
                addChild(getLast(refinements));

            } else if (newObj.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")) {
                refinements.add(new LogicalConstraint(o.getObject().toString(), instances));
                addChild(getLast(refinements));
            }
        }

            updateInstances(this);


    }

    // SAME AS ASSEMBLECONSTRAINTS IN RULE.
    private String assembleRefinements(){ //What happens if operand is parsed last?
        String refinementString = "";
        for (ODRLElement r : refinements){
            if (refinementString == "" ){
                refinementString = "\n" + r.toString();
            } else{
                refinementString = refinementString + "\n and " + r.toString();
            }
        }
        return refinementString;

    }

    @Override
    public String toString(){
        if(isOutput){
            String outputString = "";
            for (ODRLElement s : sources){
                if (outputString.equals("")){
                    outputString = " output of " + s.getIdentifier(s.getId());
                } else {
                    outputString = outputString + " and of " + s.getIdentifier(s.getId());
                }
            }
            return outputString;
        } else if (resource != null) {
            return resource.toString() + assembleRefinements().replace("\n","\n\t");
        } else {
            return null;
        }
    }

    public void setOutput(ODRLElement source){ //TODO: for when output is in next policy.
        isOutput = true;
        if(!sources.contains(source)) {
            sources.add(source);
        }
        updateInstances(this);
    }

    @Override
    public HashMap<String, String> toDefinition(){

        for (ODRLElement r : refinements){

            definitions.putAll(r.toDefinition());
        }
        definitions.putAll(resource.toDefinition());
        return  definitions;
    }

}
