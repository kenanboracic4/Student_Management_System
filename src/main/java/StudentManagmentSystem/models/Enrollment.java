package StudentManagmentSystem.models;

import java.util.Objects;

/**
 * Predstavlja model upisa (prijave) studenta na određeni kurs.
 * Ova klasa prati akademski uspjeh studenta na predmetu, uključujući ocjenu,
 * datume polaganja, kao i reviziju (ko je dodao ili izmijenio zapis).
 * * @author Kenan Boračić
 * @version 1.0
 */
public class Enrollment {

    /** Jedinstveni broj indeksa studenta */
    private String studentIndexNumber;

    /** Jedinstvena šifra kursa na koji se student upisuje */
    private String courseCode;

    /** Akademska godina upisa u formatu "YYYY/YYYY" */
    private String academicYear;

    /** Ostvarena ocjena na predmetu (6-10) ili null ako ispit nije položen */
    private Integer grade;

    /** Datum kada je ocjena zvanično evidentirana */
    private String gradeDate;

    /** Razlog zbog kojeg je podatak o upisu ili ocjeni naknadno mijenjan */
    private String changeReason;

    /** Datum i vrijeme posljednje modifikacije zapisa */
    private String changeDate;

    /** Identifikator (ID) referenta koji je inicijalno kreirao upis */
    private String addedByReferentId;

    /** Identifikator (ID) referenta koji je posljednji modifikovao zapis */
    private String modifiedByReferentId;

    /**
     * Konstruktor za puni prikaz zapisa o upisu.
     * Koristi se prvenstveno prilikom povlačenja podataka iz baze podataka kada su polja
     * poput ocjene i metapodataka o izmjenama već popunjena.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param courseCode Jedinstvena šifra kursa.
     * @param academicYear Akademska godina upisa (npr. "2023/2024").
     * @param grade Ostvarena ocjena (6-10) ili null ako student još nije polagao.
     * @param gradeDate Datum kada je ocjena unesena.
     * @param changeReason Razlog eventualne naknadne izmjene ocjene ili statusa.
     * @param changeDate Datum kada je izvršena posljednja izmjena zapisa.
     * @param addedByReferentId ID referenta koji je prvi kreirao upis.
     * @param modifiedByReferentId ID referenta koji je posljednji modifikovao zapis.
     */
    public Enrollment(String studentIndexNumber, String courseCode, String academicYear,
                      Integer grade, String gradeDate, String changeReason, String changeDate,
                      String addedByReferentId, String modifiedByReferentId) {
        this.studentIndexNumber = studentIndexNumber;
        this.courseCode = courseCode;
        this.academicYear = academicYear;
        this.grade = grade;
        this.gradeDate = gradeDate;
        this.changeReason = changeReason;
        this.changeDate = changeDate;
        this.addedByReferentId = addedByReferentId;
        this.modifiedByReferentId = modifiedByReferentId;
    }

    /**
     * Konstruktor za kreiranje novog upisa (inicijalna prijava predmeta).
     * Koristi se kada student tek prijavljuje predmet, bez ocjene i dodatnih izmjena.
     *
     * @param studentIndexNumber Broj indeksa studenta.
     * @param courseCode Šifra predmeta koji se upisuje.
     * @param academicYear Tekuća akademska godina.
     * @param addedByReferentId ID referenta koji vrši prijavu studenta.
     */
    public Enrollment(String studentIndexNumber, String courseCode, String academicYear, String addedByReferentId) {
        this.studentIndexNumber = studentIndexNumber;
        this.courseCode = courseCode;
        this.academicYear = academicYear;
        this.addedByReferentId = addedByReferentId;
    }

    // --- GETTERS AND SETTERS ---

    /** * Dohvata broj indeksa studenta povezanog sa ovim upisom.
     * @return String koji predstavlja jedinstveni broj indeksa.
     */
    public String getStudentIndexNumber() { return studentIndexNumber; }

    /** * Postavlja broj indeksa studenta za ovaj zapis upisa.
     * @param studentIndexNumber Broj indeksa (npr. "123/2020").
     */
    public void setStudentIndexNumber(String studentIndexNumber) { this.studentIndexNumber = studentIndexNumber; }

    /** * Dohvata šifru kursa na koji je student upisan.
     * @return String koji predstavlja identifikator predmeta.
     */
    public String getCourseCode() { return courseCode; }

