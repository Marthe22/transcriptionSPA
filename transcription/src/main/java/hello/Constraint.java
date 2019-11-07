package hello;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

import java.util.HashMap;


// also consider loading the dictionary only once, like in one of the software eng cw.

public class Constraint extends ODRLElement {
    private String operator = "$1";
    private String rightOp = "$2";
    private String leftOp = "$NOT FOUND$";
    private String unit = "";
    private String rightOpDef = "";
    private HashMap definitions = new HashMap();

    public Constraint(String id, HashMap instances) {
        super(id, instances);
    }


    public ODRLElement parse(Statement o){
        Property p = o.getPredicate();


        if (p.toString().equals("http://www.w3.org/ns/odrl/2/operator")){

            operator = getNaturalLanguage(o.getObject().toString());


        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/leftOperand")){
            leftOp = getNaturalLanguage(o.getObject().toString());

        } else if (p.toString().equals("http://www.w3.org/ns/odrl/2/rightOperand")){
            if (o.getObject().isLiteral()){
               rightOp = parseLiterals(o.getObject().asLiteral());

            }else if (o.getObject().toString().contains("wikidata")){
                String uri = o.getObject().toString().replace("https://www.wikidata.org/wiki/", "").replace("https://www.wikidata.org/entity/", "");
                rightOp= new WikidataParser(uri).run();

            }
            else {
                rightOp = getNaturalLanguage(o.getObject().toString());
                rightOpDef = getDefinition(o.getObject().toString());
                definitions.put(getNaturalLanguage(o.getObject().toString()), getDefinition(o.getObject().toString()));

            }

        }  else if (p.toString().equals("http://www.w3.org/ns/odrl/2/unit")){
            if (o.getObject().toString().contains("wikidata")){
                String uri = o.getObject().toString().replace("https://www.wikidata.org/wiki/", "").replace("https://www.wikidata.org/entity/", "");
                unit = " in " + new WikidataParser(uri).run();

            }

        }
        else {System.out.println("Could not determine nature of statement.");}

       //TODO: how often do I want to update this?
        updateInstances(this);



        return this;
    }



 @Override
    public String toString() {
     String out = leftOp.replace("$1", operator).replace("$2", rightOp + unit);
         if(out.equals("every 1 month ")){
            out = "monthly";
        } else if(out.equals("every 1 year ")){
            out = "yearly";
         } else if(out.equals("every 1 hour ")){
             out = "hourly";
         } else if(out.equals("every 1 day ")){
            out = "daily";
        }
        return out;

 }

 @Override
    public HashMap<String,String> toDefinition(){
        return definitions;
 }

}
