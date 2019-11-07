import org.apache.jena.rdf.model.*;

public class NaturalLanguageGetter {
    private Model dictionary;
    private String uri;
    private Property label = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    private  Property rbimTranscription = ResourceFactory.createProperty("http://example.org/rbim/transcription");

    public NaturalLanguageGetter(Model dictionary, String uri){
        this.dictionary = dictionary;
        this.uri = uri;
    }

    private String getLabel(){
        Resource dummyResource = ResourceFactory.createResource(uri);
        Selector selectPredicate = new SimpleSelector(dummyResource, label, (Object) null);

        StmtIterator iteR = dictionary.listStatements(selectPredicate);
        while(iteR.hasNext()) {
            Statement selectedStatement = iteR.nextStatement();
            return selectedStatement.getObject().toString().replace("@en", "");
        }
        return "-NOT FOUND-";
    }

    private String getRbimTranscription(){
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
