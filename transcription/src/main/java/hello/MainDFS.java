package hello;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

public class MainDFS {

    public static void main(String[] args) {
        String policyType = "Offer";
        //String filename = "cme-derived-data";
        String filename = "CatA";
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
        System.out.println("RESULT:" + result);
        try {
            PrintWriter out1 = new PrintWriter(filename + "_transcription.txt");
            out1.println(result);
            out1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(policy.toDefinition().keySet());
        HTMLwriter htmLwriter = new HTMLwriter();
        htmLwriter.writeHTML(result, filename);


    }

}
