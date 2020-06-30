package ArangoDB;

import com.arangodb.*;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.GraphEntity;
import com.arangodb.model.EdgeCreateOptions;
import com.arangodb.model.GraphCreateOptions;
import com.arangodb.model.VertexCreateOptions;
import java.util.*;

class Service {
    void readAll(ArangoDB arangoDB) {
        List<String> list1 = new ArrayList<>();
        String query1 = "FOR t IN edges RETURN t";
        ArangoCursor<BaseDocument> cursor1 = arangoDB.db("strazPozarna").query(query1, null, null, BaseDocument.class);
        cursor1.forEachRemaining(edge ->
                list1.add(edge.getProperties().get("_to").toString().replace("zosp/", "")));
        BaseDocument tmpDocument = arangoDB.db("strazPozarna").collection("zosp").getDocument(list1.get(0), BaseDocument.class);
        System.out.println("Key: " + tmpDocument.getKey());
        System.out.println("Nazwa: " + tmpDocument.getAttribute("nazwa"));
        System.out.println("----------------------");
        List<String> list2 = new ArrayList<>();
        String query2 = "FOR t IN edges RETURN t";
        ArangoCursor<BaseDocument> cursor2 = arangoDB.db("strazPozarna").query(query2, null, null, BaseDocument.class);
        cursor2.forEachRemaining(edge ->
                list2.add(edge.getProperties().get("_from").toString().replace("osp/", "")));
        list2.forEach(tmp -> {
            BaseDocument myDocument = arangoDB.db("strazPozarna").collection("osp").getDocument(tmp, BaseDocument.class);
            System.out.println("Key: " + myDocument.getKey());
            System.out.println("Nazwa: " + myDocument.getAttribute("nazwa"));
            System.out.println("LiczbaWozow: " + myDocument.getAttribute("LiczbaWozow"));
            System.out.println("RokPowstania: " + myDocument.getAttribute("RokPowstania"));
            System.out.println("----------------------");
        });
    }

    void readByID(ArangoVertexCollection arangoVertexCollection, String key) {
        BaseDocument baseDocument = arangoVertexCollection.getVertex(key, BaseDocument.class);
        System.out.println("Key: " + baseDocument.getKey());
        System.out.println("Nazwa: " + baseDocument.getAttribute("nazwa"));
        System.out.println("LiczbaWozow: " + baseDocument.getAttribute("LiczbaWozow"));
        System.out.println("RokPowstania: " + baseDocument.getAttribute("RokPowstania"));
        System.out.println("----------------------");
    }

    void createOSP(ArangoGraph graph, String nazwa_OSP, int liczba_Wozow, int rok_Powstania, String vertexCollectionName) {
        BaseDocument document = new BaseDocument();
        String id = UUID.randomUUID().toString();
        document.setKey(id);
        document.addAttribute("nazwa", nazwa_OSP);
        document.addAttribute("LiczbaWozow", liczba_Wozow);
        document.addAttribute("RokPowstania", rok_Powstania);
        graph.vertexCollection(vertexCollectionName).insertVertex(document, new VertexCreateOptions());
        createRelationship(graph, "edges", vertexCollectionName + "/" + id, "zosp/29609");
    }

    void createRelationship(ArangoGraph graph, String edgeCollectionName, String from, String to) {
        ArangoEdgeCollection collection = graph.edgeCollection(edgeCollectionName);
        BaseEdgeDocument document = new BaseEdgeDocument(from, to);
        collection.insertEdge(document, new EdgeCreateOptions());
    }

    void deleteByID(ArangoDB arangoDB, String key, String collectionName, ArangoEdgeCollection edgeCollection) {
        Map<String, String> map = new HashMap<>();
        String query = "FOR t IN edges RETURN t";
        ArangoCursor<BaseDocument> cursor = arangoDB.db("strazPozarna").query(query, null, null, BaseDocument.class);
        cursor.forEachRemaining(edge ->
                map.put(edge.getProperties().get("_from").toString().replace("osp/", ""), edge.getKey()));
        edgeCollection.deleteEdge(map.get(key));
        arangoDB.db("strazPozarna").collection(collectionName).deleteDocument(key);
    }

    void update(ArangoDB arangoDB, String key, String nazwa_OSP, int liczba_Wozow, int rok_Powstania, String collectionName) {
        BaseDocument document = new BaseDocument();
        document.addAttribute("nazwa", nazwa_OSP);
        document.addAttribute("LiczbaWozow", liczba_Wozow);
        document.addAttribute("RokPowstania", rok_Powstania);
        arangoDB.db("strazPozarna").collection(collectionName).updateDocument(key, document);
    }

    void sortFromOldest(ArangoDB arangoDB) {
        String query = "FOR c IN osp SORT c.RokPowstania RETURN c";
        ArangoCursor<BaseDocument> cursor = arangoDB.db("strazPozarna").query(query, null, null, BaseDocument.class);
        cursor.forEachRemaining(vertex ->{
        System.out.println("Key: " + vertex.getKey());
        System.out.println("Nazwa: " + vertex.getProperties().get("nazwa"));
        System.out.println("LiczbaWozow: " + vertex.getProperties().get("LiczbaWozow"));
        System.out.println("RokPowstania: " + vertex.getProperties().get("RokPowstania"));
        System.out.println("----------------------");
        });
    }

    void capslock(ArangoDB arangoDB, boolean capslock) {
        String query;
        if(capslock)
            query  = "FOR c IN osp UPDATE c._key WITH { nazwa: UPPER(c.nazwa) } IN osp";
        else
            query = "FOR c IN osp UPDATE c._key WITH { nazwa: LOWER(c.nazwa) } IN osp";
        ArangoCursor<BaseDocument> cursor = arangoDB.db("strazPozarna").query(query, null, null, BaseDocument.class);
        cursor.forEachRemaining(vertex ->{
            System.out.println("Key: " + vertex.getKey());
            System.out.println("Nazwa: " + vertex.getProperties().get("nazwa"));
            System.out.println("LiczbaWozow: " + vertex.getProperties().get("LiczbaWozow"));
            System.out.println("RokPowstania: " + vertex.getProperties().get("RokPowstania"));
            System.out.println("----------------------");
        });
    }

    void createGraph(ArangoDatabase db) {
        EdgeDefinition edgeDefinition = new EdgeDefinition()
                .collection("edges")
                .from("osp")
                .to("zosp");
        GraphEntity graph = db.createGraph(
                "some-graph", Arrays.asList(edgeDefinition), new GraphCreateOptions()
        );
    }

    void createVertexCollection(ArangoGraph graph, String name) {
        graph.addVertexCollection(name);
    }

    void createGroup(ArangoGraph graph, String vertexCollectionName) {
        ArangoVertexCollection collection = graph.vertexCollection(vertexCollectionName);
        BaseDocument document = new BaseDocument();
        document.addAttribute("nazwa", "ZOSP");
        collection.insertVertex(document, new VertexCreateOptions());
    }
}
