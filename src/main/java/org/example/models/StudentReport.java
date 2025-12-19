package org.example.models;

import java.util.List;

public class StudentReport {
    private Student student;
    private List<Enrollment> enrollments;
    private int totalEcts;
    private double averageGrade;

    public StudentReport(Student student, List<Enrollment> enrollments, int totalEcts, double averageGrade) {
        this.student = student;
        this.enrollments = enrollments;
        this.totalEcts = totalEcts;
        this.averageGrade = averageGrade;
    }

    // Getteri
    public Student getStudent() { return student; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public int getTotalEcts() { return totalEcts; }
    public double getAverageGrade() { return averageGrade; }

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
