package StudentManagmentSystem.models;

import java.util.Objects;

public class Enrollment {

    private String studentIndexNumber;
    private String courseCode;
    private String academicYear;
    private Integer grade;
    private String gradeDate;
    private String changeReason;
    private String changeDate;

    // Nove kolone za referenta
    private String addedByReferentId;
    private String modifiedByReferentId;

    // Konstruktor za puni prikaz (čitanje iz baze)
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

    // Konstruktor za novi upis (kada student tek prijavljuje predmet)
    public Enrollment(String studentIndexNumber, String courseCode, String academicYear, String addedByReferentId) {
        this.studentIndexNumber = studentIndexNumber;
        this.courseCode = courseCode;
        this.academicYear = academicYear;
        this.addedByReferentId = addedByReferentId;
    }

    // Getteri i Setteri

    public String getStudentIndexNumber() { return studentIndexNumber; }
    public void setStudentIndexNumber(String studentIndexNumber) { this.studentIndexNumber = studentIndexNumber; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getGradeDate() { return gradeDate; }
    public void setGradeDate(String gradeDate) { this.gradeDate = gradeDate; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public String getChangeDate() { return changeDate; }
    public void setChangeDate(String changeDate) { this.changeDate = changeDate; }

    public String getAddedByReferentId() { return addedByReferentId; }
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }

    public String getModifiedByReferentId() { return modifiedByReferentId; }
    public void setModifiedByReferentId(String modifiedByReferentId) { this.modifiedByReferentId = modifiedByReferentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        // Primarni ključ je kombinacija ova tri polja
        return Objects.equals(studentIndexNumber, that.studentIndexNumber) &&
                Objects.equals(courseCode, that.courseCode) &&
                Objects.equals(academicYear, that.academicYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentIndexNumber, courseCode, academicYear);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "Indeks='" + studentIndexNumber + '\'' +
                ", Predmet='" + courseCode + '\'' +
                ", Akademska Godina='" + academicYear + '\'' +
                ", Ocjena=" + grade +
                ", Dodao Referent='" + addedByReferentId + '\'' +
                ", Izmijenio Referent='" + modifiedByReferentId + '\'' +
                '}';
    }
}