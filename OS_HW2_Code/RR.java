import java.util.ArrayList;
import java.util.List;

public class RR implements Algorithm {

    private List<Task> tasks;
    private List<Task> currentRR;
    private final int BURST_TIME = 10;
    private final int TASKNUM;
    private int[] waitTime;
    private int[] runTime;
    private int[] turnaroundTime;
    private int[] responseTime;
    
    /**
     * Constructor
     *
     * Parameters: List of Tasks
     * @param tasks
     */
    public RR(List<Task> tasks) {
    	//initializes variables and sets times to 0, and runTime to the runtime of each task
    	this.tasks = tasks;
    	TASKNUM = tasks.size();
    	waitTime = new int[TASKNUM];
        turnaroundTime = new int[TASKNUM];
        responseTime = new int[TASKNUM];
        runTime = new int[TASKNUM];
        
        for(int i = 0; i < TASKNUM; i++) {
        	waitTime[i] = 0;
        	turnaroundTime[i] = 0;
        	responseTime[i] = 0;
        	runTime[i] = tasks.get(i).getBurst();
        }
    }

    /**
     * Invokes the scheduler
     */
    public void schedule() {
    	//variables to keep track of time and firstRun for response time
    	int time = 0;
    	boolean firstRun = true;
    	
    	//while there are more tasks to perform
    	while(tasks.size() > 0) {
    		//keep track of initial loop size
    		int numRuns = tasks.size();
    		
    		//create a copy of the list for the RR run
    		currentRR = new ArrayList<Task>(tasks);
    		
    		//for each task currently in the list
    		for(int i = 0; i < numRuns; i++) {
    			//store response time on first run
    			if(firstRun) {
	    			responseTime[i] = time;
    			}
    			
    			//pick next task, run, decrement burst time, remove task from sublist, increment time
    			Task currentTask = pickNextTask();
    			CPU.run(currentTask, BURST_TIME);
    			currentTask.setBurst(currentTask.getBurst() - BURST_TIME);
    			currentRR.remove(currentTask);
    			time += BURST_TIME;
    			
    			//if the task finishes
    			if(currentTask.getBurst() <= 0) {
    				//remove the task from the original list
    				tasks.remove(currentTask);
    				
    				//add any leftover time back into time
    				time += currentTask.getBurst();
    				
    				//store turnaround time and wait time
    				turnaroundTime[currentTask.getTid()] = time;
    				waitTime[currentTask.getTid()] = time - runTime[currentTask.getTid()];
    				
    				//print that task finished
    				System.out.println("Task " + currentTask.getName() + " finished.\n");
    			}
    		}
    		//after first run, stop setting response time
    		firstRun = false;
    	}
    	//calculate and display the average times
    	calculateAvg();
    }

    /**
     * Selects the next task using the appropriate scheduling algorithm
     */
    public Task pickNextTask() {
    	//return tasks in order
        Task pickedTask = currentRR.get(0);
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
