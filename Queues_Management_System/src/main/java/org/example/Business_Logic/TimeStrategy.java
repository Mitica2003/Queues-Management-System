package org.example.Business_Logic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class TimeStrategy implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task task) {
        int minimumValue = 10000000;
        for( Server i: servers ){
            if( i.getWaitingPeriod().get() < minimumValue ){
                minimumValue = i.getWaitingPeriod().get();  // Salvam valoarea minima
            }
        }

        for( Server i: servers ){
            if( i.getWaitingPeriod().get() == minimumValue ){
                i.addTask(task);                            // Introducem valoarea minima
                break;
            }
        }
    }
}
