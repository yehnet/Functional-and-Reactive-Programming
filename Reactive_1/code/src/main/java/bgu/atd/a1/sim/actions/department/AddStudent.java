package bgu.atd.a1.sim.actions.department;

import java.util.ArrayList;

import bgu.atd.a1.Action;
import  bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.sim.actions.student.NewStudent;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class AddStudent extends Action<String> {
    private String studentID;

    public AddStudent(String studentID , ActorThreadPool pool) {
        super("new student", pool);
        this.studentID = studentID;
    }

    public void start(){
        ArrayList<Action<String>> actions = new ArrayList<>();

        Action<String> addStud = new NewStudent(actors_thread_pool);
        super.sendMessage(addStud, studentID, new StudentPrivateState());
        actions.add(addStud);

        this.getResult().subscribe(() -> {
            this.actorState.addRecord("Add Student");
        });

        super.then(actions, () -> {
            complete("Student number " + studentID + "added\n");
            ((DepartmentPrivateState) this.actorState).addStudent(studentID);
        });
    }
    
}
