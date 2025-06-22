package IIT_Projects.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import IIT_Projects.model.Course;

@Repository
public class CourseRepository {
    @Autowired
    private JdbcTemplate jdbc;

    public void save(Course course) {
        if (course.getPrerequisites() != null) {
            for (String prereq : course.getPrerequisites()) {
                if (!exists(prereq)) {
                    throw new IllegalArgumentException("Prerequisite course ID does not exist: " + prereq);
                }
            }
        }

        jdbc.update("INSERT INTO courses (id, title, description) VALUES (?, ?, ?)",
                course.getId(), course.getTitle(), course.getDescription());

        if (course.getPrerequisites() != null) {
            for (String prereq : course.getPrerequisites()) {
                jdbc.update("INSERT INTO course_prerequisites (course_id, prereq_id) VALUES (?, ?)",
                        course.getId(), prereq);
            }
        }
    }

    public boolean exists(String id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM courses WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public Course findById(String id) {
        Course course = jdbc.queryForObject(
                "SELECT * FROM courses WHERE id = ?",
                (rs, rowNum) -> new Course(rs.getString("id"), rs.getString("title"), rs.getString("description"), new ArrayList<>()),
                id);

        List<String> prereqs = jdbc.query(
                "SELECT prereq_id FROM course_prerequisites WHERE course_id = ?",
                (rs, rowNum) -> rs.getString("prereq_id"), id);

        course.setPrerequisites(prereqs);
        return course;
    }

    public List<Course> findAll() {
        List<Course> courses = jdbc.query(
                "SELECT * FROM courses",
                (rs, rowNum) -> new Course(rs.getString("id"), rs.getString("title"), rs.getString("description"), new ArrayList<>())
        );

        for (Course course : courses){
            course.setPrerequisites(jdbc.query(
                    "SELECT prereq_id FROM course_prerequisites WHERE course_id = ?",
                    (rs, rowNum) -> rs.getString("prereq_id"), course.getId()));
        }

        return courses;
    }

    public boolean isPrerequisite(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM course_prerequisites WHERE prereq_id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    public void deleteById(String id) {
        jdbc.update("DELETE FROM course_prerequisites WHERE course_id = ?", id);
        jdbc.update("DELETE FROM courses WHERE id = ?", id);
    }
}
