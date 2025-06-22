package IIT_Projects.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import IIT_Projects.model.CourseInstance;

@Repository
public class CourseInstanceRepository {
    @Autowired
    private JdbcTemplate jdbc;

    public void save(CourseInstance instance) {
        jdbc.update("INSERT INTO course_instances (course_id, year, semester) VALUES (?, ?, ?)",
                instance.getCourseId(), instance.getYear(), instance.getSemester());
    }

    public List<CourseInstance> findByYearAndSemester(int year, int semester) {
        return jdbc.query("SELECT * FROM course_instances WHERE year = ? AND semester = ?",
                (rs, rowNum) -> new CourseInstance(rs.getLong("id"), rs.getString("course_id"), rs.getInt("year"), rs.getInt("semester")),
                year, semester);
    }

    public List<CourseInstance> findByCourseId(String courseId) {
        return jdbc.query("SELECT * FROM course_instances WHERE course_id = ?",
                (rs, rowNum) -> new CourseInstance(rs.getLong("id"), rs.getString("course_id"), rs.getInt("year"), rs.getInt("semester")),
                courseId);
    }

    public CourseInstance findOne(int year, int semester, String courseId) {
        try {
            return jdbc.queryForObject(
                    "SELECT * FROM course_instances WHERE year = ? AND semester = ? AND course_id = ?",
                    (rs, rowNum) -> new CourseInstance(rs.getLong("id"), rs.getString("course_id"), rs.getInt("year"), rs.getInt("semester")),
                    year, semester, courseId);
        } catch (Exception e) {
            return null;
        }
    }

    public void delete(int year, int semester, String courseId) {
        jdbc.update("DELETE FROM course_instances WHERE year = ? AND semester = ? AND course_id = ?",
                year, semester, courseId);
    }

    public void deleteByCourseId(String courseId) {
        jdbc.update("DELETE FROM course_instances WHERE course_id = ?", courseId);
    }
}
