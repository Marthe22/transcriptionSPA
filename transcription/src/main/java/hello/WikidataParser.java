package hello;

import org.apache.jena.query.*;

public class WikidataParser {
    public String qCode;
    private String queryString;

    public WikidataParser(String code) {
        this.qCode = code;


      queryString  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n"
                + "PREFIX wd: <http://www.wikidata.org/entity/>  \n"
                + "select  * \n"
                + "where { \n"
                + "       wd:" + qCode + " rdfs:label ?label . \n"
                + "  FILTER (langMatches( lang(?label), 'EN' ) ) \n"
                + "      }  \n"
                + "LIMIT 1 \n";
    }



    public String run(){
        System.out.println("qCode is : " + qCode);
        Query query = QueryFactory.create(queryString);
        QueryExecution q = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", queryString);
        ResultSet results = q.execSelect();

        String currency = "";

        while (results.hasNext()){
            currency = results.next().get("label").toString().replace("@en","");
        }

        q.close();
        return currency;
    }

}
