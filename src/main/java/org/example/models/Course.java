package org.example.models;

import java.util.Objects;

public class Course {

    private String courseCode;
    private String name;
    private int ects;
    private int semester;


    public Course(String courseCode, String name, int ects, int semester) {
        this.courseCode = courseCode;
        this.name = name;
        this.ects = ects;
        this.semester = semester;
    }

    // konstruktor bez sifre predmeta
    public Course(String name, int ects, int semester) {
        this.name = name;
        this.ects = ects;
        this.semester = semester;
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

    @Override
    public String toString() {
        return "Predmet{" +
                "sifraPredmeta='" + courseCode + '\'' +
                ", Naziv='" + name + '\'' +
                ", ects=" + ects +
                ", semestar=" + semester +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;

        return ects == course.ects && semester == course.semester && Objects.equals(courseCode, course.courseCode) && Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, name, ects, semester);
    }
}