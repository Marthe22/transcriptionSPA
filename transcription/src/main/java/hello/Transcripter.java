package hello;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import java.io.InputStream;


public class Transcripter {

    String result;

    public Transcripter(String filename){
        this.result = transcript(filename);

    }
    private String transcript(String filename){
        String policyType = "Offer";
        //String filename = "cme-derived-data";
        //String filename = "CatA";
        //String filename = "dummyLicense";
        InputStream in = FileManager.get().open( filename + ".ttl");

        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }

        // build and write model
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null, "TTL");
        model.write(System.out, "N3");

        // Use Depth-First Search algorithm to parse ODRL license and create tree.
        DFSinRDF dfSinRDF = new DFSinRDF(model, "http://www.w3.org/ns/odrl/2/" + policyType);
        Tree tree = dfSinRDF.rdfDFSCaller();
        ODRLElement policy = (ODRLElement) tree.getRoot().getUserObject();
        System.out.println("\n\n\n\n\n");
        String result = policy.toString();
        return result;
    }

    public String getResult(){
        return result;
    }
}
