package org.example.repository.interfaces;

import org.example.models.Course;

import java.util.ArrayList;
import java.util.Optional;

public interface CourseInterface {

    void addCourse(Course course);

    boolean updateCourse(Course course, String courseCode);

    boolean deleteCourse(String courseCode);

    ArrayList<Course> getAllCourses();

    Optional<Course> getCourseByCode(String courseCode);

    ArrayList<Course> findCoursesByNamePrefix(String prefix);
}
