import java.util.List;

public class SJF implements Algorithm {

    private List<Task> tasks;
    final int TASKNUM;
    private int[] waitTime;
    private int[] turnaroundTime;
    private int[] responseTime;
    
    /**
     * Constructor
     *
     * Parameters: List of Tasks
     * @param tasks
     */
    public SJF(List<Task> tasks) {
    	//intializes variables and sets times to zero
        this.tasks = tasks;
        TASKNUM = tasks.size();
        waitTime = new int[TASKNUM];
        turnaroundTime = new int[TASKNUM];
        responseTime = new int[TASKNUM];
        
        for(int i = 0; i < TASKNUM; i++) {
        	waitTime[i] = 0;
        	turnaroundTime[i] = 0;
        	responseTime[i] = 0;
        }
    }

    
    /**
     * Invokes the scheduler
     */
    public void schedule() {
    	
    	for(int i = 0; i < TASKNUM; i++) {
    		//get next task, run it, increment turnaround time
    		Task currentTask = pickNextTask();
    		CPU.run(currentTask, currentTask.getBurst());
    		turnaroundTime[i] += currentTask.getBurst();
 
    		//increment time for all other processes
    		for(int k = 0; k < tasks.size(); k++) {
    			turnaroundTime[k] += currentTask.getBurst();
    			waitTime[k] += currentTask.getBurst();
    			responseTime[k] += currentTask.getBurst();
    		}
    	}
    	//display time averages after completion
    	calculateAvg();
    }

    /**
     * Selects the next task using the appropriate scheduling algorithm
     */
    public Task pickNextTask() {
    	//finds the lowest burst time in the list and returns it while removing it from the list
    	int lowestBurst = tasks.get(0).getBurst();
    	int index = 0;
    	
    	for(int i = 1; i < tasks.size(); i++) {
    		int currentBurst = tasks.get(i).getBurst();
    		if(lowestBurst > currentBurst) {
    			lowestBurst = currentBurst;
    			index = i;
    		}
    	}
    	
        Task pickedTask = tasks.remove(index);
        return pickedTask;
    }

    
    /**
     * Averages and displays the times stored in waitTime, responseTime, and turnaroundTime
     */
    public void calculateAvg() {
    	double avgWait = 0;
    	double avgResponse = 0;
    	double avgTurnaround = 0;
    	
    	for(int i = 0; i < TASKNUM; i++) {
    		avgWait += waitTime[i];
    		avgResponse += responseTime[i];
    		avgTurnaround += turnaroundTime[i];
    	}
    	
    	System.out.println("Average Wait Time: " + avgWait / TASKNUM);
    	System.out.println("Average Response Time: " + avgResponse / TASKNUM);
    	System.out.println("Average Turnaround Time: " + avgTurnaround / TASKNUM);
    }
}
