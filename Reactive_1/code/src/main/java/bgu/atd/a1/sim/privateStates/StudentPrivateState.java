package bgu.atd.a1.sim.privateStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bgu.atd.a1.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState {

	private HashMap<String, Integer> grades;
	private long signature;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		this.grades = new HashMap<String, Integer>();
	}

	public List<String> getGradesList() {
		ArrayList<String> gradesList = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : grades.entrySet()){
			gradesList.add("(" + entry.getKey() + ", " + Integer.toString(entry.getValue()) + ")");
		}
		return gradesList;
	}
	public HashMap<String, Integer> getGrades(){
		return grades;
	}

	public Set<String> getCourses() {
		return grades.keySet();
	}

	public List<String> getPassedCourses() {
		List<String> courses = new ArrayList<>();
		for (String sCourse : grades.keySet()) {
			// FIXME : is it the way we check a passed course?
			if (grades.get(sCourse) > 56) {
				courses.add(sCourse);
			}
		}
		return courses;
	}

	public boolean CheckPrequisites(List<String> prequisites) {
		if (prequisites.isEmpty())
			return true;
		if (grades.size() > 0) {
			Set<String> attended = grades.keySet();
			for (String course : prequisites) {
				if (!attended.contains(course) || grades.get(course) < 56) {
					return false;
				}
			}
			return true;
		} else return false;
	}

	public long getSignature() {
		return signature;
	}

	public void addSignature(long signature) {
		this.signature = signature;
	}

	public void addGrade(String course, String grade) {
		if (grade.equals("-")) {
			grades.put(course, -1);
		} else {
			grades.put(course, Integer.valueOf(grade));
		}
	}

	public void removeCourse(String course) {
		if (grades.containsKey(course)) {
			grades.remove(course);
		}
	}

}