    /** * Postavlja šifru kursa za ovaj upis.
     * @param courseCode Šifra predmeta (npr. "MAT101").
     */
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    /** * Vraća akademsku godinu u kojoj je upis izvršen.
     * @return Akademska godina u formatu "YYYY/YYYY".
     */
    public String getAcademicYear() { return academicYear; }

    /** * Definiše akademsku godinu upisa.
     * @param academicYear Akademska godina (npr. "2023/2024").
     */
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    /** * Dohvata ostvarenu ocjenu studenta na predmetu.
     * @return Integer vrijednost ocjene (5-10) ili null ako nije ocijenjen.
     */
    public Integer getGrade() { return grade; }

    /** * Postavlja ocjenu za ovaj upis nakon položenog ispita.
     * @param grade Numerička vrijednost ocjene.
     */
    public void setGrade(Integer grade) { this.grade = grade; }

    /** * Vraća datum kada je ocjena zvanično unesena u sistem.
     * @return Datum ocjenjivanja kao String.
     */
    public String getGradeDate() { return gradeDate; }

    /** * Postavlja datum kada je student položio ispit/dobio ocjenu.
     * @param gradeDate Datum unosa ocjene.
     */
    public void setGradeDate(String gradeDate) { this.gradeDate = gradeDate; }

    /** * Vraća obrazloženje zašto je zapis o upisu ili ocjeni bio modifikovan.
     * @return Tekstualni opis razloga izmjene.
     */
    public String getChangeReason() { return changeReason; }

    /** * Postavlja razlog izmjene podataka (audit trail).
     * @param changeReason Detaljan opis razloga za promjenu zapisa.
     */
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    /** * Vraća datum posljednje izvršene modifikacije na ovom upisu.
     * @return Datum izmjene kao String.
     */
    public String getChangeDate() { return changeDate; }

    /** * Postavlja datum kada je izvršena izmjena nad podacima.
     * @param changeDate Vrijeme i datum modifikacije.
     */
    public void setChangeDate(String changeDate) { this.changeDate = changeDate; }

    /** * Dohvata ID referenta koji je inicijalno kreirao ovaj upis.
     * @return Identifikator referenta kreatora.
     */
    public String getAddedByReferentId() { return addedByReferentId; }

    /** * Postavlja ID referenta koji dodaje studenta na predmet.
     * @param addedByReferentId Korisničko ime ili ID referenta.
     */
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }

    /** * Vraća ID referenta koji je posljednji izvršio promjene na ocjeni ili statusu.
     * @return Identifikator referenta modifikatora.
     */
    public String getModifiedByReferentId() { return modifiedByReferentId; }

    /** * Postavlja ID referenta koji vrši korekciju podataka o upisu.
     * @param modifiedByReferentId Korisničko ime ili ID referenta.
     */
    public void setModifiedByReferentId(String modifiedByReferentId) { this.modifiedByReferentId = modifiedByReferentId; }

    /**
     * Poredi dva Enrollment objekta na osnovu ključnih parametara.
     * Upisi se smatraju istim ako se podudaraju indeks studenta, šifra kursa i akademska godina.
     *
     * @param o Objekat za poređenje.
     * @return true ako su ključna polja identična, inače false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(studentIndexNumber, that.studentIndexNumber) &&
                Objects.equals(courseCode, that.courseCode) &&
                Objects.equals(academicYear, that.academicYear);
    }

    /**
     * Generiše hash vrijednost na osnovu indeksa, šifre kursa i akademske godine.
     * Koristi se za pravilno funkcionisanje unutar hash mapa i setova.
     * @return int hash kod generisan iz ključnih polja.
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentIndexNumber, courseCode, academicYear);
    }

    /**
     * Vraća formatiranu tekstualnu reprezentaciju objekta Enrollment.
     * Ukoliko ocjena nije veća od 5, prikazuje se status "Nije ocijenjen".
     *
     * @return Deskriptivni string sa informacijama o upisu pogodnim za ispis.
     */
    @Override
    public String toString() {
        return "Indeks: " + studentIndexNumber +
                " | Predmet: " + courseCode +
                " | Akademska godina: " + academicYear +
                " | Ocjena: " + (grade != null && grade > 5 ? grade : "Nije ocijenjen") +
                " | Referent: " + addedByReferentId;
    }
}