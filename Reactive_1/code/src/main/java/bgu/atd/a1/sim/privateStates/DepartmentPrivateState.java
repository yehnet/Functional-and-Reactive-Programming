package bgu.atd.a1.sim.privateStates;

import java.util.List;
import java.util.ArrayList;


import bgu.atd.a1.PrivateState;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState{
	private List<String> courseList;
	private List<String> studentList;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState() {
		this.courseList = new ArrayList<String>();
		this.studentList = new ArrayList<String>();
	}

	public List<String> getCourseList() {
		return courseList;
	}

	public List<String> getStudentList() {
		return studentList;
	}

	public void addCourse(String courseName){
		courseList.add(courseName);
	}

	public void removeCourse(String courseName){
		courseList.remove(courseName);
	}

	public void addStudent(String studentName){
		studentList.add(studentName);
	}

	public String toString() {
		return courseList.toString() + "\t" + studentList.toString() + "\t" + getLogger().toString();
	}
}
