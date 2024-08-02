package org.example;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import javax.sql.DataSource;
import java.util.Scanner;

public class DatenbankSpiel {
    // Methode zur Erzeugung einer Datenquelle (DataSource)
    public static DataSource createDataSource() {
        // Erstellt und konfiguriert eine MySQL-Datenquelle (DataSource)
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/" + "fcadaal"); // Setzt die URL der Datenbank
        dataSource.setUser("root"); // Setzt den Datenbankbenutzer
        dataSource.setPassword("xxxxx"); // Setzt das Passwort für die Datenbank
        return dataSource; // Gibt die konfigurierte Datenquelle zurück
    }

    // Methode zum Anzeigen der Spiele
    public static void displayGames() {
        DataSource ds = createDataSource(); // Erstellt die Datenquelle
        // Verbindung mit der Datenbank herstellen
        try (Connection connection = ds.getConnection()) {
            System.out.println((connection.isValid(0) ? "Die Verbindung war erfolgreich!" : "Oops, die Verbindung konnte nicht hergestellt werden!"));
            // SQL-Abfrage, um die Spiele anzuzeigen
            String anzeigen = "SELECT CONCAT(sl.slDatum,\" \",sl.slUhrzeit), la.laNr, la.laName, ao.aoNr, ao.aoName FROM spiel sl " +
                    "INNER JOIN liga la ON la.laNr = sl.liga_laNr " +
                    "INNER JOIN austragungsort ao ON ao.aoNr = sl.austragungsort_aoNr ";

            // Abfrage wird vorbereitet und ausgeführt, um die Spiele anzuzeigen
            try (PreparedStatement ps = connection.prepareStatement(anzeigen); ResultSet rs = ps.executeQuery()) {
                // Counter fürs Zählen der Spiele
                int counter = 0;
                System.out.println("================================================================================================================");
                System.out.format("%50s\n", "Spiele");
                System.out.println("================================================================================================================");
                System.out.format("%1s%20s%20s%22s%28s\n", "Spieltermin", "LaNr", "Liga", "AoNr", "Austragungsort");
                System.out.println("================================================================================================================");
                // Die Daten werden in rs gespeichert und hier abgelesen
                while (rs.next()) {
                    String termin = rs.getString(1); // Spieltermin
                    int laNr = rs.getInt(2); // Liga-Nummer
                    String la = rs.getString(3); // Liga-Name
                    int aoNr = rs.getInt(4); // Austragungsort-Nummer
                    String ao = rs.getString(5); // Austragungsort-Name
                    // Ausgabe der Spielinformationen
                    System.out.format("%1s%10d%32s%10d%32s  \n", termin, laNr, la, aoNr, ao);
                    System.out.println("----------------------------------------------------------------------------------------------------------------");
                    counter++;
                }
                System.out.println("Insgesamt: " + counter + " Spiele");
                System.out.println("----------------------------------------------------------------------------------------------------------------");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Methode zum Hinzufügen neuer Spiele
    public static void addGame() {
        DataSource ds = createDataSource(); // Erstellt die Datenquelle
        // Verbindung mit der Datenbank herstellen
        try (Connection connection = ds.getConnection()) {
            System.out.println((connection.isValid(0) ? "Die Verbindung war erfolgreich!" : "Oops, die Verbindung konnte nicht hergestellt werden!"));
            // Scanner wird verwendet, damit der User die Spielinfos eingeben kann
            Scanner scanner = new Scanner(System.in);
            String spielDatum = "";
            String spielUhrzeit = "";
            int ao = 0, la = 0;
            System.out.println("Datum des Spiels mit dem Format \"Year-Month-Day\" eingeben: ");
            spielDatum = scanner.nextLine();
            System.out.println("Spiel der Termin mit dem Format \"Hour:MinutesUhr\" eingeben: ");
            spielUhrzeit = scanner.nextLine();
            System.out.println("Liga eingeben: ");
            la = scanner.nextInt();
            System.out.println("Austragungsort eingeben: ");
            ao = scanner.nextInt();
            // SQL-Abfrage, um ein Spiel hinzuzufügen
            String einfuegen = "INSERT INTO spiel VALUES(?,?,?,?)";
            // Abfrage wird vorbereitet und die Infos werden in die Tabelle Spiel eingefügt
            PreparedStatement ps = connection.prepareStatement(einfuegen);
            ps.setInt(1, la);
            ps.setString(2, spielDatum);
            ps.setString(3, spielUhrzeit);
            ps.setInt(4, ao);
            ps.executeUpdate();
            System.out.println("Daten erfolgreich eingefügt!");
        } catch (SQLException e) {
            System.out.println("Der Fehler ist: " + e.getMessage());
        }
    }



    // Main Methode
    public static void main(String[] args) {
        // Dashboard zur Verwaltung der Datenbank
        while (true) {
            Scanner scanner = new Scanner(System.in);
            int choice;
            System.out.println("Welcome to MySQL!");
            System.out.println("Bitte auswählen: \n 1. Spiele abrufen \n 2. Spiel einfügen");
            choice = scanner.nextInt();
            // Benutzerwahl verarbeiten
            switch (choice) {
                case 1:
                    displayGames(); // Spiele anzeigen
                    break;
                case 2:
                    addGame(); // Neues Spiel hinzufügen
                    break;

            }
            System.out.println("Do you want to exit? (y/n)");
            char answer = scanner.next().charAt(0);
            if (answer == 'y') {
                System.exit(0); // Programm beenden, wenn Benutzer 'y' eingibt
            }
        }
    }
}