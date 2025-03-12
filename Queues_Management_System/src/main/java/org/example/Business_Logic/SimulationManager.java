package org.example.Business_Logic;
import org.example.Model.Task;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable{
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int minArrivalTime;
    public int maxArrivalTime;
    public int numberOfServers;
    public int numberOfClients;
    public Scheduler.SelectionPolicy selectionPolicy;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private FileWriter fileOutput;

    static double averageWaitingTime = 0;
    static double averageServiceTime = 0;
    static int peakHour = 0;

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int minArrivalTime, int maxArrivalTime, int numberOfServers, int numberOfClients, Scheduler.SelectionPolicy selectionPolicy) throws IOException {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.selectionPolicy = selectionPolicy;

        scheduler = new Scheduler(numberOfServers, maxProcessingTime);
        scheduler.changeStrategy(selectionPolicy);
        generatedTasks = new ArrayList<>();
        fileOutput = new FileWriter("logOfEvents.txt");
        generateNRandomTasks(numberOfClients, minArrivalTime, maxArrivalTime, minProcessingTime, maxProcessingTime);
    }

    private void calculateTimesFromSimulation(int serviceTime){
        averageServiceTime += serviceTime;
    }

    public void generateNRandomTasks(int numberOfClients, int minTimeArrival, int maxTimeArrival, int minTimeServ, int maxTimeServ ){
        Random random = new Random();
        for( int i = 1; i <= numberOfClients; i++ ){
            int arrivalTime = random.nextInt(maxTimeArrival - minTimeArrival) + minTimeArrival;
            int serviceTime = random.nextInt(maxTimeServ - minTimeServ) + minTimeServ;

            Task task = new Task(i, arrivalTime, serviceTime);
            generatedTasks.add(task);
            calculateTimesFromSimulation(serviceTime);
        }
        averageServiceTime /= numberOfClients;
        Collections.sort(generatedTasks, Comparator.comparing(Task::getArrivalTime));
    }

    private double calculateAverageWaitingTime() {
        int waitingTime = 0;
        int numberOfClients = 0;
        for (int i = 0; i < scheduler.getServers().size(); i++ ) {
            if(scheduler.getServers().get(i).getTasks().length > 0) {
                for(Task j : scheduler.getServers().get(i).getTasks()) {
                    waitingTime += j.getArrivalTime();
                    break;
                }
                numberOfClients += scheduler.getServers().get(i).getTasks().length;
            }
        }

        if (numberOfClients == 0)
            return 0.0;
        else
            return (double) waitingTime / numberOfClients;
    }

    public int getPeakHourFromSimulation(int peakHourTime, int currentTime){
        int currentPeakHour = 0;
        for (int i = 0; i < scheduler.getServers().size(); i++ ) {
            if(scheduler.getServers().get(i).getTasks().length > 0) {
                currentPeakHour += scheduler.getServers().get(i).getTasks().length;
            }
        }
        if(currentPeakHour > peakHour){
            peakHour = currentPeakHour;
            return currentTime;
        }
        else
            return peakHourTime;
    }

    private void closeFile(){
        try {
            fileOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOtherTimesInFile(){
        try {
            fileOutput.write("\n");
            fileOutput.write("Average waiting time: " + averageWaitingTime);
            fileOutput.write("\n");
            fileOutput.write("Average service time: " + averageServiceTime);
            fileOutput.write("\n");
            fileOutput.write("Peak hour for the simulation interval: " + peakHour);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void threadSleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void removeFromGeneratedTasks(int currentTime){
        int currentTimeInSimulation = currentTime;
        generatedTasks.removeIf(task -> task.getArrivalTime() == currentTimeInSimulation);
    }

    private void dispatchTasks(int currentTime){
        for (Task task : generatedTasks) {
            if (task.getArrivalTime() == currentTime) {
                scheduler.dispatchTask(task);
            }
        }
    }

    private void writeInFile(int currentTime){
        try {
            fileOutput.write("Time " + currentTime);
            fileOutput.write("\n");

            fileOutput.write("Waiting clients: ");
            for(Task i : generatedTasks){
                fileOutput.write("(" + i.getTaskID() + "," + i.getArrivalTime() + "," + i.getServiceTime() + ")" + "; ");
            }
            fileOutput.write("\n");

            for( int i = 0; i < scheduler.getServers().size(); i++ ) {
                int k = i + 1;
                fileOutput.write("Queue " + k + ":");
                if(scheduler.getServers().get(i).getTasks().length > 0) {
                    for(Task j : scheduler.getServers().get(i).getTasks()) {
                        fileOutput.write("(" + j.getTaskID() + "," + j.getArrivalTime() + "," + j.getServiceTime() + ")" + "; ");
                    }
                }
                else
                    fileOutput.write("closed");
                fileOutput.write("\n");
            }
            fileOutput.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int currentTime = 0, peakHourTime = 0;
        while(currentTime < timeLimit) {
            System.out.println("Running for time " + currentTime + "...");
            dispatchTasks(currentTime);
            removeFromGeneratedTasks(currentTime);
            averageWaitingTime += calculateAverageWaitingTime();
            peakHourTime = getPeakHourFromSimulation(peakHourTime, currentTime);

            threadSleep();
            writeInFile(currentTime);
            threadSleep();

            currentTime++;
        }
        averageWaitingTime = averageWaitingTime / (timeLimit * numberOfServers);
        peakHour = peakHourTime;

        writeOtherTimesInFile();
        closeFile();

        averageWaitingTime = averageServiceTime = 0.0;
        peakHour = 0;

        System.out.println("\n Finished running. Check 'logOfEvents' file for the results.");
    }
}
