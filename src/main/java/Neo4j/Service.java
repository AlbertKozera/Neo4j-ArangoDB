package Neo4j;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

public class Service {
    public Result createGroup(Transaction transaction, String nazwa_Grupy) {
        String command = "CREATE (:Grupa {nazwaGrupy:$nazwa_Grupy})";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nazwa_Grupy", nazwa_Grupy);
        return transaction.run(command, parameters);
    }

    public Result createOSP(Transaction transaction, String nazwa_OSP, int liczba_Wozow, int rok_Powstania) {
        String command = "CREATE (:OSP {nazwaOSP:$nazwa_OSP, liczbaWozow:$liczba_Wozow, rokPowstania:$rok_Powstania})";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nazwa_OSP", nazwa_OSP);
        parameters.put("liczba_Wozow", liczba_Wozow);
        parameters.put("rok_Powstania", rok_Powstania);
        return transaction.run(command, parameters);
    }

    public Result createRelationship(Transaction transaction, String nazwa_OSP, String nazwa_Grupy) {
        String command =
                "MATCH (o:OSP),(g:Grupa) " +
                        "WHERE o.nazwaOSP = $nazwa_OSP AND g.nazwaGrupy = $nazwa_Grupy "
                        + "CREATE (o)−[r:JEST_W]−>(g)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nazwa_OSP", nazwa_OSP);
        parameters.put("nazwa_Grupy", nazwa_Grupy);
        return transaction.run(command, parameters);
    }

    public Result readAllNodes(Transaction transaction) {
        String command =
                "MATCH (n)" +
                        "RETURN n";
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public void printField(Pair<String, Value> field) {
        System.out.println("field = " + field);
        Value value = field.value();
        if (TYPE_SYSTEM.NODE().isTypeOf(value))
            printNode(field.value().asNode());
        else if (TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value))
            printRelationship(field.value().asRelationship());
        else
            throw new RuntimeException();
    }

    public void printRelationship(Relationship relationship) {
        System.out.println("id = " + relationship.id());
        System.out.println("type = " + relationship.type());
        System.out.println("startNodeId = " + relationship.startNodeId());
        System.out.println("endNodeId = " + relationship.endNodeId());
        System.out.println("asMap = " + relationship.asMap());
    }

    public void printNode(Node node) {
        System.out.println("id = " + node.id());
        System.out.println("labels = " + " : " + node.labels());
        System.out.println("asMap = " + node.asMap());
    }

    public Result deleteEverything(Transaction transaction) {
        String command = "MATCH (n) DETACH DELETE n";
        return transaction.run(command);
    }

    public Result readNodesOrderBy(Transaction transaction) {
        String command ="MATCH (o:OSP)\n" +
                "RETURN o\n" +
                "ORDER BY o.rokPowstania";
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public Result selectByID(Transaction transaction, int id_OSP){
        String command = "MATCH (o:OSP) where ID(o) = $id_OSP return o";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_OSP", id_OSP);
        Result result = transaction.run(command, parameters);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public Result updateOSP(Transaction transaction, int id_OSP, String nazwa_OSP, int liczba_Wozow, int rok_Powstania) {
        String command = "MATCH (o:OSP) WHERE ID(o)=$id_OSP SET o.nazwaOSP=$nazwa_OSP, o.liczbaWozow=$liczba_Wozow, o.rokPowstania=$rok_Powstania return o";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_OSP", id_OSP);
        parameters.put("nazwa_OSP", nazwa_OSP);
        parameters.put("liczba_Wozow", liczba_Wozow);
        parameters.put("rok_Powstania", rok_Powstania);
        return transaction.run(command, parameters);
    }

    public Result capslock(Transaction transaction, boolean capslock) {
        if(capslock){
            String command = "MATCH(o:OSP) SET o.nazwaOSP = toUpper(o.nazwaOSP) return o";
            return transaction.run(command);
        }
        else {
            String command = "MATCH(o:OSP) SET o.nazwaOSP = toLower(o.nazwaOSP) return o";
            return transaction.run(command);
        }
    }

    public Result deleteByID(Transaction transaction, int id_OSP) {
        String command = "MATCH (o:OSP) WHERE ID(o) = $id_OSP DETACH DELETE o ";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_OSP", id_OSP);
        return transaction.run(command, parameters);
    }
}
