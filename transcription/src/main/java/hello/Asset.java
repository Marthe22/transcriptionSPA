package  hello;
import org.apache.jena.rdf.model.Statement;

import java.util.HashMap;

public class Asset extends ODRLElement {
    public AssetCollection partOf;
    public Asset(String id, HashMap instances) {
        super(id, instances);
    }

    @Override
    public ODRLElement parse(Statement o) {
        System.out.println("Asset.parse");
        return null;
    }
}
