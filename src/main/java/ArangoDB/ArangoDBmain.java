package ArangoDB;

import com.arangodb.*;
import java.util.*;

public class ArangoDBmain {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Service service = new Service();
        ArangoDB arangoDB = new ArangoDB.Builder().user("albert").password("albert").build(); // polaczenie
        ArangoDatabase db = arangoDB.db("strazPozarna"); // utworzenie bazy
        ArangoGraph graph = db.graph("some-graph");
        ArangoVertexCollection arangoVertexCollection = graph.vertexCollection("osp");
        ArangoEdgeCollection arangoEdgeCollection = graph.edgeCollection("edges");

        String id;
        String nazwa_OSP;
        int liczba_Wozow, rok_Powstania;
        while (true) {
            System.out.println("\nS T R A Z - P O Z A R N A");
            System.out.println("1. Wyswietl wszystkie vertexy");
            System.out.println("2. Wyswietl konkretna OSP");
            System.out.println("3. Wyswietl OSP od najstarszego");
            System.out.println("4. Dodaj");
            System.out.println("5. Usun");
            System.out.println("6. Aktualizuj");
            System.out.println("7. Przetwarzanie danych [caps ON]");
            System.out.println("8. Przetwarzanie danych [caps OFF]");
            System.out.println("9. Zakoncz prace klastra");
            int mode = input.nextInt();
            input.nextLine();
            switch (mode) {
                case 1:
                    service.readAll(arangoDB);
                    break;
                case 2:
                    System.out.println("Wprowadz ID");
                    id = input.nextLine();
                    service.readByID(arangoVertexCollection, id);
                    break;
                case 3:
                    service.sortFromOldest(arangoDB);
                    break;
                case 4:
                    System.out.println("Nazwa");
                    nazwa_OSP = input.nextLine();
                    System.out.println("Liczba wozow");
                    liczba_Wozow = input.nextInt();
                    input.nextLine();
                    System.out.println("Rok powstania");
                    rok_Powstania = input.nextInt();
                    input.nextLine();
                    service.createOSP(graph, nazwa_OSP, liczba_Wozow, rok_Powstania, "osp");
                    break;
                case 5:
                    System.out.println("Wprowadz ID OSP do usuniecia");
                    id = input.nextLine();
                    service.deleteByID(arangoDB, id, "osp", arangoEdgeCollection);
                    break;
                case 6:
                    System.out.println("Wprowadz ID OSP do aktualizacji");
                    id = input.nextLine();
                    System.out.println("Nazwa");
                    nazwa_OSP = input.nextLine();
                    System.out.println("Liczba wozow");
                    liczba_Wozow = input.nextInt();
                    input.nextLine();
                    System.out.println("Rok powstania");
                    rok_Powstania = input.nextInt();
                    input.nextLine();
                    service.update(arangoDB, id, nazwa_OSP, liczba_Wozow, rok_Powstania, "osp");
                    break;
                case 7:
                    service.capslock(arangoDB, true);
                    break;
                case 8:
                    service.capslock(arangoDB, false);
                    break;
                case 9:
                    System.exit(0);
                    break;
            }
        }
    }
}
