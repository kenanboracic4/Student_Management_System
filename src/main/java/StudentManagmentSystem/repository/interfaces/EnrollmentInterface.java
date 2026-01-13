package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Enrollment;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs koji definiše operacije nad podacima o upisima studenata na predmete.
 * Upis predstavlja vezu između studenta i kursa u specifičnoj akademskoj godini,
 * uključujući informacije o ocjeni i reviziji podataka.
 */
public interface EnrollmentInterface {

    /**
     * Kreira novi zapis o upisu studenta na predmet.
     * Implementacija metode treba da osigura perzistenciju polja {@code addedByReferentId}
     * kako bi se pratilo ko je izvršio inicijalni unos.
     *
     * @param enrollment Objekat koji sadrži podatke o upisu.
     * @return {@link Enrollment} objekat koji je spašen u bazi (obično sa generisanim metapodacima).
     */
    Enrollment create(Enrollment enrollment);

    /**
     * Pronalazi specifičan zapis o upisu koristeći kompozitni primarni ključ.
     * Kompozitni ključ se sastoji od broja indeksa, šifre predmeta i akademske godine.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param courseCode Jedinstvena šifra predmeta.
     * @param academicYear Akademska godina (npr. "2023/2024").
     * @return {@link Optional} sa pronađenim upisom ili prazan ako zapis ne postoji.
     */
    Optional<Enrollment> findByPrimaryKey(String studentIndexNumber, String courseCode, String academicYear);

    /**
     * Vraća listu svih predmeta koje je određeni student upisao u konkretnoj akademskoj godini.
     * Korisno za generisanje semestralnih listova ili pregleda po godinama.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param academicYear Akademska godina za koju se traže podaci.
     * @return Lista upisa koji zadovoljavaju kriterije.
     */
    List<Enrollment> findByStudentAndYear(String studentIndexNumber, String academicYear);

    /**
     * Ažurira podatke o postojećem upisu.
     * Najčešće se koristi za unos ocjene, ali i za naknadne izmjene.
     * Implementacija mora automatski postaviti {@code changeDate} i evidentirati
     * {@code modifiedByReferentId} radi revizije (audit log).
     *
     * @param enrollment Objekat sa ažuriranim podacima.
     * @return {@code true} ako je ažuriranje u bazi uspješno, inače {@code false}.
     */
    boolean update(Enrollment enrollment);

    /**
     * Briše zapis o upisu iz baze podataka na osnovu ključnih identifikatora.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param courseCode Šifra predmeta.
     * @param academicYear Akademska godina.
     * @return {@code true} ako je brisanje izvršeno, inače {@code false}.
     */
    boolean delete(String studentIndexNumber, String courseCode, String academicYear);

    /**
     * Vraća listu svih upisa evidentiranih u sistemu.
     *
     * @return Lista svih {@link Enrollment} objekata.
     */
    List<Enrollment> findAll();
}