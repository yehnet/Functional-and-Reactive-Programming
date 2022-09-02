package bgu.atd.a1.sim.actions.course;

import java.util.List;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;

public class NewCourse extends Action<String> {
    private List<String> prequisites;
    private int spots;

    public NewCourse(List<String> prequisites,int spots, ActorThreadPool pool){
        super("new course has opened\n", pool);
        this.prequisites = prequisites;
        this.spots = spots;
    }

    public void start(){
        CoursePrivateState cps = (CoursePrivateState) this.actorState;
        cps.setPrequisites(prequisites);
        cps.incAvailableSpots(spots);
        complete("new course has opened\n");
    }
    
}
