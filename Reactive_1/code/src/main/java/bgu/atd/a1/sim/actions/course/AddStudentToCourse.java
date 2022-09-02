package bgu.atd.a1.sim.actions.course;

import java.util.List;
import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.student.CheckPrequisites;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class AddStudentToCourse extends Action<Boolean> {
    private String studentNumber;
    private String courseName;
    private String grade;

    public AddStudentToCourse(String studentNumber, String courseName, String grade, ActorThreadPool pool) {
        super("Add student to course", pool);
        this.studentNumber = studentNumber;
        this.courseName = courseName;
        this.grade = grade;
    }

    public void start() {
        CoursePrivateState cps = (CoursePrivateState) this.actorState;
        List<String> prequisites = cps.getPrequisites();

        ArrayList<Action<String>> actions = new ArrayList<>();
        Action<String> confPre = new CheckPrequisites(prequisites, actors_thread_pool);
        actions.add(confPre);
        super.sendMessage(confPre, studentNumber, new StudentPrivateState());

        super.then(actions, () -> {
            if (confPre.getResult().get().equals("true"))
            {
                Action<String> regStud = new RegStudent(studentNumber,grade,actors_thread_pool);
                super.sendMessage(regStud, courseName, new CoursePrivateState());
                ArrayList<Action<String>> actionsN = new ArrayList<>();
                actionsN.add(regStud);

                super.then(actionsN, () -> {
                    if (regStud.getResult().get().equals("student added to course")) {
                        complete(true);
                        cps.addRecord("Participate In Course");
                    }
                    else
                        complete(false);
                });
            }
            else {
                complete(false);
            }
        });
    }
}
