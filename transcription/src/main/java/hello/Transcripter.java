package hello;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


public class Transcripter {

    private  String result;

    public Transcripter(MultipartFile file){
        this.result = transcript(file);

    }
    private String transcript(MultipartFile file){
        String policyType = "Offer";
        //String filename = "cme-derived-data";
        //String filename = "CatA";
        //String filename = "dummyLicense";

        InputStream in = null;
        try {
            in = new BufferedInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* try {
            in = file.getInputStream();
        } catch (IOException e) {
            result = "InputStream is null";
            e.printStackTrace();
            return result;

        }

        if (in == null) {
            ;
            throw new IllegalArgumentException(
                    "File: " + file.getName() + " not found");
        }*/
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
