package org.example.repository.interfaces;

import org.example.models.Enrollment;

import java.util.List;
import java.util.Optional;


public interface EnrollmentInterface {


    Enrollment create(Enrollment enrollment);


    Optional<Enrollment> findByPrimaryKey(String studentIndexNumber, String courseCode, String academicYear);


    List<Enrollment> findByStudentAndYear(String studentIndexNumber, String academicYear);

    boolean update(Enrollment enrollment);


    boolean delete(String studentIndexNumber, String courseCode, String academicYear);


    List<Enrollment> findAll();
}