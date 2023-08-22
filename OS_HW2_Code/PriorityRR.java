import java.util.ArrayList;
import java.util.List;

public class PriorityRR implements Algorithm {

    private List<Task> tasks;
    private List<Task> currentRR;
    private int BURST_TIME = 10;
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
    public PriorityRR(List<Task> tasks) {
    	//initializes variables and sets times to 0
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
        	runTime[i] = 0;
        }
    }

    /**
     * Invokes the scheduler
     */
    public void schedule() {
    	int time = 0;
    	int timeIndex = 0;
    	//while there are still tasks
    	while(tasks.size() > 0) {
    		
    		//create a sublist of all tasks (needed for pickNextTask())
    		currentRR = new ArrayList<Task>(tasks);
    		
    		//get next highest priority task
    		Task currentTask = pickNextTask();
   			
   			//clear copy of tasks and create a sublist with only tasks of current highest priority
   			currentRR.clear();
   			currentRR.add(currentTask);
   			for(int k = 0; k < tasks.size(); k++) {
   				if((currentTask.getPriority() == tasks.get(k).getPriority()) && !(currentTask.equals(tasks.get(k)))) {
   					currentRR.add(tasks.get(k));
   				}
   			}
   			
   			//if only one task of highest priority: run it to completion
   			if(currentRR.size() == 1) {
   				//set response time
   				responseTime[timeIndex] = time;
   				
   				//run and increment time
   				CPU.run(currentTask, currentTask.getBurst());
   				time += currentTask.getBurst();
   				
   				//set wait time and turnaround time
   				runTime[timeIndex] += currentTask.getBurst();
   				waitTime[timeIndex] = time - runTime[timeIndex];
   				turnaroundTime[timeIndex] = time;
   				timeIndex++;
   				
   				//remove task from sublist and original list
   				tasks.remove(currentTask);
   				currentRR.remove(currentTask);
   				
   				//print that task has finished
   				System.out.println("Task " + currentTask.getName() + " finished.\n");
   			} 
   			//if multiple tasks of highest priority
   			else {
   				//create a list to store all the tasks of the same current highest priority so that currentRR can be changed
   				List<Task> tempList = new ArrayList<Task>(currentRR);
   				
   				//while there are still tasks of that priority
   				boolean firstRun = true;
   				int orgTimeIndex = timeIndex;
   				
   				//keep track of initial number of tasks
   				int tasksToRun = tempList.size();
   				while(tempList.size() > 0) {
   					
   					//run for the amount tasks in starting currentRR
   					int RRSize = currentRR.size();
   					for(int j = 0; j < RRSize; j++) {
   						//pick task within sublist and run
   						currentTask = pickNextTask();
   						if(firstRun) {
   							responseTime[timeIndex] = time;
   						}
   						CPU.run(currentTask, BURST_TIME);
   						time += BURST_TIME;
   						runTime[timeIndex] += BURST_TIME;
   						timeIndex++;
   						
   						currentTask.setBurst(currentTask.getBurst() - BURST_TIME);
   						
   						//remove from possible tasks to run for this RR
   						currentRR.remove(currentTask);
   						
   						//if the task completes, remove it from tempList and tasks
   						if(currentTask.getBurst() <= 0) {
   							time += currentTask.getBurst();
   							runTime[timeIndex - 1] += currentTask.getBurst();
   							turnaroundTime[timeIndex - 1] = time;
   							waitTime[timeIndex - 1] = time - runTime[timeIndex - 1];
   	   		   				
   							tempList.remove(currentTask);
   							tasks.remove(currentTask);
   							System.out.println("Task " + currentTask.getName() + " finished.\n");
   						}
   					}
   					//reset the currentRR with the remaining tasks of the same priority
   					currentRR = new ArrayList<Task>(tempList);
   					firstRun = false;
   					timeIndex = orgTimeIndex;
   				}
   				timeIndex += tasksToRun;
   			}
      	}
    	calculateAvg();
    }

    /**
     * Selects the next task using the appropriate scheduling algorithm
     */
    public Task pickNextTask() {
    	int lowestPri = currentRR.get(0).getPriority();
    	int index = 0;
    	
    	for(int i = 1; i < currentRR.size(); i++) {
    		int currentPri = currentRR.get(i).getPriority();
    		if(lowestPri < currentPri) {
    			lowestPri = currentPri;
    			index = i;
    		}
    	}
    	
        Task pickedTask = currentRR.get(index);
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