package bgu.atd.a1.sim.actions.student;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;

public class NewStudent extends Action<String> {

    public NewStudent(ActorThreadPool pool) {
        super("add student", pool);
    }

    @Override
    protected void start() {
        then(null, () -> {
            complete("added student\n");
        });
    }
}
