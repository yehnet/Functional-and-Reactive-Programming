package bgu.atd.a1.sim.privateStates;

import bgu.atd.a1.PrivateState;
import bgu.atd.a1.sim.Computer;

import java.util.List;
import java.util.HashMap;


public class WarehousePrivateState extends PrivateState {
    
    private HashMap<String, Computer> Computers = new HashMap<>();

    public WarehousePrivateState(List<Computer> computers){
        for (Computer computer : computers) {
            this.Computers.put(computer.getName(), computer);
        }
    }

    public WarehousePrivateState() {}

    public long checkAndSign(List<String> courses, HashMap<String, Integer> coursesGrades, String computer){
        return Computers.get(computer).checkAndSign(courses, coursesGrades);
    }
}   
