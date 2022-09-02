package bgu.atd.a1.sim.privateStates;

import java.util.List;
import java.util.ArrayList;

import bgu.atd.a1.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		regStudents = new ArrayList<>();
		registered = 0;
		availableSpots = 0;
	}

	public Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public void setPrequisites(List<String> prequisites){
		this.prequisites = prequisites;
	}
	public List<String> getPrequisites() {
		return prequisites;
	}

	public void incAvailableSpots(int spots) {
		this.availableSpots += spots;
	}

	public void closeCourse(){
		this.availableSpots = -1;
	}

	// public void init(Integer availableSpots, List<String> prequisites){
	// 	// why this is'nt in the constructor???
	// 	this.availableSpots = availableSpots;
	// 	this.prequisites = prequisites;
	// }

	public boolean regStudent(String studentnumber){
		if (availableSpots > 0 && !regStudents.contains(studentnumber)){
			regStudents.add(studentnumber);
			availableSpots += -1;
			registered++;
			return true;
		}
		return false;
	}

	public void removeStudent(String studentName){
		if ( regStudents.contains(studentName) ){
			regStudents.remove(studentName);
			availableSpots++;
			registered--;
		}
	}

	public void closeRegisterPeriod(){
		availableSpots = 0;
	}

	public String toString(){
		return regStudents.toString() + "\t" + getLogger().toString();
	}
}
