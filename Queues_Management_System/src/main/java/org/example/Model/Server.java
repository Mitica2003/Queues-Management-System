package org.example.Model;
//
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable{

    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(){
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    @Override
    public void run() {
        while (true){
            if(!tasks.isEmpty()) {
                try {
                    int servTime = tasks.peek().getServiceTime();
                    for (int i = 0; i < servTime; i++) {
                        Thread.sleep(1000);
                        int x = tasks.peek().getServiceTime() - 1;
                        tasks.peek().setServiceTime(x);
                        if (waitingPeriod.get() >= 1)
                            waitingPeriod.decrementAndGet();
                    }
                    tasks.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Task[] getTasks(){
        return tasks.toArray(new Task[0]);
    }


    public void setTasks(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public int getSizeQueue(){
        return this.tasks.size();
    }
}

