package bgu.atd.a1.sim.actions.course;

import java.util.ArrayList;
import java.util.List;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;

public class DeactivateCourse extends Action<String> {
    private String courseName;

    public DeactivateCourse(String courseName, ActorThreadPool pool) {
        super("Deactivate Course", pool);
        this.courseName = courseName;
    }

    public void start() {
        CoursePrivateState cps = (CoursePrivateState) this.actorState;
        List<String> students = cps.getRegStudents();

        ArrayList<Action<String>> actions = new ArrayList<>();
        for (String student : students) {
            Action<String> unregisterstudent = new UnregisterStudent(student, courseName, actors_thread_pool);
            actions.add(unregisterstudent);
            super.sendMessage(unregisterstudent, courseName, new CoursePrivateState());
        }

        super.then(actions, () -> {
            cps.closeCourse();
            complete("students unregistered from course\n");
        });
    }
}

