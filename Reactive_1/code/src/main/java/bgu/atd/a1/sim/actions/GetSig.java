package bgu.atd.a1.sim.actions;

import java.util.ArrayList;
import java.util.List;


import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;
import bgu.atd.a1.sim.privateStates.WarehousePrivateState;

// sumbit to students actor to get the grads from the private state

public class GetSig extends Action<String> {

    private ActorThreadPool pool;
    private List<String> courses;
    private String computer;

    public GetSig(ActorThreadPool pool, List<String> courses, String computer) 
    {
        super("Signature generation", pool);
        this.courses = courses;
        this.computer = computer;
    }

    public void start(){
        ArrayList<Action<Long>> actions = new ArrayList<>();
        Action<Long> siggen = new SigGen(((StudentPrivateState) super.actorState).getGrades(), courses, computer, pool);
        actions.add(siggen);
        super.sendMessage(siggen, "Warehouse", new WarehousePrivateState());
        super.then(actions, () -> {
            ((StudentPrivateState) super.actorState).addSignature(siggen.getResult().get());
            complete("Got student sign");});
    }

}
