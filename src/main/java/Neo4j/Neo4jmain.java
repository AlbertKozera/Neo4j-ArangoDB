package Neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.util.Scanner;

public class Neo4jmain {
    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "admin"));
            Session session = driver.session()) {
            Service service = new Service();
            int id;
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
                        session.writeTransaction(service::readAllNodes);
                        break;
                    case 2:
                        System.out.println("Wprowadz ID");
                        id = input.nextInt();
                        input.nextLine();
                        int finalId1 = id;
                        session.writeTransaction(tx -> service.selectByID(tx, finalId1));
                        break;
                    case 3:
                        session.writeTransaction(service::readNodesOrderBy);
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
                        String finalNazwa_OSP = nazwa_OSP;
                        int finalLiczba_Wozow = liczba_Wozow;
                        int finalRok_Powstania = rok_Powstania;
                        session.writeTransaction(tx -> service.createOSP(tx, finalNazwa_OSP, finalLiczba_Wozow, finalRok_Powstania));
                        session.writeTransaction(tx -> service.createRelationship(tx, finalNazwa_OSP, "ZOSP"));
                        break;
                    case 5:
                        System.out.println("Wprowadz ID OSP do usuniecia");
                        id = input.nextInt();
                        input.nextLine();
                        int finalId = id;
                        session.writeTransaction(tx -> service.deleteByID(tx, finalId));
                        break;
                    case 6:
                        System.out.println("Wprowadz ID OSP do aktualizacji");
                        id = input.nextInt();
                        input.nextLine();
                        System.out.println("Nazwa");
                        nazwa_OSP = input.nextLine();
                        System.out.println("Liczba wozow");
                        liczba_Wozow = input.nextInt();
                        input.nextLine();
                        System.out.println("Rok powstania");
                        rok_Powstania = input.nextInt();
                        input.nextLine();
                        int finalId2 = id;
                        String finalNazwa_OSP1 = nazwa_OSP;
                        int finalLiczba_Wozow1 = liczba_Wozow;
                        int finalRok_Powstania1 = rok_Powstania;
                        session.writeTransaction(tx -> service.updateOSP(tx, finalId2, finalNazwa_OSP1, finalLiczba_Wozow1, finalRok_Powstania1));
                        break;
                    case 7:
                        session.writeTransaction(tx -> service.capslock(tx, true));
                        break;
                    case 8:
                        session.writeTransaction(tx -> service.capslock(tx, false));
                        break;
                    case 9:
                        session.close();
                        System.exit(0);
                        break;
                }
            }
        }
    }
}
