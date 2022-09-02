package bgu.atd.a1.sim.actions.student;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class RemoveCourse extends Action<String> {
    private String courseName;

    public RemoveCourse(String courseName, ActorThreadPool actors_thread_pool) {
        super("removing course from student list\n", actors_thread_pool);
        this.courseName = courseName;
    }

    @Override
    protected void start() {
        StudentPrivateState sps = (StudentPrivateState) this.actorState;

        if (sps.getCourses().contains(courseName)) {
            sps.removeCourse(courseName);
        }

        super.then(null, () -> {
            complete("student unregistered from course\n");
        });
    }
}
