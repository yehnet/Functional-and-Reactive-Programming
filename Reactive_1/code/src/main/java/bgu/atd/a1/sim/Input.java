package bgu.atd.a1.sim;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Input{
    private int threads;
    private List<Computer> Computers;
    @SerializedName("Phase 1") private List<?> phase_1;
    @SerializedName("Phase 2") private List<?> phase_2;
    @SerializedName("Phase 3") private List<?> phase_3;

    public Input() {
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public List<Computer> getComputer() {
        return Computers;
    }

    public List<?> getPhase_1() {
        return phase_1;
    }

    public int getPhase_1_size(){
        return phase_1.size();
    }

    public List<?> getPhase_2() {
        return phase_2;
    }

    public int getPhase_2_size(){
        return phase_2.size();
    }

    public List<?> getPhase_3() {
        return phase_3;
    }

    public int getPhase_3_size(){
        return phase_3.size();
    }

    @Override
    public String toString() {
        return "InputObj{" +
                "threads=" + threads +
                ", computers=" + Computers +
                ", phase_1=" + phase_1 +
                ", phase_2=" + phase_2 +
                ", phase_3=" + phase_3 +
                '}';
    }
}
