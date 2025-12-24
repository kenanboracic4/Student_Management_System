package StudentManagmentSystem.models;

import java.util.Objects;

public class Course {

    private String courseCode;
    private String name;
    private int ects;
    private int semester;

    // Nove kolone koje prate bazu
    private String createdAt;
    private String updatedAt;
    private String addedByReferentId; // Šifra referenta

    // Konstruktor za puni prikaz (iz baze)
    public Course(String courseCode, String name, int ects, int semester, String createdAt, String updatedAt, String addedByReferentId) {
        this.courseCode = courseCode;
        this.name = name;
        this.ects = ects;
        this.semester = semester;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.addedByReferentId = addedByReferentId;
    }

    // Konstruktor za kreiranje novog predmeta (prije slanja u bazu)
    public Course(String courseCode, String name, int ects, int semester, String addedByReferentId) {
        this.courseCode = courseCode;
        this.name = name;
        this.ects = ects;
        this.semester = semester;
        this.addedByReferentId = addedByReferentId;
    }

    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getEcts() { return ects; }
    public void setEcts(int ects) { this.ects = ects; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getAddedByReferentId() { return addedByReferentId; }
    public void setAddedByReferentId(String addedByReferentId) { this.addedByReferentId = addedByReferentId; }


    @Override
    public String toString() {
        return "Šifra: " + courseCode +
                ", Naziv: " + name +
                ", ECTS: " + ects +
                ", Semestar: " + semester +
                ", Dodao: " + (addedByReferentId != null ? addedByReferentId : "Sistem");
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, name, ects, semester);
    }
}