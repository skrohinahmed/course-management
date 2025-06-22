import React, { useState, useEffect } from 'react';

const CourseManagementSystem = () => {
  const [activeSection, setActiveSection] = useState('courses');
  const [courses, setCourses] = useState([]);
  const [instances, setInstances] = useState([]);
  const [courseAlert, setCourseAlert] = useState({ message: '', type: '' });
  const [instanceAlert, setInstanceAlert] = useState({ message: '', type: '' });
  const [loading, setLoading] = useState({ courses: false, instances: false });
  
  // Course form state
  const [courseForm, setCourseForm] = useState({
    id: '',
    title: '',
    description: '',
    prerequisites: ['']
  });
  
  // Instance form state
  const [instanceForm, setInstanceForm] = useState({
    courseId: '',
    year: '',
    semester: ''
  });
  
  // Filter state
  const [filters, setFilters] = useState({
    year: '',
    semester: ''
  });

  const API_BASE_URL = 'http://localhost:8080/api';

  // Alert function
  const showAlert = (setter, message, type) => {
    setter({ message, type });
    setTimeout(() => setter({ message: '', type: '' }), 5000);
  };

  // Load courses for dropdown
  const loadCoursesForDropdown = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/courses`);
      const coursesData = await response.json();
      setCourses(coursesData);
    } catch (error) {
      console.error('Error loading courses:', error);
    }
  };

  // Load all courses
  const loadAllCourses = async () => {
    setLoading(prev => ({ ...prev, courses: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/courses`);
      const coursesData = await response.json();
      setCourses(coursesData);
    } catch (error) {
      console.error('Error loading courses:', error);
    } finally {
      setLoading(prev => ({ ...prev, courses: false }));
    }
  };

  // Load filtered instances
  const loadFilteredInstances = async () => {
    const { year, semester } = filters;
    if (!year || !semester) {
      alert('Please select both year and semester to filter instances.');
      return;
    }
    
    setLoading(prev => ({ ...prev, instances: true }));
    try {
      const response = await fetch(`${API_BASE_URL}/instances/${year}/${semester}`);
      const instancesData = await response.json();
      setInstances(instancesData);
    } catch (error) {
      console.error('Error loading instances:', error);
    } finally {
      setLoading(prev => ({ ...prev, instances: false }));
    }
  };

  // Course form handlers
  const handleCourseFormChange = (field, value) => {
    setCourseForm(prev => ({ ...prev, [field]: value }));
  };

  const handlePrerequisiteChange = (index, value) => {
    setCourseForm(prev => ({
      ...prev,
      prerequisites: prev.prerequisites.map((prereq, i) => i === index ? value : prereq)
    }));
  };

  const addPrerequisite = () => {
    setCourseForm(prev => ({
      ...prev,
      prerequisites: [...prev.prerequisites, '']
    }));
  };

  const removePrerequisite = (index) => {
    if (courseForm.prerequisites.length > 1) {
      setCourseForm(prev => ({
        ...prev,
        prerequisites: prev.prerequisites.filter((_, i) => i !== index)
      }));
    }
  };

  const resetCourseForm = () => {
    setCourseForm({
      id: '',
      title: '',
      description: '',
      prerequisites: ['']
    });
  };

  const handleCourseSubmit = async (e) => {
    e.preventDefault();
    
    const prerequisites = courseForm.prerequisites
      .map(prereq => prereq.trim())
      .filter(prereq => prereq !== '');
    
    const courseData = {
      id: courseForm.id,
      title: courseForm.title,
      description: courseForm.description,
      prerequisites
    };
    
    try {
      const response = await fetch(`${API_BASE_URL}/courses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(courseData)
      });
      
      const result = await response.text();
      
      if (response.ok) {
        showAlert(setCourseAlert, 'Course created successfully!', 'success');
        resetCourseForm();
        if (activeSection === 'view-courses') {
          loadAllCourses();
        }
      } else {
        showAlert(setCourseAlert, result, 'error');
      }
    } catch (error) {
      showAlert(setCourseAlert, 'Error creating course: ' + error.message, 'error');
    }
  };

  // Instance form handlers
  const handleInstanceFormChange = (field, value) => {
    setInstanceForm(prev => ({ ...prev, [field]: value }));
  };

  const resetInstanceForm = () => {
    setInstanceForm({ courseId: '', year: '', semester: '' });
  };

  const handleInstanceSubmit = async (e) => {
    e.preventDefault();
    
    const instanceData = {
      courseId: instanceForm.courseId,
      year: parseInt(instanceForm.year),
      semester: parseInt(instanceForm.semester)
    };
    
    try {
      const response = await fetch(`${API_BASE_URL}/instances`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(instanceData)
      });
      
      const result = await response.text();
      
      if (response.ok) {
        showAlert(setInstanceAlert, 'Course instance created successfully!', 'success');
        resetInstanceForm();
      } else {
        showAlert(setInstanceAlert, result, 'error');
      }
    } catch (error) {
      showAlert(setInstanceAlert, 'Error creating instance: ' + error.message, 'error');
    }
  };

  // Delete functions
  const deleteCourse = async (courseId) => {
    if (!window.confirm(`Are you sure you want to delete course ${courseId}?`)) {
      return;
    }
    
    try {
      const response = await fetch(`${API_BASE_URL}/courses/${courseId}`, {
        method: 'DELETE'
      });
      
      const result = await response.text();
      
      if (response.ok) {
        alert('Course deleted successfully!');
        loadAllCourses();
      } else {
        alert('Error deleting course: ' + result);
      }
    } catch (error) {
      alert('Error deleting course: ' + error.message);
    }
  };

  const deleteInstance = async (year, semester, courseId) => {
    if (!window.confirm('Are you sure you want to delete this course instance?')) {
      return;
    }
    
    try {
      const response = await fetch(`${API_BASE_URL}/instances/${year}/${semester}/${courseId}`, {
        method: 'DELETE'
      });
      
      const result = await response.text();
      
      if (response.ok) {
        alert('Course instance deleted successfully!');
        loadFilteredInstances();
      } else {
        alert('Error deleting instance: ' + result);
      }
    } catch (error) {
      alert('Error deleting instance: ' + error.message);
    }
  };

  // Navigation handler
  const showSection = (sectionId) => {
    setActiveSection(sectionId);
    
    if (sectionId === 'instances') {
      loadCoursesForDropdown();
    } else if (sectionId === 'view-courses') {
      loadAllCourses();
    }
  };

  // Initialize
  useEffect(() => {
    loadCoursesForDropdown();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-500 to-purple-600">
      <div className="max-w-6xl mx-auto p-5">
        {/* Header */}
        <div className="text-center mb-8 text-white">
          <h1 className="text-4xl font-bold mb-3 drop-shadow-lg">Course Management System</h1>
          <p className="text-lg opacity-90">Manage courses and their delivery instances</p>
        </div>

        {/* Navigation */}
        <div className="flex flex-wrap justify-center mb-8 bg-white bg-opacity-10 backdrop-blur-lg rounded-2xl p-3 gap-2">
          {[
            { id: 'courses', label: 'Courses' },
            { id: 'instances', label: 'Course Instances' },
            { id: 'view-courses', label: 'View All Courses' },
            { id: 'view-instances', label: 'View All Instances' }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => showSection(tab.id)}
              className={`px-4 py-3 rounded-full font-medium transition-all duration-300 text-sm ${
                activeSection === tab.id
                  ? 'bg-white bg-opacity-20 border-2 border-white border-opacity-50 transform -translate-y-1 shadow-lg text-white'
                  : 'bg-transparent border-2 border-white border-opacity-30 text-white hover:bg-white hover:bg-opacity-10 hover:-translate-y-0.5'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Course Management Section */}
        {activeSection === 'courses' && (
          <div className="bg-white bg-opacity-95 backdrop-blur-lg rounded-3xl p-8 shadow-2xl">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">Course Management</h2>
            
            <div className="bg-gray-50 p-6 rounded-2xl mb-6 border border-gray-200">
              <h3 className="text-xl font-semibold mb-4 text-gray-700">Add New Course</h3>
              
              <div>
                <div className="flex gap-4 mb-4 flex-wrap">
                  <div className="flex-1 min-w-48">
                    <label className="block mb-2 font-semibold text-gray-600">Course ID</label>
                    <input
                      type="text"
                      value={courseForm.id}
                      onChange={(e) => handleCourseFormChange('id', e.target.value)}
                      required
                      placeholder="e.g., CS101"
                      className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                    />
                  </div>
                  <div className="flex-1 min-w-48">
                    <label className="block mb-2 font-semibold text-gray-600">Course Title</label>
                    <input
                      type="text"
                      value={courseForm.title}
                      onChange={(e) => handleCourseFormChange('title', e.target.value)}
                      required
                      placeholder="e.g., Introduction to Programming"
                      className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                    />
                  </div>
                </div>
                
                <div className="mb-4">
                  <label className="block mb-2 font-semibold text-gray-600">Course Description</label>
                  <textarea
                    value={courseForm.description}
                    onChange={(e) => handleCourseFormChange('description', e.target.value)}
                    required
                    placeholder="Enter course description..."
                    className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all min-h-20 resize-y"
                  />
                </div>
                
                <div className="mb-6">
                  <label className="block mb-2 font-semibold text-gray-600">Prerequisites</label>
                  {courseForm.prerequisites.map((prereq, index) => (
                    <div key={index} className="flex items-center mb-3 bg-white p-3 rounded-lg border border-gray-200">
                      <input
                        type="text"
                        value={prereq}
                        onChange={(e) => handlePrerequisiteChange(index, e.target.value)}
                        placeholder="Enter prerequisite course ID (optional)"
                        className="flex-1 mr-3 border-none bg-transparent focus:outline-none"
                      />
                      <button
                        type="button"
                        onClick={() => removePrerequisite(index)}
                        className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium"
                      >
                        Remove
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    onClick={addPrerequisite}
                    className="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-medium"
                  >
                    Add Prerequisite
                  </button>
                </div>
                
                <div className="flex gap-3">
                  <button
                    type="button"
                    onClick={handleCourseSubmit}
                    className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:-translate-y-1 hover:shadow-xl transition-all font-semibold"
                  >
                    Create Course
                  </button>
                  <button
                    type="button"
                    onClick={resetCourseForm}
                    className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-semibold"
                  >
                    Reset
                  </button>
                </div>
              </div>
            </div>
            
            {courseAlert.message && (
              <div className={`p-4 rounded-lg font-medium mb-4 ${
                courseAlert.type === 'success' 
                  ? 'bg-green-100 text-green-800 border border-green-200' 
                  : 'bg-red-100 text-red-800 border border-red-200'
              }`}>
                {courseAlert.message}
              </div>
            )}
          </div>
        )}

        {/* Course Instance Management Section */}
        {activeSection === 'instances' && (
          <div className="bg-white bg-opacity-95 backdrop-blur-lg rounded-3xl p-8 shadow-2xl">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">Course Instance Management</h2>
            
            <div className="bg-gray-50 p-6 rounded-2xl mb-6 border border-gray-200">
              <h3 className="text-xl font-semibold mb-4 text-gray-700">Add New Course Instance</h3>
              
              <div>
                <div className="flex gap-4 mb-4 flex-wrap">
                  <div className="flex-1 min-w-48">
                    <label className="block mb-2 font-semibold text-gray-600">Course ID</label>
                    <select
                      value={instanceForm.courseId}
                      onChange={(e) => handleInstanceFormChange('courseId', e.target.value)}
                      required
                      className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                    >
                      <option value="">Select a course...</option>
                      {courses.map(course => (
                        <option key={course.id} value={course.id}>
                          {course.id} - {course.title}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="flex-1 min-w-48">
                    <label className="block mb-2 font-semibold text-gray-600">Year</label>
                    <input
                      type="number"
                      value={instanceForm.year}
                      onChange={(e) => handleInstanceFormChange('year', e.target.value)}
                      required
                      min="2020"
                      max="2030"
                      placeholder="e.g., 2024"
                      className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                    />
                  </div>
                  <div className="flex-1 min-w-48">
                    <label className="block mb-2 font-semibold text-gray-600">Semester</label>
                    <select
                      value={instanceForm.semester}
                      onChange={(e) => handleInstanceFormChange('semester', e.target.value)}
                      required
                      className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                    >
                      <option value="">Select semester...</option>
                      <option value="1">Semester 1</option>
                      <option value="2">Semester 2</option>
                    </select>
                  </div>
                </div>
                
                <div className="flex gap-3">
                  <button
                    type="button"
                    onClick={handleInstanceSubmit}
                    className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:-translate-y-1 hover:shadow-xl transition-all font-semibold"
                  >
                    Create Instance
                  </button>
                  <button
                    type="button"
                    onClick={resetInstanceForm}
                    className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-semibold"
                  >
                    Reset
                  </button>
                </div>
              </div>
            </div>
            
            {instanceAlert.message && (
              <div className={`p-4 rounded-lg font-medium mb-4 ${
                instanceAlert.type === 'success' 
                  ? 'bg-green-100 text-green-800 border border-green-200' 
                  : 'bg-red-100 text-red-800 border border-red-200'
              }`}>
                {instanceAlert.message}
              </div>
            )}
          </div>
        )}

        {/* View All Courses Section */}
        {activeSection === 'view-courses' && (
          <div className="bg-white bg-opacity-95 backdrop-blur-lg rounded-3xl p-8 shadow-2xl">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">All Courses</h2>
            
            <button
              onClick={loadAllCourses}
              className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:-translate-y-1 hover:shadow-xl transition-all font-semibold mb-6"
            >
              Refresh Courses
            </button>
            
            {loading.courses ? (
              <div className="text-center py-8 text-gray-600">Loading courses...</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full bg-white rounded-xl overflow-hidden shadow-lg">
                  <thead>
                    <tr className="bg-gradient-to-r from-indigo-500 to-purple-600 text-white">
                      <th className="p-4 text-left font-semibold">Course ID</th>
                      <th className="p-4 text-left font-semibold">Title</th>
                      <th className="p-4 text-left font-semibold">Description</th>
                      <th className="p-4 text-left font-semibold">Prerequisites</th>
                      <th className="p-4 text-left font-semibold">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {courses.map(course => (
                      <tr key={course.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                        <td className="p-4">{course.id}</td>
                        <td className="p-4">{course.title}</td>
                        <td className="p-4">{course.description}</td>
                        <td className="p-4">{course.prerequisites ? course.prerequisites.join(', ') : 'None'}</td>
                        <td className="p-4">
                          <button
                            onClick={() => deleteCourse(course.id)}
                            className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* View All Instances Section */}
        {activeSection === 'view-instances' && (
          <div className="bg-white bg-opacity-95 backdrop-blur-lg rounded-3xl p-8 shadow-2xl">
            <h2 className="text-2xl font-bold mb-6 text-gray-800">All Course Instances</h2>
            
            <div className="bg-gray-50 p-5 rounded-xl mb-5 border border-gray-200">
              <h3 className="text-lg font-semibold mb-4 text-gray-700">Filter Instances</h3>
              <div className="flex gap-4 mb-4 flex-wrap">
                <div className="flex-1 min-w-48">
                  <label className="block mb-2 font-semibold text-gray-600">Year</label>
                  <input
                    type="number"
                    value={filters.year}
                    onChange={(e) => setFilters(prev => ({ ...prev, year: e.target.value }))}
                    min="2020"
                    max="2030"
                    placeholder="Enter year"
                    className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                  />
                </div>
                <div className="flex-1 min-w-48">
                  <label className="block mb-2 font-semibold text-gray-600">Semester</label>
                  <select
                    value={filters.semester}
                    onChange={(e) => setFilters(prev => ({ ...prev, semester: e.target.value }))}
                    className="w-full p-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200 transition-all"
                  >
                    <option value="">All semesters</option>
                    <option value="1">Semester 1</option>
                    <option value="2">Semester 2</option>
                  </select>
                </div>
              </div>
              <div className="flex gap-3">
                <button
                  onClick={loadFilteredInstances}
                  className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:-translate-y-1 hover:shadow-xl transition-all font-semibold"
                >
                  Apply Filter
                </button>
                <button
                  onClick={() => setInstances([])}
                  className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-semibold"
                >
                  Clear
                </button>
              </div>
            </div>
            
            {loading.instances ? (
              <div className="text-center py-8 text-gray-600">Loading instances...</div>
            ) : instances.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full bg-white rounded-xl overflow-hidden shadow-lg">
                  <thead>
                    <tr className="bg-gradient-to-r from-indigo-500 to-purple-600 text-white">
                      <th className="p-4 text-left font-semibold">Instance ID</th>
                      <th className="p-4 text-left font-semibold">Course ID</th>
                      <th className="p-4 text-left font-semibold">Year</th>
                      <th className="p-4 text-left font-semibold">Semester</th>
                      <th className="p-4 text-left font-semibold">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {instances.map(instance => (
                      <tr key={instance.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                        <td className="p-4">{instance.id}</td>
                        <td className="p-4">{instance.courseId}</td>
                        <td className="p-4">{instance.year}</td>
                        <td className="p-4">{instance.semester}</td>
                        <td className="p-4">
                          <button
                            onClick={() => deleteInstance(instance.year, instance.semester, instance.courseId)}
                            className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center py-8 text-gray-600">
                Please use the filter to view instances by year and semester.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default CourseManagementSystem;