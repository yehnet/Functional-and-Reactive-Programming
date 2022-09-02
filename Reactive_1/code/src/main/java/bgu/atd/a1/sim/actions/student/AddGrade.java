package bgu.atd.a1.sim.actions.student;

import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;
import bgu.atd.a1.Action;

public class AddGrade extends Action<String>{
    
    private String courseName;
    private String grade;

    public AddGrade(String grade, String courseName, ActorThreadPool pool){
        super("add grade", pool);
        this.courseName = courseName;
        this.grade = grade;
    }

    public void start(){
        StudentPrivateState ps = (StudentPrivateState) this.actorState;
        ps.addGrade(courseName, grade);
        super.then(null, () -> complete("grade added\n"));
    }
}
