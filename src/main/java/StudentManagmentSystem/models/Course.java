package StudentManagmentSystem.models;

import java.util.Objects;

/**
 * Predstavlja model kursa (predmeta) unutar sistema za upravljanje studentima.
 * Sadrži informacije o šifri kursa, nazivu, ECTS bodovima, semestru i metapodatke o kreiranju.
 * * @author Kenan Boračić
 * @version 1.0
 */
public class Course {

    /** Jedinstvena šifra kursa (npr. CS101) */
    private String courseCode;

    /** Puni naziv predmeta */
    private String name;

    /** Broj ECTS bodova koje kurs nosi */
    private int ects;

    /** Semestar u kojem se kurs izvodi (1-8) */
    private int semester;

    /** Datum i vrijeme kreiranja zapisa u sistemu */
    private String createdAt;

    /** Datum i vrijeme posljednje izmjene podataka o kursu */
    private String updatedAt;

    /** Identifikator (ID) referenta koji je unio kurs u bazu */
    private String addedByReferentId;

    /**
     * Konstruktor za kreiranje novog kursa sa svim poljima, uključujući vremenske oznake.
     * Koristi se obično prilikom učitavanja podataka iz baze.
     *
     * @param courseCode Jedinstvena šifra kursa (npr. CS101).
     * @param name Naziv kursa.
     * @param ects Broj ECTS bodova koje kurs nosi.
     * @param semester Semestar u kojem se kurs izvodi.
     * @param createdAt Datum i vrijeme kreiranja zapisa.
     * @param updatedAt Datum i vrijeme posljednje izmjene zapisa.
     * @param addedByReferentId ID referenta koji je dodao kurs u sistem.
     */
    public Course(String courseCode, String name, int ects, int semester, String createdAt, String updatedAt, String addedByReferentId) {
        this.courseCode = courseCode;
        this.name = name;
        this.ects = ects;
        this.semester = semester;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.addedByReferentId = addedByReferentId;
    }

    /**
     * Konstruktor za kreiranje novog kursa bez vremenskih oznaka.
     * Koristi se prilikom inicijalnog unosa novog kursa u sistem.
     *
     * @param courseCode Jedinstvena šifra kursa.
     * @param name Naziv kursa.
     * @param ects Broj ECTS bodova.
     * @param semester Semestar izvođenja.
     * @param addedByReferentId ID referenta koji unosi kurs.
     */
    public Course(String courseCode, String name, int ects, int semester, String addedByReferentId) {
        this.courseCode = courseCode;
        this.name = name;
        this.ects = ects;
        this.semester = semester;
        this.addedByReferentId = addedByReferentId;
    }

    // --- GETTERS AND SETTERS ---

    /** * Dohvata jedinstvenu šifru kursa.
     * @return String koji predstavlja identifikator kursa.
     */
    public String getCourseCode() { return courseCode; }

    /** * Postavlja jedinstvenu šifru kursa.
     * @param courseCode Nova šifra kursa (npr. MAT1).
     */
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    /** * Dohvata puni naziv predmeta.
     * @return Naziv kursa kao String.
     */
    public String getName() { return name; }

    /** * Postavlja novi naziv za predmet.
     * @param name Naziv kursa (npr. Matematika 1).
     */
    public void setName(String name) { this.name = name; }

    /** * Vraća broj ECTS bodova koje nosi ovaj kurs.
     * @return Broj bodova kao cijeli broj.
     */
    public int getEcts() { return ects; }

    /** * Postavlja broj ECTS bodova za ovaj predmet.
     * @param ects Broj bodova (npr. 6).
     */
    public void setEcts(int ects) { this.ects = ects; }

    /** * Vraća semestar u kojem se kurs sluša.
     * @return Redni broj semestra (1-8).
     */
    public int getSemester() { return semester; }

    /** * Postavlja semestar u kojem će se izvoditi kurs.
     * @param semester Broj semestra.
     */
    public void setSemester(int semester) { this.semester = semester; }

    /** * Vraća datum kada je kurs ubačen u bazu podataka.
     * @return Datum u tekstualnom formatu.
     */
    public String getCreatedAt() { return createdAt; }

    /** * Postavlja datum kreiranja zapisa o kursu.
     * @param createdAt Datum kreiranja.
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** * Vraća datum kada su podaci o kursu posljednji put promijenjeni.
     * @return Datum posljednjeg ažuriranja.
     */
    public String getUpdatedAt() { return updatedAt; }

    /** * Postavlja datum ažuriranja zapisa.
     * @param updatedAt Datum izmjene.
     */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /** * Dohvata ID referenta koji je odgovoran za dodavanje ovog kursa.
     * @return Korisničko ime ili ID referenta.
     */
    public String getAddedByReferentId() { return addedByReferentId; }

    /** * Postavlja ID referenta koji je unio kurs.
     * @param addedByReferentId ID referenta iz sistema.
     */
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }


    /**
     * Vraća tekstualnu reprezentaciju kursa sa ključnim informacijama.
     * @return String formatiran za prikaz korisniku (Šifra, Naziv, ECTS, Semestar).
     */
    @Override
    public String toString() {
        return "Šifra: " + courseCode +
                ", Naziv: " + name +
                ", ECTS: " + ects +
                ", Semestar: " + semester +
                ", Dodao: " + (addedByReferentId != null ? addedByReferentId : "Sistem");
    }

    /**
     * Poredi dva Course objekta na osnovu logičkih polja (šifra, naziv, ects i semestar).
     * @param o Objekat za poređenje.
     * @return true ako su objekti logički identični, inače false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return ects == course.ects &&
                semester == course.semester &&
                Objects.equals(courseCode, course.courseCode) &&
                Objects.equals(name, course.name);
    }

    /**
     * Generiše hash code vrijednost za instancu kursa.
     * @return int hash vrijednost bazirana na poljima: courseCode, name, ects, semester.
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseCode, name, ects, semester);
    }
}