import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class GetLabel {
    private String filename = "odrl.ttl";
    private Model dictionary = ModelFactory.createDefaultModel();
    private Property label = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    private Property rbimTranscription = ResourceFactory.createProperty("http://example.org/rbim/transcription");

    public GetLabel(){

        InputStream in = FileManager.get().open(filename);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }
        dictionary.read(in, null, "TTL");

    }
    public String getTheLabel(String uri){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, label, (Object) null);
        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            System.out.println("in while loop");
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return uri + "  -NOT FOUND-";
    }

    public String getRbimTranscription(String uri){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, rbimTranscription, (Object) null);

        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return "-NOT FOUND-";
    }
}
