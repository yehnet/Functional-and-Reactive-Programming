/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.atd.a1.sim;
import java.util.HashMap;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.Map;

import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.PrivateState;

import bgu.atd.a1.Action;

import bgu.atd.a1.sim.actions.*;
import bgu.atd.a1.sim.actions.course.*;
import bgu.atd.a1.sim.actions.department.*;
import bgu.atd.a1.sim.actions.student.*;
import bgu.atd.a1.sim.privateStates.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	
	public static ActorThreadPool actorThreadPool;
	public static CountDownLatch P1cdl;
	public static CountDownLatch P2cdl;
	public static CountDownLatch P3cdl;
	public static Input input;

	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
		actorThreadPool.submit(new newActor(), "Warehouse", new WarehousePrivateState(input.getComputer()));

		

		// Phase 1
		System.out.println("Phase 1 has started!");

		for (Object phase : input.getPhase_1()) {
			phaseToAction((Map<String, Object>) phase, P1cdl);
		}

		// Start actor
		actorThreadPool.start();
		System.out.println("Pool started");
	

		try {P1cdl.await();} catch (InterruptedException e) {}


		System.out.println("Phase 2 has started!");

		for (Object phase : input.getPhase_2()) {
			phaseToAction((Map<String, Object>) phase, P2cdl);
		}


		try {P2cdl.await();} catch (InterruptedException e) {}

		System.out.println("Phase 3 has started!");

		for (Object phase : input.getPhase_3()) {
			phaseToAction((Map<String, Object>) phase, P3cdl);
		}

		try {P3cdl.await();} catch (InterruptedException e) {}

		System.out.println("Phases ended!");
    }
	

	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/
	public static HashMap<String,PrivateState> end(){
		try { actorThreadPool.shutdown(); } catch (InterruptedException e) {};
		return (HashMap<String,PrivateState>)actorThreadPool.getActors();
	}

	private static void phaseToAction(Map<String, Object> action, CountDownLatch phase){
		String actionName = (String) action.get("Action");

		Action currAction;

        switch (actionName){
            case "Open Course":
                currAction = new OpenCourse((String) action.get("Course"),
											(ArrayList<String>) action.get("Prerequisites"),
									 		Integer.valueOf((String) action.get("Space")),
									 		actorThreadPool);

				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Department"), new DepartmentPrivateState());
				break; 

			case "Add Student":
				currAction = new AddStudent((String) action.get("Student"), actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Department"), new DepartmentPrivateState());
				break;

			case "Participate In Course":
				currAction = new AddStudentToCourse((String) action.get("Student"),
													(String) action.get("Course"),
													 ((ArrayList<String>) action.get("Grade")).get(0), // for some reason input is list
													 actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Course"), new CoursePrivateState());
				break;

			case "Register With Preferences":
				currAction = new PreferredRegister((String) action.get("Student"),
												(ArrayList<String>) action.get("Preferences"),
												(ArrayList<String>) action.get("Grade"),
												actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Student"), new StudentPrivateState());		
				break;

			case "Unregister":
				currAction = new UnregisterStudent((String) action.get("Student"),
													(String) action.get("Course"),
													actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Course"), new CoursePrivateState());
				break;

			case "Close Course":
				currAction = new CloseCourse((String) action.get("Course"), actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Department"), new DepartmentPrivateState());
				break;

			case "Administrative Check":
				currAction = new AdministrativeObligations((ArrayList<String>) action.get("Students"),
														   (ArrayList<String>) action.get("Conditions"),
														   (String) action.get("Computer"), actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Department") , new DepartmentPrivateState());
				break;

			case "Add Spaces":
				currAction = new IncSpaceInCourse(Integer.valueOf((String) action.get("Number")), actorThreadPool);
				currAction.getResult().subscribe(() -> {phase.countDown();});
				actorThreadPool.submit(currAction, (String) action.get("Course"), new CoursePrivateState());
				break;

            default:
			System.out.println("Action named: " + actionName + " doesn't exist!\n");
                break;
        }
	}
	
	private static String getKeyByValue(Map <String,PrivateState> map, PrivateState value) {
		for (String key : map.keySet()){
			if (value.equals(map.get(key))){
				return key;
			}
		}
		return null;
	}
	public static void main(String [] args){
		Gson gson = new Gson();
		try {
			Reader reader = new FileReader(args[0]);

			input = gson.fromJson(reader, Input.class);

			P1cdl = new CountDownLatch(input.getPhase_1_size());
			P2cdl = new CountDownLatch(input.getPhase_2_size());
			P3cdl = new CountDownLatch(input.getPhase_3_size());

			ActorThreadPool pool = new ActorThreadPool(input.getThreads());
		
			attachActorThreadPool(pool);
			start();
			
			Map <String,PrivateState> result = end();
			System.out.println("Pool ended");

			result.remove("Warehouse");

			Output output = new Output();

			for (PrivateState state : result.values()) {
				if (state instanceof DepartmentPrivateState){
					HashMap<String, Object> map = new HashMap<>();
					map.put("Department",  getKeyByValue(result,state));
					map.put("actions", state.getLogger());
					map.put("courseList", ((DepartmentPrivateState) state).getCourseList());
					map.put("studentList", ((DepartmentPrivateState) state).getStudentList());
					output.addDepartment(map);
				}

				if (state instanceof CoursePrivateState){
					HashMap<String, Object> map = new HashMap<>();
					map.put("Course",    getKeyByValue(result,state));
					map.put("actions", state.getLogger());
					map.put("availableSpots", ((CoursePrivateState) state).getAvailableSpots());
					map.put("registered", ((CoursePrivateState) state).getRegistered());
					map.put("regStudents", ((CoursePrivateState) state).getRegStudents());
					map.put("prequisites", ((CoursePrivateState) state).getPrequisites());
					output.addCourse(map);
				}

				if (state instanceof StudentPrivateState){
					HashMap<String, Object> map = new HashMap<>();
					map.put("Student",   getKeyByValue(result,state));
					map.put("actions", state.getLogger());
					map.put("grades", ((StudentPrivateState) state).getGradesList());
					map.put("signature", ((StudentPrivateState) state).getSignature());
					output.AddStudent(map);
				}
			}

			gson = new GsonBuilder().setPrettyPrinting().create();


			Writer writer = new FileWriter("out.txt");
			gson.toJson(output, writer);
			writer.close();

			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(result);
			oos.close();

		}
		catch (FileNotFoundException e){ System.out.println("Input file not found"); }
		catch (IOException e) { System.out.println("Output file not found");}
	}
}
