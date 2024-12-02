package lab3;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    private List<Process> processes;
    private MMU mmu;
    private int maxProcesses;

    public Kernel(int maxProcesses, int totalPhysicalPages) {
        this.processes = new ArrayList<>();
        this.mmu = new MMU(totalPhysicalPages);
        this.maxProcesses = maxProcesses;
    }

    public void createProcess(int totalVirtualPages) {
        if(processes.size() < maxProcesses) {
            Process process = new Process(processes.size(), totalVirtualPages);
            processes.add(process);
        }
    }

    public void executeProcess() {
        for(Process process: processes) {
            mmu.handleProcess(process);
        }
    }
}
