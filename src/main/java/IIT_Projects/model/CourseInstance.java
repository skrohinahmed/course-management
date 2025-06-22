package IIT_Projects.model;

public class CourseInstance {
    private Long id;
    private String courseId;
    private int year;
    private int semester;
    public CourseInstance(Long id,String courseId,int year,int semester)
    {
    	this.id=id;
    	this.courseId=courseId;
    	this.semester=semester;
    	this.year=year;
    }
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    // Getters, setters, constructors
}

