import hello.DFSinRDF;
import hello.ODRLElement;
import hello.Tree;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import org.junit.Test;

import java.io.*;

import static junit.framework.TestCase.assertTrue;


public class DFSinRDFTest {


    @Test
    public void outputIsCMEDerivedDataLicense() throws IOException {
        String policyType = "Offer";
        String filename = "cme-derived-data";
        InputStream in = FileManager.get().open( filename + ".ttl");
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }

        // build and write model
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null, "TTL");

        DFSinRDF dfSinRDF;
        dfSinRDF = new DFSinRDF(model, "http://www.w3.org/ns/odrl/2/" + policyType);
        Tree tree = dfSinRDF.rdfDFSCaller();
        ODRLElement policy = (ODRLElement)tree.getRoot().getUserObject();
        String result = policy.toString();
        try {
            PrintWriter out1 = new PrintWriter(filename + "_transcription.txt");
            out1.println(result);
            out1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File fileOut = new File("cme-derived-data_transcription.txt");
        File comparison = new File(
                "/Users/martheboulleau/Documents/Refinitiv_Work/Refinitiv_NLG/src/test/java/cme-derived-data_comparison.txt");
        assertTrue("The files differ!", FileUtils.contentEquals(comparison,
                fileOut));


    }

    @Test
    public void outputIsCatALicense() throws IOException {
        String policyType = "Offer";
        String filename = "CatA";
        InputStream in = FileManager.get().open( filename + ".ttl");
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filename + " not found");
        }

        // build and write model
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null, "TTL");

        DFSinRDF dfSinRDF = new DFSinRDF(model, "http://www.w3.org/ns/odrl/2/" + policyType);
        Tree tree = dfSinRDF.rdfDFSCaller();
        ODRLElement policy = (ODRLElement)tree.getRoot().getUserObject();
        String result = policy.toString();
        try {
            PrintWriter out1 = new PrintWriter(filename + "_transcription.txt");
            out1.println(result);
            out1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File fileOut = new File("/Users/martheboulleau/Documents/Refinitiv_Work/Refinitiv_NLG/CatA_transcription.txt");
        File comparison = new File(
                "/Users/martheboulleau/Documents/Refinitiv_Work/Refinitiv_NLG/src/test/java/CatA_comparison.txt");
        assertTrue("The files differ!", FileUtils.contentEquals(comparison,
                fileOut));
        System.out.println();


    }

}
