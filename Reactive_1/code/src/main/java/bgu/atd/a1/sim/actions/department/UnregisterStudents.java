package bgu.atd.a1.sim.actions.department;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.course.DeactivateCourse;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class UnregisterStudents extends Action<String> {
    private String courseName;

    public UnregisterStudents(String courseName, ActorThreadPool pool) {
        super("unregister student", pool);
        this.courseName = courseName;
    }

    public void start() {
    ArrayList<Action<String>> actions = new ArrayList<>();
    CoursePrivateState cps = (CoursePrivateState) this.actorState;
    Action<String> deactice = new DeactivateCourse(courseName, super.actors_thread_pool);
    actions.add(deactice);
    super.sendMessage(deactice, courseName, new StudentPrivateState());
    super.then(actions, () -> {
        cps.closeCourse();
        complete("students unregistered from course\n");
        });
    }
}
