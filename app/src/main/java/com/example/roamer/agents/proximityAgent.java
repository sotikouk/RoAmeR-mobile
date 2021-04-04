package com.example.roamer.agents;

import com.example.roamer.ARPoint;

import java.util.List;
import java.util.logging.Level;

import jade.util.Logger;
import jade.util.leap.ArrayList;
import jade.util.leap.ArrayList.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// Δημιουργεί ειδοποιήσεις όταν ο χρήστης ειναι κοντά σε μια επιχείρηση που
// τον ενδιαφέρει. Κάνει ειδοποιήσεις και στην περίπτωση που υπάρχει προσφορά
// σε κάποιο προιόν.

public class proximityAgent extends Agent {
    private Logger logger = Logger.getJADELogger(this.getClass().getName());

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Roamer-client2b");
        sd.setName("Roamer-proximity");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new coordinatesBusiness(this));
        // registerO2AInterface(arClientInterface.class, this);

        addBehaviour(new dist2poiNotify(this));
        addBehaviour(new dist2offerNotify(this));

    }


    class coordinatesBusiness extends CyclicBehaviour implements arClientInterface {
        public ArrayList bPoints = new ArrayList();


        coordinatesBusiness(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            registerO2AInterface(arClientInterface.class, this);
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String coordinates = msg.getContent();
                String[] bcoordinates = coordinates.split(":");
                double lat = Double.parseDouble(bcoordinates[0]);
                double lon = Double.parseDouble(bcoordinates[1]);
                double alt = Double.parseDouble(bcoordinates[2]);
                String bkind = bcoordinates[3];
                logger.log(Level.INFO,"*************coordinates recieved "+" "+lat+" "+lon+" "+alt+" "+bkind+"****************");
                //setPoints(bkind, lat, lon, alt);
                ARPoint bPoint;
                bPoint = new ARPoint(bkind, lat, lon, alt);
                bPoints.add(bPoint);
                BusinessPoints();
            }
            else
                block();
        }

        @Override
        public ARPoint BusinessPoints() {
            int last = bPoints.size() - 1;
             if (last < 0)
                 return (ARPoint) bPoints.get(last);
            return null;
        }

    }

    // public void setPoints(String name, double lat, double lon, double alt) {
    //    bPoints.add(new ARPoint(name, lat, lon, alt));


    class dist2poiNotify extends CyclicBehaviour {
        dist2poiNotify(Agent a) {
            super(a);
        }

        public void action() {
        }
    }

    class dist2offerNotify extends CyclicBehaviour {
        dist2offerNotify(Agent a) {
            super(a);
        }

        public void action() {
        }
    }
}
