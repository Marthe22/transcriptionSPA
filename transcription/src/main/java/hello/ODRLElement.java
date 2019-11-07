package hello;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ODRLElement  extends MyNode{
    private String filename = "odrl.ttl";
    private Model dictionary = ModelFactory.createDefaultModel();
    private Property label = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    private Property rbimTranscription = ResourceFactory.createProperty("http://example.org/rbim/transcription");
    private Property definition = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#definition");
    public Property a = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    public HashMap<String,ODRLElement> instances;



    public ODRLElement(String id, HashMap instances_in){
        super(id);
        InputStream in = FileManager.get().open(filename);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }
        dictionary.read(in, null, "TTL");
        this.instances = instances_in;
    }

    public abstract ODRLElement parse(Statement o);
    public HashMap<String, String > toDefinition() {
        System.out.println("!!! ODRLElement.toDefinition() is called!!!!");
        return null;
    }

    public String toString(){
        return "ODRLElement.toString() was called.";}

   /////////////////////////////////GET TRANSCRIPTION/LABEL //////////////////////////////////
    private String getLabel(String uri){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, label, (Object) null);

        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return uri + "  -NOT FOUND-";
    }

    private String getRbimTranscription(String uri){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, rbimTranscription, (Object) null);

        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return "-NOT FOUND-";
    }

    protected String getNaturalLanguage(String uri){
        String text;
        text = getRbimTranscription(uri);
        if (text.equals("-NOT FOUND-")){
            text = getLabel(uri);
        }
        return text;
    }

    protected String getDefinition(String uri){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, definition, (Object) null);
        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return "-NOT FOUND-";
    }

    protected Integer find(ArrayList<? extends ODRLElement> constraints, String id){
        for (int i = 0; i < constraints.size(); i++){
            if(constraints.get(i).getId().equals(id)){
                return i;
            }
        }
        System.out.println("Element " + id + " not found.");
        return -1;
    }

    protected String parseLiterals(Literal l){
        String xsdLiteral = "";

        if (l.getDatatype().equals(XSDDatatype.XSDduration)){
            try {
                Duration duration = DatatypeFactory.newInstance().newDuration(l.getValue().toString());
                xsdLiteral = printDuration(duration);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }

        } else if (l.getDatatype().equals(XSDDatatype.XSDstring)){
            xsdLiteral = l.getString();

        } else  if (l.getDatatype().equals(XSDDatatype.XSDfloat)){

            xsdLiteral = l.getValue().toString();
        }else  if (l.getDatatype().equals(XSDDatatype.XSDinteger)){

            xsdLiteral = l.getValue().toString();
        }

        return xsdLiteral;
    }

    public void updateInstances(ODRLElement object){
        if (instances.containsKey(object.getId())){
            instances.replace(object.getId(), object);
        }else{
            instances.put(object.getId(), object);
        }
    }

    private String printDuration(Duration d){
        String duration = "";
        if(d.getYears() != 0){
            duration = duration + d.getYears() + " year";
            if(d.getYears() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " ";}
        }
        if (d.getMonths() != 0){
            duration = duration + d.getMonths() + " month";
            if(d.getMonths() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " ";}
        }
        if (d.getDays() != 0){
            duration = duration + d.getDays() + " day";
            if(d.getDays() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " "; }
        }
        if (d.getHours() != 0){
            duration = duration + d.getHours() + " hour";
            if(d.getHours() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " ";}
        }
        if (d.getMinutes() != 0){
            duration = duration + d.getMinutes() + " minute";
            if(d.getMinutes() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " ";}
        }
        if (d.getSeconds() != 0){
            duration = duration + d.getSeconds() + " second";
            if(d.getSeconds() > 1 ){
                duration = duration + "s ";
            } else {duration = duration + " ";}
        }
        return duration;
    }

    public String getIdentifier( String url){
        String identifier = null;
        try {
            identifier = new File(new URL(url).getPath()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return identifier;
    }

    public <E> E getLast(ArrayList<E> arrayList){
       if(arrayList.isEmpty()){
           return null;
       } else {
           return arrayList.get(arrayList.size()-1);
       }
    }
}
