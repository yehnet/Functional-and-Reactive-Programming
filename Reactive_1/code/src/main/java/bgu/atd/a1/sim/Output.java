package bgu.atd.a1.sim;

import java.util.ArrayList;
import java.util.HashMap;

public class Output {
    private ArrayList<HashMap<String, Object>> Departments;
    private ArrayList<HashMap<String, Object>> Courses;
    private ArrayList<HashMap<String, Object>> Students;


    public Output() {
        Departments = new ArrayList<>();
        Courses = new ArrayList<>();
        Students = new ArrayList<>();
    }

    public void addDepartment(HashMap<String, Object> Department){
        Departments.add(Department);
    }

    public void addCourse(HashMap<String, Object> Course){
        Courses.add(Course);
    }

    public void AddStudent(HashMap<String, Object> Student){
        Students.add(Student);
    }
}