package StudentManagmentSystem.models;

import java.util.List;

/**
 * Predstavlja izvještaj (karton) studenta koji sumira akademski uspjeh.
 * Ova klasa se koristi za generisanje pregleda svih ocjena, ukupnog broja
 * ECTS bodova i izračunavanje prosjeka. Služi kao "Data Transfer Object" (DTO)
 * za prikaz podataka na korisničkom interfejsu ili u izvještajima.
 * * @author Kenan Boračić
 * @version 1.0
 */
public class StudentReport {

    /** Objekat studenta koji sadrži lične podatke i broj indeksa */
    private Student student;

    /** Lista svih upisa (prijavljenih i položenih predmeta) povezanih sa studentom */
    private List<Enrollment> enrollments;

    /** Suma ECTS bodova ostvarenih na svim položenim predmetima */
    private int totalEcts;

    /** Izračunata aritmetička sredina svih prolaznih ocjena (6-10) */
    private double averageGrade;

    /**
     * Konstruktor za kreiranje izvještaja o studentu.
     *
     * @param student Objekat studenta na kojeg se izvještaj odnosi.
     * @param enrollments Lista svih upisa (prijavljenih i položenih predmeta).
     * @param totalEcts Suma ECTS bodova svih položenih predmeta.
     * @param averageGrade Izračunata prosječna ocjena položenih ispita.
     */
    public StudentReport(Student student, List<Enrollment> enrollments, int totalEcts, double averageGrade) {
        this.student = student;
        this.enrollments = enrollments;
        this.totalEcts = totalEcts;
        this.averageGrade = averageGrade;
    }

    // --- GETTERI ---

    /** * Dohvata osnovne podatke o studentu.
     * @return Objekat tipa {@link Student}.
     */
    public Student getStudent() { return student; }

    /** * Dohvata listu svih upisa studenta na predmete.
     * @return {@link List} objekata tipa {@link Enrollment}.
     */
    public List<Enrollment> getEnrollments() { return enrollments; }

    /** * Dohvata ukupno ostvarene ECTS bodove kroz položene ispite.
     * @return Suma ECTS bodova kao cijeli broj.
     */
    public int getTotalEcts() { return totalEcts; }

    /** * Dohvata prosječnu ocjenu studenta.
     * @return Prosjek kao double vrijednost.
     */
    public double getAverageGrade() { return averageGrade; }

    /**
     * Generiše detaljan tekstualni prikaz kartona studenta.
     * Prikazuje zaglavlje sa ličnim podacima, tabelarni pregled predmeta sa pripadajućim
     * ocjenama i datumima polaganja, te finalni sumarni prikaz bodova i prosjeka.
     *
     * @return Formatiran String spreman za ispis na konzolu ili u tekstualni dokument.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------- KARTON STUDENTA ----------\n");
        sb.append("Student: ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
        sb.append("Indeks: ").append(student.getIndexNumber()).append("\n");
        sb.append("-------------------------------------\n");
        for (Enrollment e : enrollments) {
            sb.append(String.format("Predmet: %-10s | Ocjena: %-2s | Datum: %s\n",
                    e.getCourseCode(),
                    (e.getGrade() == null ? "N/A" : e.getGrade()),
                    (e.getGradeDate() == null ? "-" : e.getGradeDate())));
        }
        sb.append("-------------------------------------\n");
        sb.append("Ukupno položenih ECTS: ").append(totalEcts).append("\n");
        sb.append("Prosječna ocjena: ").append(String.format("%.2f", averageGrade)).append("\n");
        return sb.toString();
    }
}