package org.example.Business_Logic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public interface Strategy {
    void addTask(List<Server> servers, Task task);

}
