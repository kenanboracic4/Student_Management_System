package org.example.models;

import java.util.Objects;

public class Course {

    private int id;
    private String name;
    private int ects;

    public Course(int id, String name, int ects) {
        this.id = id;
        this.name = name;
        this.ects = ects;
    }

    public Course(String name, int ects) {
        this.name = name;
        this.ects = ects;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getEcts() { return ects; }
    public void setEcts(int ects) { this.ects = ects; }

    @Override
    public String toString() {
        return "Predmet{" +
                "id=" + id +
                ", Naziv: ='" + name + '\'' +
                ", ects=" + ects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && ects == course.ects && Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ects);
    }
}