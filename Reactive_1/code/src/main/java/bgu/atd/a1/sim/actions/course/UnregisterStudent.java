package bgu.atd.a1.sim.actions.course;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.student.RemoveCourse;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class UnregisterStudent extends Action<String> {
    private String studentName;
    private String courseName;

    public UnregisterStudent(String studentName, String courseName, ActorThreadPool pool) {
        super("unregister student", pool);
        this.studentName = studentName;
        this.courseName = courseName;
    }

    public void start() {
        CoursePrivateState cps = (CoursePrivateState) this.actorState;
        
        if (cps.getRegStudents().contains(studentName)) {
            cps.removeStudent(studentName);

            ArrayList<Action<String>> actions = new ArrayList<>();
            Action<String> removeCourse = new RemoveCourse(courseName, actors_thread_pool);
            super.sendMessage(removeCourse, studentName, new StudentPrivateState());
            actions.add(removeCourse);

            super.then(actions, () -> {
                complete("student unregistered from course\n");
                this.actorState.addRecord("Unregister");
            });
        } else {
            super.then(null, () -> {
                complete("failed to unregister student from course - student isn't registered");
            });
        }
    }
}
