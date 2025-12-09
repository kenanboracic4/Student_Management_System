package org.example;

import org.example.models.Student;
import org.example.repository.implementations.StudentRepository;
import org.example.repository.interfaces.StudentInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {


        try {
            DbConnection.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("FATALNA GREŠKA: Inicijalizacija baze nije uspjela. Aplikacija se gasi.");
            System.exit(1);
        }

        System.out.println("Baza uspješno inicijalizirana/tabele provjerene.");

        // Korak 2: Test konekcije i pokretanje aplikacije
        try (Connection conn = DbConnection.getConnection()) {

            if (conn != null) {
                System.out.println("✅ Konekcija je uspješno uspostavljena. Spremni za rad!");

                StudentInterface studRepo =  new StudentRepository();

                ArrayList<Student> studenti = studRepo.getAllStudents();

                Optional<Student> pronadjeniStudent = studRepo.getStudentByIndex("473");
                System.out.println(pronadjeniStudent);


            } else {
                System.err.println("Greska: Konekcioni objekat je null.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Greska pri radu s bazom: " + e.getMessage());
        }
    }
}