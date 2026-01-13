package StudentManagmentSystem.repository.implementations;

import StudentManagmentSystem.DbConnection;
import StudentManagmentSystem.models.Student;
import StudentManagmentSystem.repository.interfaces.StudentInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * SQL implementacija {@link StudentInterface} interfejsa.
 * Ova klasa služi za direktnu komunikaciju sa tabelom {@code Student} u bazi podataka.
 * Omogućava dodavanje, izmjenu, brisanje i pretragu studenata uz praćenje metapodataka.
 */
public class StudentRepository implements StudentInterface {

    /**
     * Ubacuje novi zapis o studentu u bazu podataka.
     * Polje {@code datumKreiranja} se ne šalje eksplicitno jer je u SQLite bazi
     * definisano kao {@code DEFAULT CURRENT_TIMESTAMP}.
     * * @param student Objekat koji sadrži podatke o studentu za unos.
     * @throws RuntimeException Ukoliko dođe do greške prilikom izvršavanja SQL upita.
     */
    @Override
    public void addStudent(Student student) {
        String sql = "INSERT INTO Student (brojIndeksa, sifra, ime, prezime, studijskiProgram, godinaUpisa, referentKojiJeDodao) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, student.getIndexNumber());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getFirstName());
            ps.setString(4, student.getLastName());
            ps.setString(5, student.getStudyProgram());
            ps.setInt(6, student.getEnrollmentYear());
            ps.setString(7, student.getAddedByReferentId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo dodavanje studenta: " + e.getMessage(), e);
        }
    }

    /**
     * Ažurira podatke postojećeg studenta na osnovu originalnog broja indeksa.
     * Metoda takođe ažurira {@code datumAzuriranja} vrijednošću koja je prethodno
     * pripremljena u servisnom sloju.
     * * @param student Objekat sa novim podacima.
     * @param index Originalni broj indeksa (primarni ključ) prije izmjene.
     * @return {@code true} ako je bar jedan red u bazi uspješno ažuriran.
     */
    @Override
    public boolean updateStudent(Student student, String index) {
        String sql = "UPDATE Student SET sifra = ?, ime = ?, prezime = ?, studijskiProgram = ?, godinaUpisa = ?, datumAzuriranja = ? WHERE brojIndeksa = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getPassword());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getStudyProgram());
            ps.setInt(5, student.getEnrollmentYear());
            ps.setString(6, student.getUpdatedAt());
            ps.setString(7, index);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Nije uspjelo ažuriranje studenta!", e);
        }
    }

    /**
     * Trajno uklanja studenta iz baze podataka.
     * * @param student Objekat studenta kojeg treba obrisati.
     * @return {@code true} ako je brisanje uspješno.
     */
    @Override
    public boolean deleteStudent(Student student) {
        String sql = "DELETE FROM Student WHERE brojIndeksa = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, student.getIndexNumber());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju studenta!", e);
        }
    }

    /**
     * Vraća listu svih studenata registrovanih u sistemu.
     * * @return {@link ArrayList} sa svim studentima.
     */
    @Override
    public ArrayList<Student> getAllStudents() {
        String sql = "SELECT * FROM Student";
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nije uspio prikaz svih studenata!", e);
        }
        return students;
    }

    /**
     * Pronalazi studenta prema primarnom ključu (broj indeksa).
     * * @param indexNumber Broj indeksa za pretragu.
     * @return {@link Optional} koji sadrži studenta ili je prazan ako student ne postoji.
     */
    @Override
    public Optional<Student> getStudentByIndex(String indexNumber) {
        String sql = "SELECT * FROM Student WHERE brojIndeksa = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, indexNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvaćanju studenta!", e);
        }
        return Optional.empty();
    }

    /**
     * Pretražuje studente čije prezime počinje zadatim prefiksom.
     * Koristi SQL operator {@code LIKE} za fleksibilnu pretragu.
     * * @param prefix Početna slova prezimena.
     * @return Lista studenata koji zadovoljavaju kriterij.
     */
    @Override
    public ArrayList<Student> findByLastNamePrefix(String prefix) {
        String sql = "SELECT * FROM Student WHERE prezime LIKE ?";
        ArrayList<Student> students = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri pretrazi studenata!", e);
        }
        return students;
    }

    /**
     * Pomoćna metoda za konverziju SQL reda (ResultSet) u Java objekat {@link Student}.
     * * @param rs ResultSet pozicioniran na tekući red.
     * @return Popunjen objekat {@link Student}.
     * @throws SQLException Ukoliko dođe do greške pri mapiranju kolona.
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("brojIndeksa"),
                rs.getString("sifra"),
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getString("studijskiProgram"),
                rs.getInt("godinaUpisa"),
                rs.getString("datumKreiranja"),
                rs.getString("datumAzuriranja"),
                rs.getString("referentKojiJeDodao")
        );
    }
}