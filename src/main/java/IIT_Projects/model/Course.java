package IIT_Projects.model;

import java.util.List;

public class Course {
    private String id;
    private String title;
    private String description;
    private List<String> prerequisites;
    public Course(String id,String title,String description,List<String> prerequisites) {
		this.description=description;
		this.id=id;
		this.title=title;
		this.prerequisites=prerequisites;
	}
	public List<String> getPrerequisites() {
		return prerequisites;
	}
	public void setPrerequisites(List<String> prerequisites) {
		this.prerequisites = prerequisites;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

    // Getters, setters, constructors
    
}

