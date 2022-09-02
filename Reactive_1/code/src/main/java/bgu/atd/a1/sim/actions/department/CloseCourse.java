package bgu.atd.a1.sim.actions.department;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;

public class CloseCourse extends Action<String> {
    private String courseName;

    public CloseCourse(String courseName, ActorThreadPool pool) {
        super("closing a course", pool);
        this.courseName = courseName;
    }

    public void start() {
        DepartmentPrivateState dps = (DepartmentPrivateState) this.actorState;

        ArrayList<Action<String>> actions = new ArrayList<>();
        Action<String> remStud = new UnregisterStudents(courseName, actors_thread_pool);
        super.sendMessage(remStud, courseName, new CoursePrivateState());
        actions.add(remStud);

        super.then(actions, () -> {
            dps.removeCourse(courseName);
            dps.addRecord("Close Course");
            complete("course is closed\n");
        });
    }

}
