package hello;

import org.apache.jena.rdf.model.*;

import java.util.HashMap;

public class Resrce extends ODRLElement {
    public Resrce(String id, HashMap instances) {
        super(id, instances);
    }
    private String contentNature = "";
    private String resource = "";
    private String contentNatureDef = "";
    private HashMap<String,String> definitions = new HashMap<>();

    @Override
    public ODRLElement parse(Statement o) {
        ODRLElement object = this;
        Property p = o.getPredicate();
         if(p.toString().equals("http://www.w3.org/2000/01/rdf-schema#label")){
             resource = o.getObject().toString();
             updateInstances(object);
         } else if(p.toString().equals("http://example.org/rbim/contentNature")){
             contentNature = getNaturalLanguage(o.getObject().toString());
             contentNatureDef = getDefinition(o.getObject().toString());
             definitions.put(getNaturalLanguage(o.getObject().toString()), getDefinition(o.getObject().toString()));
             System.out.println("Definition of " + getIdentifier(o.getObject().toString()) + " is: " + contentNatureDef);
             updateInstances(object);
         }
        return object;
    }

    @Override
    public  String toString(){
        return contentNature + resource;
    }

    @Override
    public HashMap<String, String > toDefinition(){
        return definitions;
    }
}
