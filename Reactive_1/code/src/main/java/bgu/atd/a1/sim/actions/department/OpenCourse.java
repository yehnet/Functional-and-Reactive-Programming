package bgu.atd.a1.sim.actions.department;

import java.util.ArrayList;
import java.util.List;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.course.NewCourse;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;

public class OpenCourse extends Action<String>{
    private String courseName;
    private List<String> prequisites;
    private Integer spots;
    
    public OpenCourse(String courseName, List<String> prequisites, Integer spots, ActorThreadPool pool){
        super("open a new course\n", pool);
        this.courseName = courseName;
        this.prequisites = prequisites;
        this.spots = spots;
    }
    
    public void start(){
        DepartmentPrivateState dps = (DepartmentPrivateState) this.actorState;
        // dps.addCourse(courseName);

        ArrayList<Action<String>> actions = new ArrayList<>();
        Action<String> addCourse = new NewCourse(prequisites,spots, actors_thread_pool);
        this.getResult().subscribe(() -> {
			dps.addRecord("Open Course");
			dps.addCourse(courseName);
        });
        actions.add(addCourse);
        then(actions, () -> complete("new course has been added\n"));
        super.sendMessage(addCourse, courseName, new CoursePrivateState());
    }
    
}
