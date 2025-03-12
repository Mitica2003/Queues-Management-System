package org.example.Model;

public class Task {

    int taskID;
    private int arrivalTime;
    private int serviceTime;

    public Task(int taskID, int arrivalTime, int serviceTime) {
        this.taskID = taskID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
}
