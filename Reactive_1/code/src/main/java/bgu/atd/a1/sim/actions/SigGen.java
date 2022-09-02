package bgu.atd.a1.sim.actions;

import java.util.HashMap;
import java.util.List;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.WarehousePrivateState;


public class SigGen extends Action<Long> {

    private HashMap<String,Integer> grades;
    private List<String> courses;
    private String computer;

    public SigGen(HashMap<String,Integer> grades, List<String> courses, String computer, ActorThreadPool pool){
        super("SigGen", pool);
        this.grades = grades;
        this.computer = computer;
        this.courses = courses;
    }

    public void start(){
        Long signature = ((WarehousePrivateState) super.actorState).checkAndSign(courses, grades, computer);
        super.then(null, () -> complete(signature));
    }
    
}
