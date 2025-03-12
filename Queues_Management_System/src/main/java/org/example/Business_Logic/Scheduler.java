package org.example.Business_Logic;
import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;
import java.util.ArrayList;

public class Scheduler {

    private List<Server> servers;
    private int maxNoServers;
    private int maxTaskPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTaskPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTaskPerServer = maxTaskPerServer;
        servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            Thread thread = new Thread(server);
            servers.add(server);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ShortestQueueStrategy();
        if(policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new TimeStrategy();
    }

    public void dispatchTask(Task t) {
        strategy.addTask(servers, t);
    }

    public List<Server> getServers() {
        return servers;
    }

    public enum SelectionPolicy {
        SHORTEST_QUEUE, SHORTEST_TIME
    }
}
