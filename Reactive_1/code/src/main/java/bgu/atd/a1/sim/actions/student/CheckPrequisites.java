package bgu.atd.a1.sim.actions.student;

import java.util.List;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class CheckPrequisites extends Action<String> {
    private List<String> prequisites;

    public CheckPrequisites(List<String> prequisites, ActorThreadPool pool) {
        super("check prequisites", pool);
        this.prequisites = prequisites;
    }

    public void start() {
        StudentPrivateState sps = (StudentPrivateState) this.actorState;
        if (sps.CheckPrequisites(prequisites))
            complete("true");
        else
            complete("false");
    }

}
