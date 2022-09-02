package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;

public class newActor extends Action<String>{
    public newActor() {
        super("Add actor", null);
    }

    public void start(){
        //System.out.println(" New actor is handled");
        super.then(null, () -> complete("actor added\n"));
    }
}
