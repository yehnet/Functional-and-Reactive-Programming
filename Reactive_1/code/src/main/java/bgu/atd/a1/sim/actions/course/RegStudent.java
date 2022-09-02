package bgu.atd.a1.sim.actions.course;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.student.AddGrade;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class RegStudent extends Action<String> {

    private String studentNumber;
    private String grade;

    public RegStudent(String studentNumber, String grade, ActorThreadPool pool) {
        super("Add student to course", pool);
        this.studentNumber = studentNumber;
        this.grade = grade;
    }

    public void start() {
        CoursePrivateState ps = (CoursePrivateState) this.actorState;
        boolean success = ps.regStudent(studentNumber);
        if (success) {
            ArrayList<Action<String>> actions = new ArrayList<Action<String>>();
            Action<String> addGradeAction = new AddGrade(grade, this.actorID, actors_thread_pool);
            actions.add(addGradeAction);
            super.sendMessage(addGradeAction, studentNumber, new StudentPrivateState());

            if (grade != "-") {
                super.then(actions, () -> {
                    complete("student added to course");
                });
            }
            super.then(actions, () -> {
                complete("student added to course");
            });
        } else {
            super.then(null, () -> {
                complete("failed to add student to course - not enough places");
            });
        }
    }

}
