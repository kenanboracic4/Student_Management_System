package StudentManagmentSystem.models;

import java.util.Objects;

/**
 * Predstavlja model studenta unutar sistema.
 * Klasa sadrži lične podatke studenta, informacije o studijskom programu,
 * kredencijale za pristup sistemu, te metapodatke o tome ko je i kada unio studenta.
 * * @author Kenan Boračić
 * @version 1.0
 */
public class Student {

    /** Jedinstveni broj indeksa studenta (npr. "250/23") */
    private String indexNumber;

    /** Lozinka studenta za pristup studentskom panelu */
    private String password;

    /** Ime studenta */
    private String firstName;

    /** Prezime studenta */
    private String lastName;

    /** Naziv studijskog programa kojem student pripada */
    private String studyProgram;

    /** Godina u kojoj je student prvi put upisao studij */
    private int enrollmentYear;

    /** Datum i vrijeme kreiranja kartona studenta u bazi */
    private String createdAt;

    /** Datum i vrijeme posljednje izmjene bilo kojeg podatka o studentu */
    private String updatedAt;

    /** Identifikator (ID) referenta koji je registrovao studenta u sistem */
    private String addedByReferentId;

    /**
     * Konstruktor za puni prikaz studenta sa svim metapodacima.
     * Koristi se prilikom dobavljanja podataka iz baze podataka (npr. pri pretrazi ili listanju).
     *
     * @param indexNumber Jedinstveni broj indeksa studenta.
     * @param password Lozinka studenta za prijavu na sistem.
     * @param firstName Ime studenta.
     * @param lastName Prezime studenta.
     * @param studyProgram Naziv studijskog programa (npr. "Softversko inženjerstvo").
     * @param enrollmentYear Godina prvog upisa na fakultet.
     * @param createdAt Datum i vrijeme kreiranja kartona studenta.
     * @param updatedAt Datum i vrijeme posljednje izmjene podataka.
     * @param addedByReferentId ID referenta koji je registrovao studenta u sistem.
     */
    public Student(String indexNumber, String password, String firstName, String lastName,
                   String studyProgram, int enrollmentYear, String createdAt,
                   String updatedAt, String addedByReferentId) {
        this.indexNumber = indexNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studyProgram = studyProgram;
        this.enrollmentYear = enrollmentYear;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.addedByReferentId = addedByReferentId;
    }

    /**
     * Konstruktor za kreiranje novog studenta.
     * Koristi se prilikom inicijalne registracije studenta u bazu podataka.
     *
     * @param indexNumber Jedinstveni broj indeksa studenta.
     * @param password Lozinka za pristup.
     * @param firstName Ime studenta.
     * @param lastName Prezime studenta.
     * @param studyProgram Studijski program.
     * @param enrollmentYear Godina upisa.
     * @param addedByReferentId ID referenta koji unosi podatke.
     */
    public Student(String indexNumber, String password, String firstName, String lastName,
                   String studyProgram, int enrollmentYear, String addedByReferentId) {
        this.indexNumber = indexNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studyProgram = studyProgram;
        this.enrollmentYear = enrollmentYear;
        this.addedByReferentId = addedByReferentId;
    }

    // --- GETTERS AND SETTERS ---

    /** * Dohvata broj indeksa studenta.
     * @return String koji predstavlja broj indeksa.
     */
    public String getIndexNumber() { return indexNumber; }

    /** * Postavlja broj indeksa studenta.
     * @param indexNumber Novi broj indeksa.
     */
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    /** * Dohvata lozinku studenta.
     * @return String lozinka.
     */
    public String getPassword() { return password; }

    /** * Postavlja novu lozinku za studenta.
     * @param password Nova lozinka.
     */
    public void setPassword(String password) { this.password = password; }

    /** * Dohvata ime studenta.
     * @return Ime studenta.
     */
    public String getFirstName() { return firstName; }

    /** * Postavlja ime studenta.
     * @param firstName Ime studenta.
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** * Dohvata prezime studenta.
     * @return Prezime studenta.
     */
    public String getLastName() { return lastName; }

    /** * Postavlja prezime studenta.
     * @param lastName Prezime studenta.
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** * Dohvata naziv studijskog programa.
     * @return Naziv studijskog programa.
     */
    public String getStudyProgram() { return studyProgram; }

    /** * Postavlja studijski program studenta.
     * @param studyProgram Naziv programa.
     */
    public void setStudyProgram(String studyProgram) { this.studyProgram = studyProgram; }

    /** * Dohvata godinu prvog upisa studenta.
     * @return Godina upisa kao int.
     */
    public int getEnrollmentYear() { return enrollmentYear; }

    /** * Postavlja godinu upisa studenta.
     * @param enrollmentYear Godina upisa.
     */
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    /** * Dohvata datum kreiranja zapisa studenta u sistemu.
     * @return Datum kreiranja.
     */
    public String getCreatedAt() { return createdAt; }

    /** * Postavlja datum kreiranja zapisa.
     * @param createdAt Datum kreiranja.
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** * Dohvata datum posljednjeg ažuriranja podataka o studentu.
     * @return Datum izmjene.
     */
    public String getUpdatedAt() { return updatedAt; }

    /** * Postavlja datum posljednje izmjene podataka.
     * @param updatedAt Datum izmjene.
     */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /** * Dohvata ID referenta koji je inicijalno registrovao studenta.
     * @return Identifikator referenta.
     */
    public String getAddedByReferentId() { return addedByReferentId; }

    /** * Postavlja ID referenta koji vrši registraciju studenta.
     * @param addedByReferentId ID referenta.
     */
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }

    /**
     * Vraća tekstualnu reprezentaciju objekta Student.
     * @return String sa osnovnim podacima: indeks, ime, prezime i program.
     */
    @Override
    public String toString() {
        return "Student: [" + indexNumber + "] " + firstName + " " + lastName +
                " | Program: " + studyProgram +
                " | Dodao referent: " + addedByReferentId;
    }

    /**
     * Poredi dva objekta tipa Student na osnovu broja indeksa.
     * Broj indeksa se smatra primarnim ključem za identifikaciju studenta.
     * * @param o Objekat koji se poredi.
     * @return true ako su brojevi indeksa isti, inače false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(indexNumber, student.indexNumber);
    }

    /**
     * Generiše jedinstvenu hash vrijednost na osnovu broja indeksa studenta.
     * @return int hash vrijednost.
     */
    @Override
    public int hashCode() {
        return Objects.hash(indexNumber);
    }
}