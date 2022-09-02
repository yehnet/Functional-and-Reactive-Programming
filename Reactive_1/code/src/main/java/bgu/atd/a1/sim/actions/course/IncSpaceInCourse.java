package bgu.atd.a1.sim.actions.course;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;

public class IncSpaceInCourse extends Action<String> {
    private int spots;

    public IncSpaceInCourse(int spots, ActorThreadPool pool) {
        super("increasing available places in course", pool);
        this.spots = spots;
    }

    public void start() {
        CoursePrivateState cps = (CoursePrivateState) this.actorState;
        cps.incAvailableSpots(spots);

        then(null, () -> {
            complete("spots have been added\n");
        });
    }
}
