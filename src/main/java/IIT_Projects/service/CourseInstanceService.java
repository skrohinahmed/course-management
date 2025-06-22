// CourseInstanceService.java
package IIT_Projects.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import IIT_Projects.model.CourseInstance;
import IIT_Projects.repository.CourseInstanceRepository;
import IIT_Projects.repository.CourseRepository;

@Service
public class CourseInstanceService {

    @Autowired private CourseInstanceRepository instanceRepo;
    @Autowired private CourseRepository courseRepo;

    public void createInstance(CourseInstance instance) {
        if (!courseRepo.exists(instance.getCourseId())) {
            throw new IllegalArgumentException("Course ID does not exist: " + instance.getCourseId());
        }
        instanceRepo.save(instance);
    }

    public List<CourseInstance> getInstancesByYearAndSemester(int year, int semester) {
        return instanceRepo.findByYearAndSemester(year, semester);
    }

    public CourseInstance getInstance(int year, int semester, String courseId) {
        return instanceRepo.findOne(year, semester, courseId);
    }

    public void deleteInstance(int year, int semester, String courseId) {
        instanceRepo.delete(year, semester, courseId);
    }
}
