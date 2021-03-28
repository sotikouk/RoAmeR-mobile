package com.example.roamer.agents;

import com.example.roamer.ARPoint;
import com.example.roamer.RoAmeRView;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


// Επικοινωνεί με τον proximity agent για να καταγράψει τις προτημήσεις του χρήστη

public class clientAgent extends Agent {

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Roamer-Retail client");
        sd.setName("Roamer-client");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

    }

}
