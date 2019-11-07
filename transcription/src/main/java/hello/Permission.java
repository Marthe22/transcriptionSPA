package hello;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class Permission extends Rule {
    private ArrayList<ODRLElement> duty = new ArrayList<>(); //array
    private String assigner = ""; //only one Party name
    private String assignee = ""; //only one Party name
    public boolean isProhibition = false;
    private String permissionString = null;
    private ArrayList<ODRLElement> assetOut = new ArrayList<>(); //Can there be more than one?
    private String url = "";
    private HashMap<String, String> definitions = new HashMap<>();


    public Permission(String id, HashMap instances) {
        super(id, instances);
    }
/*============================================================================================================*/

    public ODRLElement parse(Statement o){
        ODRLElement object = this;
        Resource s = o.getSubject();
        String subjectType = s.getProperty(a).getObject().toString();

       if (subjectType.equals("http://www.w3.org/ns/odrl/2/Permission")
       || subjectType.equals("http://www.w3.org/ns/odrl/2/Prohibition") ){
            parsePermission(o);

        }else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Constraint")
       || subjectType.equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){
           // then constraint will be of subjectType constraint.
           int i = find(constraints, s.toString());
           object = constraints.get(i).parse(o);
           if (constraints.get(i).toString() != null){
               instances.putAll(constraints.get(i).instances);
           }

       } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Action")){
           int i = find(actions, o.getSubject().toString());
           object = actions.get(i).parse(o);
           if (actions.get(i).toString() != null){
               instances.putAll(actions.get(i).instances);
           }
       }else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Duty")) {
           int i = find(duty, o.getSubject().toString());
           object = duty.get(i).parse(o);
           if (duty.get(i).toString() != null) {
               instances.putAll(duty.get(i).instances);
           }
       } else if (subjectType.equals("http://www.w3.org/ns/odrl/2/Asset")){
        //Then it is either a target or an output.

           int i = find(target, o.getSubject().toString());

           if(i >= 0){
               object = target.get(i).parse(o);
               if (target.get(i).toString() != null) {
                   instances.putAll(target.get(i).instances);
               }
           } else {
               i = find(assetOut, o.getSubject().toString());
               object = assetOut.get(i).parse(o);
               if (assetOut.get(i).toString() != null) {
                   instances.putAll(assetOut.get(i).instances);
               }
           }
       }

        this.updateInstances(object);
           return object;
    }

    /*======================================================================================================*/

    private void parsePermission(Statement o){
        String predicate = o.getPredicate().toString();
        // Maybe use cases.
        if (predicate.equals("http://www.w3.org/ns/odrl/2/action")){
            if(instances.containsKey(o.getObject().toString())){
                actions.add(instances.get(o.getObject().toString()));
                addChild(getLast(actions));
            } else{
            parseAction(o);}

        } else if (predicate.equals("http://www.w3.org/ns/odrl/2/target")){
            if(instances.containsKey(o.getObject().toString())){
                // make a dummy asset with the output that has already been computed.
                target.add(instances.get(o.getObject().toString()));
                addChild(getLast(target));

            } else {
                target.add(new AssetCollection(o.getObject().toString(), instances));
                addChild(getLast(target));
            }

        } else if (predicate.equals("http://www.w3.org/ns/odrl/2/duties")){
            duty.add(new Duty(o.getObject().toString(), instances));
            addChild(getLast(duty));

        } else if (predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
            permissionString = getNaturalLanguage(o.getObject().toString());

        } else if (predicate.equals("http://www.w3.org/ns/odrl/2/assigner")){
            assigner = getNaturalLanguage(o.getObject().toString());

        }else if (predicate.equals("http://www.w3.org/ns/odrl/2/assignee")){
            assignee = getNaturalLanguage(o.getObject().toString());

        } else if (predicate.equals("http://www.w3.org/ns/odrl/2/constraint")){
            if(instances.containsKey(o.getObject().toString())){
                // make a dummy asset with the output that has already been computed.
                constraints.add(instances.get(o.getObject().toString()));
                addChild(instances.get(o.getObject().toString()));
                //constraintString = assembleConstraints();
            }
            else if (o.getObject().asResource().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/LogicalConstraint")){
                constraints.add(new LogicalConstraint(o.getObject().toString(), instances));
                addChild(getLast(constraints));
            } else {
                constraints.add(new Constraint(o.getObject().toString(), instances));
                addChild(getLast(constraints));
            }

        } else if(predicate.equals("http://www.w3.org/ns/odrl/2/output")){
            System.out.println("predicate is output.");
           if(instances.containsKey(o.getObject().toString())){
               url = o.getObject().toString();
               System.out.println("This thing happened.");
                // Here, I need to check whether this output is used as target for any other permission and reevaluate it.
            } else {
               AssetCollection asset = new AssetCollection(o.getObject().toString(), instances);
               asset.setOutput(this);
               assetOut.add(asset);
               addChild(asset);
            }

        } else {System.out.println("Could not determine nature of statement for " + predicate);}



    }

    private String assembleDuties() {
       if (!isProhibition) {
           String dutyString = "";
           for (ODRLElement d : duty) {
               if (!d.toString().equals("")) {
                   if (dutyString == "") {
                       dutyString = "\nif" + d.toString();
                   } else {
                       dutyString = dutyString + "\nand if " + d.toString();
                   }
               }
           }
           return dutyString;
       } else {
           String dutyString = "";
           for (ODRLElement d : duty) {
               if (!d.toString().equals("")) {
                   if (dutyString == "") {
                       dutyString = "\nand if violated, as a remedy  \n \t" + d.toString();
                   } else {
                       dutyString = dutyString + "\nand \n\t" + d.toString();
                   }
               }
           }
        return dutyString;
       }
    }


    // ==================== PARSE ACTIONS ===================================
    @Override
    protected void parseAction(Statement o){
        //the object can either be the name of an action or a pointer to an instance of actions.
        //the object can also indicate next policy.

        if(o.getObject().asResource().hasProperty(a)){ // check that it has a property subjectType. If yes, check that subjectType is Action.
            if (instances.containsKey(o.getObject().toString())){
                actions.add(instances.get(o.getObject().toString()));
                addChild(getLast(actions));
            } else if (o.getObject().asResource().getProperty(a).getObject().toString().equals("http://www.w3.org/ns/odrl/2/Action")) {
                actions.add(new Action(o.getObject().toString(), instances));
                addChild(getLast(actions));
                //could potentially remove this if statement.
            }
        } else {
            //Not sure this will happen, as, for permission, any action should be created as an instance of Action.
            Action act = new Action( this.getId(), instances);
            act.initialiseAction(o.getObject().toString());
            actions.add(act);
            addChild(getLast(actions));
        }
    }


    @Override
    public String toString(){
        if(!url.equals("")){
            ODRLElement isOutput = DFsearch(url);
            if (isOutput instanceof AssetCollection){
                ((AssetCollection) isOutput).setOutput(this);
                updateInstances(isOutput);
            }
        }
        if(permissionString != null) {
            String targetString = assembleTargets().replace("\n", "\n\t");
            String out1 = permissionString.replace("$1", assembleActions()).replace("$2", targetString)
                    .replace("\n", "\n\t");

            return "Permission: " + getIdentifier(this.getId()) + "\n" + out1 +
                    assembleConstraints().replace("\n", "\n \t") +
                    assembleDuties().replace("\n", "\n \t");
        }
        else {return "Permission cannot be assembled.";}


    }

    public HashMap<String, String> toDefinition(){
        for (ODRLElement a : actions){
            definitions.putAll(a.toDefinition());
        }
        for (ODRLElement c : constraints){
            definitions.putAll(c.toDefinition());
        }
        for (ODRLElement t : target){
            definitions.putAll(t.toDefinition());
        }
        return definitions;
    }

}
