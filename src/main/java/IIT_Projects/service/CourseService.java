// CourseService.java
package IIT_Projects.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import IIT_Projects.model.Course;
import IIT_Projects.repository.CourseRepository;
import IIT_Projects.repository.CourseInstanceRepository;

@Service
public class CourseService {
    @Autowired private CourseRepository repo;
    @Autowired private CourseInstanceRepository instanceRepo;

    public void createCourse(Course course) {
        for (String prereq : course.getPrerequisites()) {
            if (!repo.exists(prereq)) {
                throw new IllegalArgumentException("Prerequisite course does not exist: " + prereq);
            }
        }
        repo.save(course);
    }

    public List<Course> getAllCourses() {
        return repo.findAll();
    }

    public Course getCourse(String id) {
        return repo.findById(id);
    }

    public void deleteCourse(String id) {
        if (repo.isPrerequisite(id)) {
            throw new IllegalStateException("Course is a prerequisite for other courses and cannot be deleted.");
        }
        if (!instanceRepo.findByCourseId(id).isEmpty()) {
            throw new IllegalStateException("Course has delivery instances and cannot be deleted.");
        }
        repo.deleteById(id);
    }
}
