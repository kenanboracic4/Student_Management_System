package org.example.models;

import java.util.Objects;

public class Student {


    private String firstName;
    private String lastName;
    private String indexNumber;
    private String studyProgram;
    private int enrollmentYear;

    public Student(String indexNumber,String firstName, String lastName, String studyProgram, int enrollmentYear) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
        this.studyProgram = studyProgram;
        this.enrollmentYear = enrollmentYear;
    }

    public Student(String firstName, String lastName, String indexNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
    }

    // Getters and Setters



    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIndexNumber() { return indexNumber; }
    public void setIndexNumber(String indexNumber) { this.indexNumber = indexNumber; }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }



    @Override
    public String toString() {
        return "Student: " +
                " Ime = " + firstName + ' ' +
                ", Prezime = " + lastName + ' ' +
                ", Broj Indeksa = " + indexNumber + ' ' +
                ", StudijskiProgram = " + studyProgram +
                ", Godina Upisa = " + enrollmentYear;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(indexNumber, student.indexNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(indexNumber);
    }
}