
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.*;

public class Rdf4j {
    private Repository db;
    private RepositoryConnection conn;

    public static void main(String[] args) throws Exception {
        System.out.println("start");

        Rdf4j rdf4j = new Rdf4j();

        rdf4j.init();

        rdf4j.runQuery(getString("sparql_distinct_types"), "type");
        rdf4j.runQuery(getString("sparql_distinct_prop"),"rel");
        rdf4j.runQuery(getString("sparql_event_count"), "count");
        rdf4j.runQuery(getString("sparql_event_name_desc"), "name", "desc");
        rdf4j.runQuery(getString("sparql_localbusiness"), "name", "opens", "closes");

        rdf4j.db.shutDown();

        System.out.println("end");
    }

    private void runQuery(String queryString, String... vars) {
        System.out.println(queryString);
        System.out.println("----------------");
        TupleQuery query = conn.prepareTupleQuery(queryString);

        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                BindingSet solution = result.next();
                for(String s: vars){
                    System.out.println("?"+s+" = " + solution.getValue(s));
                }
            }
        }
        System.out.println("----------------\n");
    }

    private void init() throws IOException {
        this.db = new SailRepository(new MemoryStore());
        this.db.initialize();
        this.conn = this.db.getConnection();
        InputStream input = Rdf4j.class.getResourceAsStream("/changed-seefeld.jsonld");
        conn.add(input, "", RDFFormat.JSONLD);
        System.out.println("init done");
    }

    private static String getString(String filename) throws IOException{
        InputStream input = Rdf4j.class.getResourceAsStream("/"+filename + ".txt");
        return IOUtils.toString(input, "UTF-8");
    }

}