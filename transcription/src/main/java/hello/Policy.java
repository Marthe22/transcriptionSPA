package hello;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class Policy extends ODRLElement {
    protected ArrayList<ODRLElement> rules = new ArrayList<>();
    private HashMap<String, String> definitions = new HashMap<>();


    public Policy(String id, HashMap instances) {super(id, instances); }

    @Override
    public ODRLElement parse(Statement o) {
        ODRLElement object = this;
        Resource s = o.getSubject();
        String subjectType = s.getProperty(a).getObject().toString();

        if (subjectType.equals("http://www.w3.org/ns/odrl/2/Set") ||
                subjectType.equals("http://www.w3.org/ns/odrl/2/Offer") ||
                        subjectType.equals("http://www.w3.org/ns/odrl/2/Agreement" )){
            this.parsePolicy(o);
        }else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Permission")
        || subjectType.equals("http://www.w3.org/ns/odrl/2/Prohibition")){

            int i = find(rules, s.toString());

            object = rules.get(i).parse(o);
            if (rules.get(i).toString() != null){
                instances.putAll(rules.get(i).instances);
            }
        }

        this.updateInstances(object);

        return object;
    }


    private void parsePolicy(Statement o){
        String predicate = o.getPredicate().toString();

        if (predicate.equals("http://www.w3.org/ns/odrl/2/permission")){
            if(instances.containsKey(o.getObject().toString())){
                //highly unlikely.
                rules.add(instances.get(o.getObject().toString()));
                addChild(instances.get(o.getObject().toString()));
            } else {
                rules.add(new Permission(o.getObject().toString(), instances));
                addChild(getLast(rules));
            }
        } else if (predicate.equals("http://www.w3.org/ns/odrl/2/prohibition")){
            if(instances.containsKey(o.getObject().toString())){
                //highly unlikely.
                rules.add(instances.get(o.getObject().toString()));
                addChild(getLast(rules));
            } else {
                Permission perm = new Permission(o.getObject().toString(), instances);
                perm.isProhibition = true;
                rules.add(perm);
                addChild(perm);

            }
        }
    }

    private String assembleRules() {
        String rulesString = "";
        for (ODRLElement r : rules){
            if(rulesString == ""){
                rulesString = "\n" + r.toString();
            }
          else if (r.toString() != null) {
               rulesString = rulesString + "\n \n" + r.toString();
           }
       }
        return rulesString;
    }

    @Override
    public String toString(){
        return  "Policy " + getIdentifier(this.getId()) + "\n" + assembleRules();

    }

    public HashMap<String, String> toDefinition(){
        for (ODRLElement r : rules){
            definitions.putAll(r.toDefinition());
        }

        return definitions;
    }
}
