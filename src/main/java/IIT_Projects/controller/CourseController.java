// CourseController.java
package IIT_Projects.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import IIT_Projects.model.Course;
import IIT_Projects.model.CourseInstance;
import IIT_Projects.service.CourseService;
import IIT_Projects.service.CourseInstanceService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CourseController {

    @Autowired private CourseService courseService;
    @Autowired private CourseInstanceService instanceService;

    // Create a course
    @PostMapping("/courses")
    public ResponseEntity<?> create(@RequestBody Course course) {
        try {
            courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    // Get all courses
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Get course by ID
    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        try {
            Course course = courseService.getCourse(id);
            return ResponseEntity.ok(course);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with ID: " + id);
        }
    }

    // Delete a course
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok("Course deleted successfully.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: " + ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    // Create course instance
    @PostMapping("/instances")
    public ResponseEntity<?> createInstance(@RequestBody CourseInstance instance) {
        try {
            instanceService.createInstance(instance);
            return ResponseEntity.status(HttpStatus.CREATED).body("Instance created successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    // Get instances by year and semester
    @GetMapping("/instances/{year}/{semester}")
    public ResponseEntity<List<CourseInstance>> getInstancesByYearSemester(@PathVariable int year, @PathVariable int semester) {
        return ResponseEntity.ok(instanceService.getInstancesByYearAndSemester(year, semester));
    }

    // Get specific course instance
    @GetMapping("/instances/{year}/{semester}/{courseId}")
    public ResponseEntity<?> getInstance(@PathVariable int year, @PathVariable int semester, @PathVariable String courseId) {
        CourseInstance instance = instanceService.getInstance(year, semester, courseId);
        if (instance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found.");
        }
        return ResponseEntity.ok(instance);
    }

    // Delete course instance
    @DeleteMapping("/instances/{year}/{semester}/{courseId}")
    public ResponseEntity<?> deleteInstance(@PathVariable int year, @PathVariable int semester, @PathVariable String courseId) {
        try {
            instanceService.deleteInstance(year, semester, courseId);
            return ResponseEntity.ok("Instance deleted successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }
}