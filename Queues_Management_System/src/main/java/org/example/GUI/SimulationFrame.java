package org.example.GUI;

import org.example.Business_Logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SimulationFrame {

    static JTextField textFieldNumberOfClients = new JTextField();
    static JTextField textFieldNumberOfQueues = new JTextField();
    static JTextField textFieldSimulationInterval = new JTextField();
    static JTextField textFieldMinArrivalTime = new JTextField();
    static JTextField textFieldMaxArrivalTime = new JTextField();
    static JTextField textFieldMinServiceTime = new JTextField();
    static JTextField textFieldMaxServiceTime = new JTextField();
    static JComboBox<String> strategyBox;

    static int numberOfClientsFromFrame;
    static int numberOfServersFromFrame;
    static int timeLimitFromFrame;
    static int minArrivalTimeFromFrame;
    static int maxArrivalTimeFromFrame;
    static int minProcessingTimeFromFrame;
    static int maxProcessingTimeFromFrame;
    static Scheduler.SelectionPolicy selectionPolicyFromFrame;
    String[] strategyString = {"Queue Strategy", "Time Strategy"};


    public SimulationFrame() {
        JFrame mainFrame = new JFrame("Queues Management Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(400, 180, 300, 550);

        strategyBox = new JComboBox<>(strategyString);

        JPanel principalPanel = createPrincipalPanel();

        mainFrame.setContentPane(principalPanel);
        mainFrame.setVisible(true);
    }

    private JPanel createPrincipalPanel(){
        JPanel principalPanel = new JPanel();
        principalPanel.setLayout(new GridLayout(9, 1));

        addPrincipalPanelContents(principalPanel);

        return principalPanel;
    }

    private void addPrincipalPanelContents(JPanel principalPanel){
        JButton runButton = new JButton("Run");

        principalPanel.add(new JLabel("Number of clients:"));
        principalPanel.add(textFieldNumberOfClients);
        principalPanel.add(new JLabel("Number of queues:"));
        principalPanel.add(textFieldNumberOfQueues);
        principalPanel.add(new JLabel("Simulation interval:"));
        principalPanel.add(textFieldSimulationInterval);
        principalPanel.add(new JLabel("Minimum arrival time:"));
        principalPanel.add(textFieldMinArrivalTime);
        principalPanel.add(new JLabel("Maximum arrival time:"));
        principalPanel.add(textFieldMaxArrivalTime);
        principalPanel.add(new JLabel("Minimum service time:"));
        principalPanel.add(textFieldMinServiceTime);
        principalPanel.add(new JLabel("Maximum service time:"));
        principalPanel.add(textFieldMaxServiceTime);
        principalPanel.add(new JLabel("Strategy:"));
        principalPanel.add(strategyBox);
        principalPanel.add(new JLabel());
        principalPanel.add(runButton);

        buttonAction(runButton);
    }

    private void getImputsFromFrame(){
        numberOfClientsFromFrame = Integer.parseInt(textFieldNumberOfClients.getText());
        numberOfServersFromFrame = Integer.parseInt(textFieldNumberOfQueues.getText());
        timeLimitFromFrame = Integer.parseInt(textFieldSimulationInterval.getText());
        minArrivalTimeFromFrame = Integer.parseInt(textFieldMinArrivalTime.getText());
        maxArrivalTimeFromFrame = Integer.parseInt(textFieldMaxArrivalTime.getText());
        minProcessingTimeFromFrame = Integer.parseInt(textFieldMinServiceTime.getText());
        maxProcessingTimeFromFrame = Integer.parseInt(textFieldMaxServiceTime.getText());
    }

    private void buttonAction(JButton runButton){
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strategyString = (String) strategyBox.getSelectedItem();
                getImputsFromFrame();
                if(!(numberOfClientsFromFrame > 0 && numberOfServersFromFrame > 0 && timeLimitFromFrame > 0 && minArrivalTimeFromFrame > 0 && maxArrivalTimeFromFrame > 0 && minProcessingTimeFromFrame > 0 && maxProcessingTimeFromFrame > 0))
                    JOptionPane.showMessageDialog(null, "All inputs must be positive", "Invalid inputs", JOptionPane.WARNING_MESSAGE);
                else if(minArrivalTimeFromFrame > maxArrivalTimeFromFrame || minProcessingTimeFromFrame > maxProcessingTimeFromFrame)
                    JOptionPane.showMessageDialog(null, "Invalid inputs for the arrival/service time intervals", "Invalid inputs", JOptionPane.WARNING_MESSAGE);
                else {
                    if( strategyString.equals("Queue Strategy") )
                        selectionPolicyFromFrame = Scheduler.SelectionPolicy.SHORTEST_QUEUE;
                    else if( strategyString.equals("Time Strategy") )
                        selectionPolicyFromFrame = Scheduler.SelectionPolicy.SHORTEST_TIME;

                    SimulationManager simulation = null;
                    try {
                        simulation = new SimulationManager(timeLimitFromFrame, maxProcessingTimeFromFrame, minProcessingTimeFromFrame, minArrivalTimeFromFrame, maxArrivalTimeFromFrame, numberOfServersFromFrame, numberOfClientsFromFrame, selectionPolicyFromFrame);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Thread t = new Thread(simulation);
                    t.start();
                }
            }
        });
    }
}
