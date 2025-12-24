package StudentManagmentSystem.models;

import java.util.Objects;

public class Student {

    private String indexNumber;
    private String password; // Nova kolona: sifra
    private String firstName;
    private String lastName;
    private String studyProgram;
    private int enrollmentYear;

    // Metapodaci (Audit fields)
    private String createdAt;
    private String updatedAt;
    private String addedByReferentId;

    // Konstruktor za puni prikaz (čitanje iz baze)
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

    // Konstruktor za kreiranje novog studenta (prije unosa u bazu)
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

    // Getters and Setters
    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudyProgram() { return studyProgram; }
    public void setStudyProgram(String studyProgram) { this.studyProgram = studyProgram; }

    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getAddedByReferentId() { return addedByReferentId; }
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }

    @Override
    public String toString() {
        return "Student: [" + indexNumber + "] " + firstName + " " + lastName +
                " | Program: " + studyProgram +
                " | Dodao referent: " + addedByReferentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(indexNumber, student.indexNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexNumber);
    }
}