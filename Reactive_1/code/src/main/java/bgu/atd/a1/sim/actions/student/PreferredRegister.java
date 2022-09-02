package bgu.atd.a1.sim.actions.student;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.course.AddStudentToCourse;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class PreferredRegister extends Action<String> {
    private String studentName;
    private ArrayList<String> coursesList;
    private ArrayList<String> gradesList;

    public PreferredRegister(String studentName, ArrayList<String> coursesList, ArrayList<String> gradesList, ActorThreadPool pool) {
        super("preferred register", pool);
        this.studentName = studentName;
        this.coursesList = coursesList;
        this.gradesList = gradesList;
    }

    public void start() {
        if (coursesList.size() > 0){
            String curr_course = coursesList.remove(0);
            String curr_grade = gradesList.remove(0);
            ArrayList<Action<Boolean>> actions = new ArrayList<>();
            Action<Boolean> register = new AddStudentToCourse(studentName, curr_course, curr_grade, actors_thread_pool);
            super.sendMessage(register, curr_course, new CoursePrivateState());
            actions.add(register);

            then(actions, () -> {
                if (register.getResult().get()) {
                    complete("success");
                } else {
                    ArrayList<Action<String>> actionsN = new ArrayList<>();
                    Action<String> nextPref = new PreferredRegister(studentName, coursesList, gradesList, actors_thread_pool);
                    super.sendMessage(nextPref, studentName, new StudentPrivateState());
                    actionsN.add(nextPref);

                    then(actionsN , () -> {
                        if(nextPref.getResult().get().equals("success"))
                            complete("success");
                        else
                            complete("failed");
                    });
                }
            });
        } else {
            complete("failed");
        }
    }
}
