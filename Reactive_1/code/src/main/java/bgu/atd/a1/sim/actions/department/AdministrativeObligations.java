package bgu.atd.a1.sim.actions.department;

import java.util.ArrayList;
import java.util.List;

import  bgu.atd.a1.Action;
import  bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.GetSig;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class AdministrativeObligations extends Action<String>{

    private List<String> studentList;
    private List<String> courseList;
    private String computer;

    public AdministrativeObligations(List<String> studentList, List<String> courseList, String computer, ActorThreadPool pool){
        super("computer allocation", pool);
        this.studentList = studentList;
        this.courseList = courseList;
        this.computer = computer;
        
    }

    public void start(){
        ArrayList<Action<String>> actions = new ArrayList<>();
        for (String student : studentList) {
            Action<String> getSig = new GetSig(super.actors_thread_pool, courseList, computer);
            actions.add(getSig);
            super.sendMessage(getSig, student, new StudentPrivateState());
        }
        this.getResult().subscribe(() -> {
			this.actorState.addRecord("Administrative Check");
        });
        super.then(actions, () -> complete("Administrative obligations complete"));
    }
}
